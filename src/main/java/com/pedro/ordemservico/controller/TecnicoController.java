package com.pedro.ordemservico.controller;

import com.pedro.ordemservico.dto.request.TecnicoRequest;
import com.pedro.ordemservico.dto.response.TecnicoResponse;
import com.pedro.ordemservico.service.TecnicoService;
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
@RequestMapping("/tecnicos")
public class TecnicoController {

    private final TecnicoService tecnicoService;

    public TecnicoController(TecnicoService tecnicoService) {
        this.tecnicoService = tecnicoService;
    }

    @PostMapping
    public ResponseEntity<TecnicoResponse> criar(@Valid @RequestBody TecnicoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tecnicoService.criar(request));
    }

    @GetMapping
    public ResponseEntity<List<TecnicoResponse>> listar() {
        return ResponseEntity.ok(tecnicoService.listarAtivos());
    }
}
