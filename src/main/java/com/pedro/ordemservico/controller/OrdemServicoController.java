package com.pedro.ordemservico.controller;

import com.pedro.ordemservico.dto.request.AtribuirTecnicoRequest;
import com.pedro.ordemservico.dto.request.CancelarOrdemServicoRequest;
import com.pedro.ordemservico.dto.request.CriarOrdemServicoRequest;
import com.pedro.ordemservico.dto.request.FinalizarOrdemServicoRequest;
import com.pedro.ordemservico.dto.response.MetricasResponse;
import com.pedro.ordemservico.dto.response.OrdemServicoHistoricoResponse;
import com.pedro.ordemservico.dto.response.OrdemServicoResponse;
import com.pedro.ordemservico.dto.response.PageResponse;
import com.pedro.ordemservico.enums.Prioridade;
import com.pedro.ordemservico.enums.StatusOrdemServico;
import com.pedro.ordemservico.exception.BusinessException;
import com.pedro.ordemservico.service.OrdemServicoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final String DEFAULT_SORT = "dataAbertura,desc";
    private static final int MIN_PAGE_SIZE = 1;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> CAMPOS_SORT_PERMITIDOS = Set.of("dataAbertura", "prioridade", "status", "id");

    private final OrdemServicoService ordemServicoService;

    public OrdemServicoController(OrdemServicoService ordemServicoService) {
        this.ordemServicoService = ordemServicoService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'SOLICITANTE')")
    public ResponseEntity<OrdemServicoResponse> criar(@Valid @RequestBody CriarOrdemServicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ordemServicoService.criar(request));
    }

    @GetMapping("/metricas")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MetricasResponse> consultarMetricas() {
        return ResponseEntity.ok(ordemServicoService.consultarMetricas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO', 'SOLICITANTE')")
    public ResponseEntity<OrdemServicoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.buscarPorId(id));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO', 'SOLICITANTE')")
    public ResponseEntity<PageResponse<OrdemServicoResponse>> listar(
            @RequestParam(required = false) StatusOrdemServico status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort) {
        int pageNormalizado = page != null ? page : DEFAULT_PAGE;
        int sizeNormalizado = size != null ? size : DEFAULT_PAGE_SIZE;
        String sortNormalizado = normalizarSort(sort);

        validarPaginacao(pageNormalizado, sizeNormalizado);
        validarIntervaloDatas(dataInicio, dataFim);
        Pageable pageable = criarPageable(pageNormalizado, sizeNormalizado, sortNormalizado);
        return ResponseEntity.ok(ordemServicoService.listar(
                status,
                prioridade,
                tecnicoId,
                departamentoId,
                dataInicio,
                dataFim,
                pageable
        ));
    }

    @PatchMapping("/{id}/atribuir-tecnico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrdemServicoResponse> atribuirTecnico(@PathVariable Long id,
                                                                @Valid @RequestBody AtribuirTecnicoRequest request) {
        return ResponseEntity.ok(ordemServicoService.atribuirTecnico(id, request));
    }

    @PatchMapping("/{id}/iniciar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<OrdemServicoResponse> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.iniciar(id));
    }

    @PatchMapping("/{id}/finalizar")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<OrdemServicoResponse> finalizar(@PathVariable Long id,
                                                          @Valid @RequestBody FinalizarOrdemServicoRequest request) {
        return ResponseEntity.ok(ordemServicoService.finalizar(id, request));
    }

    @PatchMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ADMIN', 'SOLICITANTE')")
    public ResponseEntity<OrdemServicoResponse> cancelar(@PathVariable Long id,
                                                         @Valid @RequestBody CancelarOrdemServicoRequest request) {
        return ResponseEntity.ok(ordemServicoService.cancelar(id, request));
    }

    @GetMapping("/{id}/historico")
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<List<OrdemServicoHistoricoResponse>> listarHistorico(@PathVariable Long id) {
        return ResponseEntity.ok(ordemServicoService.listarHistorico(id));
    }

    private void validarPaginacao(int page, int size) {
        if (page < 0) {
            throw new BusinessException("O parâmetro page não pode ser negativo");
        }

        if (size < MIN_PAGE_SIZE || size > MAX_PAGE_SIZE) {
            throw new BusinessException("O parâmetro size deve estar entre " + MIN_PAGE_SIZE + " e " + MAX_PAGE_SIZE);
        }
    }

    private void validarIntervaloDatas(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null && dataInicio.isAfter(dataFim)) {
            throw new BusinessException("O parâmetro dataInicio não pode ser depois de dataFim");
        }
    }

    private Pageable criarPageable(int page, int size, String sort) {
        try {
            return PageRequest.of(page, size, toSort(sort));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Parâmetros de paginação inválidos", exception);
        }
    }

    private String normalizarSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return DEFAULT_SORT;
        }
        return sort.trim();
    }

    private Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            sort = DEFAULT_SORT;
        }

        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isBlank()) {
            property = "dataAbertura";
        }
        if (!CAMPOS_SORT_PERMITIDOS.contains(property)) {
            throw new BusinessException("O parâmetro sort possui campo inválido");
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1) {
            String directionParam = parts[1].trim();
            if ("desc".equalsIgnoreCase(directionParam)) {
                direction = Sort.Direction.DESC;
            } else if (!"asc".equalsIgnoreCase(directionParam)) {
                throw new BusinessException("O parâmetro sort possui direção inválida");
            }
        }

        return Sort.by(direction, property);
    }
}
