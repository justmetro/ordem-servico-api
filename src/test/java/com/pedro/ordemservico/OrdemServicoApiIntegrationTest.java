package com.pedro.ordemservico;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pedro.ordemservico.entity.Usuario;
import com.pedro.ordemservico.enums.Role;
import com.pedro.ordemservico.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class OrdemServicoApiIntegrationTest extends PostgresContainerTestBase {

    private static final String ADMIN_EMAIL = "admin@test.com";
    private static final String ADMIN_PASSWORD = "admin123";

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setUp() throws Exception {
        limparDados();
        criarUsuarioAdmin();
        adminToken = login(ADMIN_EMAIL, ADMIN_PASSWORD);
    }

    @Test
    void deveCriarUsuarioComoAdmin() throws Exception {
        Map<String, Object> request = Map.of(
                "nome", "Usuario Criado",
                "email", "usuario.criado@test.com",
                "senha", "senha123",
                "role", "SOLICITANTE"
        );

        MvcResult result = mockMvc.perform(post("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andReturn();

        assertTrue(result.getResponse().getStatus() == 201 || result.getResponse().getStatus() == 200);
        JsonNode response = toJsonNode(result);
        assertTrue("usuario.criado@test.com".equals(response.get("email").asText()));
    }

    @Test
    void deveFazerLoginERetornarJwt() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", ADMIN_EMAIL,
                                "senha", ADMIN_PASSWORD
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void deveBloquearGetDepartamentosSemToken() throws Exception {
        mockMvc.perform(get("/departamentos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("Nao autenticado"))
                .andExpect(jsonPath("$.mensagem").value("Credenciais invalidas ou ausentes"))
                .andExpect(jsonPath("$.path").value("/departamentos"))
                .andExpect(jsonPath("$.campos").doesNotExist());
    }

    @Test
    void devePermitirHealthCheckSemToken() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void deveProtegerInfoDoActuatorSemToken() throws Exception {
        mockMvc.perform(get("/actuator/info"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deveRetornarInfoDoActuatorComToken() throws Exception {
        mockMvc.perform(get("/actuator/info")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.app.name").value("Ordem de Serviço API"))
                .andExpect(jsonPath("$.app.description").value("API REST corporativa para gestão de ordens de serviço"))
                .andExpect(jsonPath("$.app.version").value("1.0.0"));
    }

    @Test
    void deveRetornarApiErrorResponseQuandoAcessoNegado() throws Exception {
        criarUsuario("Solicitante", "solicitante@test.com", "senha123", Role.SOLICITANTE);
        String solicitanteToken = login("solicitante@test.com", "senha123");

        mockMvc.perform(get("/usuarios")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + solicitanteToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("Acesso negado"))
                .andExpect(jsonPath("$.mensagem").value("Voce nao tem permissao para acessar este recurso"))
                .andExpect(jsonPath("$.path").value("/usuarios"))
                .andExpect(jsonPath("$.campos").doesNotExist());
    }

    @Test
    void deveCriarDepartamentoComoAdmin() throws Exception {
        mockMvc.perform(post("/departamentos")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "nome", "Financeiro",
                                "sigla", "FIN"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sigla").value("FIN"));
    }

    @Test
    void deveListarDepartamentosComoAdmin() throws Exception {
        criarDepartamento("Financeiro", "FIN");

        mockMvc.perform(get("/departamentos")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk());
    }

    @Test
    void deveCriarTecnicoComoAdmin() throws Exception {
        mockMvc.perform(post("/tecnicos")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "nome", "Tecnico Teste",
                                "email", "tecnico@test.com",
                                "especialidade", "Infraestrutura"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("tecnico@test.com"));
    }

    @Test
    void deveCriarOrdemServicoComoAdmin() throws Exception {
        Long departamentoId = criarDepartamento("Financeiro", "FIN");

        mockMvc.perform(post("/ordens-servico")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(ordemServicoRequest(departamentoId))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ABERTA"));
    }

    @Test
    void deveIniciarOsComoAdmin() throws Exception {
        Long departamentoId = criarDepartamento("Financeiro", "FIN");
        Long ordemServicoId = criarOrdemServico(departamentoId);

        mockMvc.perform(patch("/ordens-servico/{id}/iniciar", ordemServicoId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"));
    }

    @Test
    void deveFinalizarOsEmAndamentoComoAdmin() throws Exception {
        Long departamentoId = criarDepartamento("Financeiro", "FIN");
        Long ordemServicoId = criarOrdemServico(departamentoId);
        iniciarOrdemServico(ordemServicoId);

        mockMvc.perform(patch("/ordens-servico/{id}/finalizar", ordemServicoId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("descricaoTecnica", "Servico concluido"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FINALIZADA"));
    }

    @Test
    void deveImpedirFinalizarOsAberta() throws Exception {
        Long departamentoId = criarDepartamento("Financeiro", "FIN");
        Long ordemServicoId = criarOrdemServico(departamentoId);

        MvcResult result = mockMvc.perform(patch("/ordens-servico/{id}/finalizar", ordemServicoId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of("descricaoTecnica", "Tentativa de finalizar"))))
                .andReturn();

        assertTrue(result.getResponse().getStatus() == 400 || result.getResponse().getStatus() == 409);
    }

    @Test
    void deveConsultarHistoricoDaOs() throws Exception {
        Long departamentoId = criarDepartamento("Financeiro", "FIN");
        Long ordemServicoId = criarOrdemServico(departamentoId);
        iniciarOrdemServico(ordemServicoId);

        mockMvc.perform(get("/ordens-servico/{id}/historico", ordemServicoId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void deveConsultarMetricasComoAdmin() throws Exception {
        mockMvc.perform(get("/ordens-servico/metricas")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk());
    }

    private void limparDados() {
        jdbcTemplate.update("DELETE FROM ordens_servico_historico");
        jdbcTemplate.update("DELETE FROM ordens_servico");
        jdbcTemplate.update("DELETE FROM tecnicos");
        jdbcTemplate.update("DELETE FROM departamentos");
        jdbcTemplate.update("DELETE FROM usuarios");
    }

    private void criarUsuarioAdmin() {
        criarUsuario("Administrador", ADMIN_EMAIL, ADMIN_PASSWORD, Role.ADMIN);
    }

    private void criarUsuario(String nome, String email, String senha, Role role) {
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(senha));
        usuario.setRole(role);
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);
    }

    private String login(String email, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "email", email,
                                "senha", senha
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return toJsonNode(result).get("token").asText();
    }

    private Long criarDepartamento(String nome, String sigla) throws Exception {
        MvcResult result = mockMvc.perform(post("/departamentos")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(Map.of(
                                "nome", nome,
                                "sigla", sigla
                        ))))
                .andExpect(status().isCreated())
                .andReturn();

        return toJsonNode(result).get("id").asLong();
    }

    private Long criarOrdemServico(Long departamentoId) throws Exception {
        MvcResult result = mockMvc.perform(post("/ordens-servico")
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(ordemServicoRequest(departamentoId))))
                .andExpect(status().isCreated())
                .andReturn();

        return toJsonNode(result).get("id").asLong();
    }

    private void iniciarOrdemServico(Long ordemServicoId) throws Exception {
        mockMvc.perform(patch("/ordens-servico/{id}/iniciar", ordemServicoId)
                        .header(HttpHeaders.AUTHORIZATION, authorizationHeader()))
                .andExpect(status().isOk());
    }

    private Map<String, Object> ordemServicoRequest(Long departamentoId) {
        return Map.of(
                "titulo", "Computador nao liga",
                "descricao", "Equipamento nao apresenta sinal de energia",
                "solicitante", "Maria Silva",
                "prioridade", "ALTA",
                "departamentoId", departamentoId
        );
    }

    private String authorizationHeader() {
        return "Bearer " + adminToken;
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    private JsonNode toJsonNode(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }
}
