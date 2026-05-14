package com.pedro.ordemservico.service;

import com.pedro.ordemservico.dto.request.AtribuirTecnicoRequest;
import com.pedro.ordemservico.dto.request.CancelarOrdemServicoRequest;
import com.pedro.ordemservico.dto.request.CriarOrdemServicoRequest;
import com.pedro.ordemservico.dto.request.FinalizarOrdemServicoRequest;
import com.pedro.ordemservico.dto.response.MetricasResponse;
import com.pedro.ordemservico.dto.response.OrdemServicoHistoricoResponse;
import com.pedro.ordemservico.dto.response.OrdemServicoResponse;
import com.pedro.ordemservico.dto.response.PageResponse;
import com.pedro.ordemservico.entity.Departamento;
import com.pedro.ordemservico.entity.OrdemServico;
import com.pedro.ordemservico.entity.OrdemServicoHistorico;
import com.pedro.ordemservico.entity.Tecnico;
import com.pedro.ordemservico.enums.Prioridade;
import com.pedro.ordemservico.enums.StatusOrdemServico;
import com.pedro.ordemservico.exception.BusinessException;
import com.pedro.ordemservico.exception.ConflictException;
import com.pedro.ordemservico.exception.ResourceNotFoundException;
import com.pedro.ordemservico.repository.OrdemServicoHistoricoRepository;
import com.pedro.ordemservico.repository.OrdemServicoRepository;
import com.pedro.ordemservico.security.UsuarioAutenticadoService;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrdemServicoService {

    private static final long SLA_HORAS = 24L;

    private final OrdemServicoRepository ordemServicoRepository;
    private final OrdemServicoHistoricoRepository ordemServicoHistoricoRepository;
    private final DepartamentoService departamentoService;
    private final TecnicoService tecnicoService;
    private final UsuarioAutenticadoService usuarioAutenticadoService;

    public OrdemServicoService(OrdemServicoRepository ordemServicoRepository,
                               OrdemServicoHistoricoRepository ordemServicoHistoricoRepository,
                               DepartamentoService departamentoService,
                               TecnicoService tecnicoService,
                               UsuarioAutenticadoService usuarioAutenticadoService) {
        this.ordemServicoRepository = ordemServicoRepository;
        this.ordemServicoHistoricoRepository = ordemServicoHistoricoRepository;
        this.departamentoService = departamentoService;
        this.tecnicoService = tecnicoService;
        this.usuarioAutenticadoService = usuarioAutenticadoService;
    }

    @Transactional
    public OrdemServicoResponse criar(CriarOrdemServicoRequest request) {
        Departamento departamento = departamentoService.buscarEntidadeAtivaPorId(request.getDepartamentoId());
        Tecnico tecnico = null;
        if (request.getTecnicoId() != null) {
            tecnico = tecnicoService.buscarEntidadeAtivaPorId(request.getTecnicoId());
        }

        OrdemServico ordemServico = new OrdemServico();
        ordemServico.setTitulo(request.getTitulo());
        ordemServico.setDescricao(request.getDescricao());
        ordemServico.setSolicitante(request.getSolicitante());
        ordemServico.setPrioridade(request.getPrioridade());
        ordemServico.setDepartamento(departamento);
        ordemServico.setTecnico(tecnico);
        ordemServico.setStatus(StatusOrdemServico.ABERTA);
        ordemServico.setDataAbertura(LocalDateTime.now());
        ordemServico.setAtivo(true);

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        registrarHistorico(ordemServicoSalva, null, StatusOrdemServico.ABERTA,
                usuarioAutenticadoService.getEmailUsuarioAtual(), null);

        return toResponse(ordemServicoSalva);
    }

    @Transactional(readOnly = true)
    public OrdemServicoResponse buscarPorId(Long id) {
        return toResponse(buscarEntidadeAtivaPorId(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<OrdemServicoResponse> listar(StatusOrdemServico status,
                                                     Prioridade prioridade,
                                                     Long tecnicoId,
                                                     Long departamentoId,
                                                     LocalDate dataInicio,
                                                     LocalDate dataFim,
                                                     Pageable pageable) {
        LocalDateTime dataInicioFiltro = dataInicio != null ? dataInicio.atStartOfDay() : null;
        LocalDateTime dataFimFiltro = dataFim != null ? dataFim.plusDays(1).atStartOfDay().minusNanos(1) : null;

        Page<OrdemServico> page = ordemServicoRepository.findAll(
                filtrarAtivas(status, prioridade, tecnicoId, departamentoId, dataInicioFiltro, dataFimFiltro),
                pageable
        );

        List<OrdemServicoResponse> content = page.getContent()
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }

    private Specification<OrdemServico> filtrarAtivas(StatusOrdemServico status,
                                                      Prioridade prioridade,
                                                      Long tecnicoId,
                                                      Long departamentoId,
                                                      LocalDateTime dataInicio,
                                                      LocalDateTime dataFim) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isTrue(root.get("ativo")));

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            if (prioridade != null) {
                predicates.add(criteriaBuilder.equal(root.get("prioridade"), prioridade));
            }
            if (tecnicoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("tecnico").get("id"), tecnicoId));
            }
            if (departamentoId != null) {
                predicates.add(criteriaBuilder.equal(root.get("departamento").get("id"), departamentoId));
            }
            if (dataInicio != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("dataAbertura"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("dataAbertura"), dataFim));
            }

            return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
        };
    }

    @Transactional
    public OrdemServicoResponse atribuirTecnico(Long id, AtribuirTecnicoRequest request) {
        OrdemServico ordemServico = buscarEntidadeAtivaPorId(id);
        impedirAlteracaoEncerrada(ordemServico);

        Tecnico tecnico = tecnicoService.buscarEntidadeAtivaPorId(request.getTecnicoId());
        String emailUsuarioAtual = usuarioAutenticadoService.getEmailUsuarioAtual();

        ordemServico.setTecnico(tecnico);
        ordemServico.setAtualizadoPor(emailUsuarioAtual);

        return toResponse(ordemServicoRepository.save(ordemServico));
    }

    @Transactional
    public OrdemServicoResponse iniciar(Long id) {
        OrdemServico ordemServico = buscarEntidadeAtivaPorId(id);
        validarPodeIniciar(ordemServico);
        String emailUsuarioAtual = usuarioAutenticadoService.getEmailUsuarioAtual();

        ordemServico.setDataInicio(LocalDateTime.now());
        ordemServico.setStatus(StatusOrdemServico.EM_ANDAMENTO);
        ordemServico.setAtualizadoPor(emailUsuarioAtual);

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        registrarHistorico(ordemServicoSalva, StatusOrdemServico.ABERTA, StatusOrdemServico.EM_ANDAMENTO,
                emailUsuarioAtual, null);

        return toResponse(ordemServicoSalva);
    }

    @Transactional
    public OrdemServicoResponse finalizar(Long id, FinalizarOrdemServicoRequest request) {
        OrdemServico ordemServico = buscarEntidadeAtivaPorId(id);
        validarPodeFinalizar(ordemServico);

        if (request.getDescricaoTecnica() == null || request.getDescricaoTecnica().isBlank()) {
            throw new BusinessException("A descrição técnica é obrigatória para finalizar a ordem de serviço");
        }

        String emailUsuarioAtual = usuarioAutenticadoService.getEmailUsuarioAtual();

        ordemServico.setDescricaoTecnica(request.getDescricaoTecnica());
        ordemServico.setDataFinalizacao(LocalDateTime.now());
        ordemServico.setStatus(StatusOrdemServico.FINALIZADA);
        ordemServico.setAtualizadoPor(emailUsuarioAtual);

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        registrarHistorico(ordemServicoSalva, StatusOrdemServico.EM_ANDAMENTO, StatusOrdemServico.FINALIZADA,
                emailUsuarioAtual, null);

        return toResponse(ordemServicoSalva);
    }

    @Transactional
    public OrdemServicoResponse cancelar(Long id, CancelarOrdemServicoRequest request) {
        OrdemServico ordemServico = buscarEntidadeAtivaPorId(id);
        validarPodeCancelar(ordemServico);

        StatusOrdemServico statusAnterior = ordemServico.getStatus();
        String observacao = request != null ? request.getMotivo() : null;
        String emailUsuarioAtual = usuarioAutenticadoService.getEmailUsuarioAtual();

        ordemServico.setStatus(StatusOrdemServico.CANCELADA);
        ordemServico.setAtualizadoPor(emailUsuarioAtual);

        OrdemServico ordemServicoSalva = ordemServicoRepository.save(ordemServico);
        registrarHistorico(ordemServicoSalva, statusAnterior, StatusOrdemServico.CANCELADA,
                emailUsuarioAtual, observacao);

        return toResponse(ordemServicoSalva);
    }

    @Transactional(readOnly = true)
    public List<OrdemServicoHistoricoResponse> listarHistorico(Long ordemServicoId) {
        buscarEntidadeAtivaPorId(ordemServicoId);

        return ordemServicoHistoricoRepository.findByOrdemServicoIdOrderByAlteradoEmAsc(ordemServicoId)
                .stream()
                .map(this::toHistoricoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public MetricasResponse consultarMetricas() {
        List<OrdemServico> ordensAtivas = ordemServicoRepository.findByAtivoTrue();
        LocalDateTime ultimos30Dias = LocalDateTime.now().minusDays(30);

        Map<String, Long> totalPorStatus = agruparPorStatus(ordensAtivas);
        Map<String, Long> totalPorPrioridade = agruparPorPrioridade(ordensAtivas);
        Map<String, Long> totalPorTecnico = agruparPorTecnico(ordensAtivas);
        Double tempoMedioResolucaoHoras = mediaHoras(ordensAtivas, true);
        Long ordensAbertasUltimos30Dias = ordensAtivas.stream()
                .filter(ordem -> ordem.getDataAbertura() != null)
                .filter(ordem -> !ordem.getDataAbertura().isBefore(ultimos30Dias))
                .count();
        Double tempoMedioFilaHoras = mediaHorasFila(ordensAtivas);
        Double percentualSlaCumprido = calcularPercentualSlaCumprido(ordensAtivas);
        Map<String, Long> cargaPorTecnico = agruparCargaPorTecnico(ordensAtivas);

        return new MetricasResponse(
                totalPorStatus,
                totalPorPrioridade,
                totalPorTecnico,
                tempoMedioResolucaoHoras,
                ordensAbertasUltimos30Dias,
                tempoMedioFilaHoras,
                percentualSlaCumprido,
                cargaPorTecnico
        );
    }

    private OrdemServico buscarEntidadeAtivaPorId(Long id) {
        OrdemServico ordemServico = ordemServicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ordem de serviço não encontrada"));

        if (!Boolean.TRUE.equals(ordemServico.getAtivo())) {
            throw new ResourceNotFoundException("Ordem de serviço não encontrada");
        }

        return ordemServico;
    }

    private void validarPodeIniciar(OrdemServico ordemServico) {
        if (ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new ConflictException("Não é possível iniciar uma ordem de serviço cancelada");
        }
        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new ConflictException("Não é possível iniciar uma ordem de serviço finalizada");
        }
        if (ordemServico.getStatus() != StatusOrdemServico.ABERTA) {
            throw new BusinessException("A ordem de serviço só pode ser iniciada quando estiver ABERTA");
        }
    }

    private void validarPodeFinalizar(OrdemServico ordemServico) {
        if (ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new ConflictException("Não é possível finalizar uma ordem de serviço cancelada");
        }
        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new ConflictException("A ordem de serviço já está finalizada");
        }
        if (ordemServico.getStatus() != StatusOrdemServico.EM_ANDAMENTO) {
            throw new BusinessException("A ordem de serviço só pode ser finalizada quando estiver EM_ANDAMENTO");
        }
    }

    private void validarPodeCancelar(OrdemServico ordemServico) {
        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new ConflictException("Não é possível cancelar uma ordem de serviço finalizada");
        }
        if (ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new ConflictException("A ordem de serviço já está cancelada");
        }
    }

    private void impedirAlteracaoEncerrada(OrdemServico ordemServico) {
        if (ordemServico.getStatus() == StatusOrdemServico.FINALIZADA) {
            throw new ConflictException("Não é possível alterar uma ordem de serviço finalizada");
        }
        if (ordemServico.getStatus() == StatusOrdemServico.CANCELADA) {
            throw new ConflictException("Não é possível alterar uma ordem de serviço cancelada");
        }
    }

    private void registrarHistorico(OrdemServico ordemServico, StatusOrdemServico statusAnterior,
                                    StatusOrdemServico statusNovo, String alteradoPor, String observacao) {
        OrdemServicoHistorico historico = new OrdemServicoHistorico();
        historico.setOrdemServico(ordemServico);
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(statusNovo);
        historico.setAlteradoPor(alteradoPor);
        historico.setObservacao(observacao);

        ordemServicoHistoricoRepository.save(historico);
    }

    private OrdemServicoResponse toResponse(OrdemServico ordemServico) {
        Departamento departamento = ordemServico.getDepartamento();
        Tecnico tecnico = ordemServico.getTecnico();

        return new OrdemServicoResponse(
                ordemServico.getId(),
                ordemServico.getTitulo(),
                ordemServico.getDescricao(),
                ordemServico.getSolicitante(),
                ordemServico.getPrioridade(),
                ordemServico.getStatus(),
                departamento != null ? departamento.getId() : null,
                departamento != null ? departamento.getNome() : null,
                tecnico != null ? tecnico.getId() : null,
                tecnico != null ? tecnico.getNome() : null,
                ordemServico.getDataAbertura(),
                ordemServico.getDataInicio(),
                ordemServico.getDataFinalizacao(),
                ordemServico.getDescricaoTecnica(),
                ordemServico.getAtualizadoEm(),
                ordemServico.getAtualizadoPor(),
                ordemServico.getAtivo(),
                ordemServico.getVersion(),
                calcularTempoResolucaoHoras(ordemServico)
        );
    }

    private OrdemServicoHistoricoResponse toHistoricoResponse(OrdemServicoHistorico historico) {
        OrdemServico ordemServico = historico.getOrdemServico();

        return new OrdemServicoHistoricoResponse(
                historico.getId(),
                ordemServico != null ? ordemServico.getId() : null,
                historico.getStatusAnterior(),
                historico.getStatusNovo(),
                historico.getAlteradoPor(),
                historico.getObservacao(),
                historico.getAlteradoEm()
        );
    }

    private Double calcularTempoResolucaoHoras(OrdemServico ordemServico) {
        if (ordemServico.getDataInicio() == null || ordemServico.getDataFinalizacao() == null) {
            return null;
        }
        return toHoras(Duration.between(ordemServico.getDataInicio(), ordemServico.getDataFinalizacao()));
    }

    private Map<String, Long> agruparPorStatus(List<OrdemServico> ordens) {
        return ordens.stream()
                .filter(ordem -> ordem.getStatus() != null)
                .collect(Collectors.groupingBy(
                        ordem -> ordem.getStatus().name(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> agruparPorPrioridade(List<OrdemServico> ordens) {
        return ordens.stream()
                .filter(ordem -> ordem.getPrioridade() != null)
                .collect(Collectors.groupingBy(
                        ordem -> ordem.getPrioridade().name(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> agruparPorTecnico(List<OrdemServico> ordens) {
        return ordens.stream()
                .filter(ordem -> ordem.getTecnico() != null)
                .collect(Collectors.groupingBy(
                        ordem -> ordem.getTecnico().getNome(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Map<String, Long> agruparCargaPorTecnico(List<OrdemServico> ordens) {
        return ordens.stream()
                .filter(ordem -> ordem.getTecnico() != null)
                .filter(ordem -> ordem.getStatus() == StatusOrdemServico.ABERTA
                        || ordem.getStatus() == StatusOrdemServico.EM_ANDAMENTO)
                .collect(Collectors.groupingBy(
                        ordem -> ordem.getTecnico().getNome(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
    }

    private Double mediaHoras(List<OrdemServico> ordens, boolean usarInicioComoBase) {
        List<Double> horas = ordens.stream()
                .filter(ordem -> ordem.getDataFinalizacao() != null)
                .map(ordem -> {
                    LocalDateTime inicio = usarInicioComoBase ? ordem.getDataInicio() : ordem.getDataAbertura();
                    if (inicio == null) {
                        return null;
                    }
                    return toHoras(Duration.between(inicio, ordem.getDataFinalizacao()));
                })
                .filter(Objects::nonNull)
                .toList();

        return calcularMedia(horas);
    }

    private Double mediaHorasFila(List<OrdemServico> ordens) {
        List<Double> horas = ordens.stream()
                .filter(ordem -> ordem.getDataAbertura() != null && ordem.getDataInicio() != null)
                .map(ordem -> toHoras(Duration.between(ordem.getDataAbertura(), ordem.getDataInicio())))
                .toList();

        return calcularMedia(horas);
    }

    private Double calcularPercentualSlaCumprido(List<OrdemServico> ordens) {
        List<OrdemServico> finalizadas = ordens.stream()
                .filter(ordem -> ordem.getDataAbertura() != null && ordem.getDataFinalizacao() != null)
                .toList();

        if (finalizadas.isEmpty()) {
            return 0.0;
        }

        long dentroDoSla = finalizadas.stream()
                .filter(ordem -> Duration.between(ordem.getDataAbertura(), ordem.getDataFinalizacao()).toHours() <= SLA_HORAS)
                .count();

        return dentroDoSla * 100.0 / finalizadas.size();
    }

    private Double calcularMedia(List<Double> valores) {
        if (valores.isEmpty()) {
            return 0.0;
        }
        return valores.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private Double toHoras(Duration duration) {
        return duration.toMinutes() / 60.0;
    }
}
