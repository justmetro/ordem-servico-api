package com.pedro.ordemservico.service;

import com.pedro.ordemservico.dto.request.DepartamentoRequest;
import com.pedro.ordemservico.dto.response.DepartamentoResponse;
import com.pedro.ordemservico.entity.Departamento;
import com.pedro.ordemservico.exception.BusinessException;
import com.pedro.ordemservico.exception.ResourceNotFoundException;
import com.pedro.ordemservico.repository.DepartamentoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DepartamentoService {

    private final DepartamentoRepository departamentoRepository;

    public DepartamentoService(DepartamentoRepository departamentoRepository) {
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional
    public DepartamentoResponse criar(DepartamentoRequest request) {
        if (departamentoRepository.existsBySigla(request.getSigla())) {
            throw new BusinessException("Já existe um departamento com a sigla informada");
        }

        Departamento departamento = new Departamento();
        departamento.setNome(request.getNome());
        departamento.setSigla(request.getSigla());
        departamento.setAtivo(true);

        Departamento departamentoSalvo = departamentoRepository.save(departamento);
        return toResponse(departamentoSalvo);
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponse> listarAtivos() {
        return departamentoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Departamento buscarEntidadeAtivaPorId(Long id) {
        Departamento departamento = departamentoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Departamento não encontrado"));

        if (!Boolean.TRUE.equals(departamento.getAtivo())) {
            throw new ResourceNotFoundException("Departamento não encontrado");
        }

        return departamento;
    }

    private DepartamentoResponse toResponse(Departamento departamento) {
        return new DepartamentoResponse(
                departamento.getId(),
                departamento.getNome(),
                departamento.getSigla(),
                departamento.getAtivo(),
                departamento.getCriadoEm()
        );
    }
}
