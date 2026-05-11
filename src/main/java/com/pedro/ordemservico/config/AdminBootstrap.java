package com.pedro.ordemservico.config;

import com.pedro.ordemservico.entity.Usuario;
import com.pedro.ordemservico.enums.Role;
import com.pedro.ordemservico.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminNome;
    private final String adminEmail;
    private final String adminSenha;

    public AdminBootstrap(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          @Value("${app.bootstrap.admin.nome}") String adminNome,
                          @Value("${app.bootstrap.admin.email}") String adminEmail,
                          @Value("${app.bootstrap.admin.senha}") String adminSenha) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminNome = adminNome;
        this.adminEmail = adminEmail;
        this.adminSenha = adminSenha;
    }

    @Override
    public void run(String... args) {
        Usuario admin = usuarioRepository.findByEmail(adminEmail)
                .orElseGet(Usuario::new);

        admin.setNome(adminNome);
        admin.setEmail(adminEmail);
        admin.setSenha(passwordEncoder.encode(adminSenha));
        admin.setRole(Role.ADMIN);
        admin.setAtivo(true);

        usuarioRepository.save(admin);
        LOGGER.info("Usuario admin inicial criado ou atualizado");
    }
}
