package com.pedro.ordemservico.controller;

import com.pedro.ordemservico.dto.request.DepartamentoRequest;
import com.pedro.ordemservico.dto.response.DepartamentoResponse;
import com.pedro.ordemservico.service.DepartamentoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/departamentos")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    public DepartamentoController(DepartamentoService departamentoService) {
        this.departamentoService = departamentoService;
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponse> criar(@Valid @RequestBody DepartamentoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departamentoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponse>> listar() {
        return ResponseEntity.ok(departamentoService.listarAtivos());
    }
}
