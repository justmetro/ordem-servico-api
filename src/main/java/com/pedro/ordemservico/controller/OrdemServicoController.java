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

@RestController
@RequestMapping("/ordens-servico")
public class OrdemServicoController {

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
    @PreAuthorize("hasAnyRole('ADMIN', 'TECNICO')")
    public ResponseEntity<PageResponse<OrdemServicoResponse>> listar(
            @RequestParam(required = false) StatusOrdemServico status,
            @RequestParam(required = false) Prioridade prioridade,
            @RequestParam(required = false) Long tecnicoId,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "dataAbertura,desc") String sort) {
        Pageable pageable = PageRequest.of(page, size, toSort(sort));
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

    private Sort toSort(String sort) {
        if (sort == null || sort.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "dataAbertura");
        }

        String[] parts = sort.split(",", 2);
        String property = parts[0].trim();
        if (property.isBlank()) {
            property = "dataAbertura";
        }

        Sort.Direction direction = Sort.Direction.ASC;
        if (parts.length > 1 && "desc".equalsIgnoreCase(parts[1].trim())) {
            direction = Sort.Direction.DESC;
        }

        return Sort.by(direction, property);
    }
}
