package com.pedro.ordemservico.service;

import com.pedro.ordemservico.dto.request.TecnicoRequest;
import com.pedro.ordemservico.dto.response.TecnicoResponse;
import com.pedro.ordemservico.entity.Tecnico;
import com.pedro.ordemservico.entity.Usuario;
import com.pedro.ordemservico.exception.BusinessException;
import com.pedro.ordemservico.exception.ResourceNotFoundException;
import com.pedro.ordemservico.repository.TecnicoRepository;
import com.pedro.ordemservico.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final UsuarioRepository usuarioRepository;

    public TecnicoService(TecnicoRepository tecnicoRepository, UsuarioRepository usuarioRepository) {
        this.tecnicoRepository = tecnicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public TecnicoResponse criar(TecnicoRequest request) {
        if (tecnicoRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Já existe um técnico com o e-mail informado");
        }

        Tecnico tecnico = new Tecnico();
        tecnico.setNome(request.getNome());
        tecnico.setEmail(request.getEmail());
        tecnico.setEspecialidade(request.getEspecialidade());
        tecnico.setAtivo(true);

        if (request.getUsuarioId() != null) {
            Usuario usuario = buscarUsuarioAtivoPorId(request.getUsuarioId());
            tecnico.setUsuario(usuario);
        }

        Tecnico tecnicoSalvo = tecnicoRepository.save(tecnico);
        return toResponse(tecnicoSalvo);
    }

    @Transactional(readOnly = true)
    public List<TecnicoResponse> listarAtivos() {
        return tecnicoRepository.findByAtivoTrue()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Tecnico buscarEntidadeAtivaPorId(Long id) {
        Tecnico tecnico = tecnicoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico não encontrado"));

        if (!Boolean.TRUE.equals(tecnico.getAtivo())) {
            throw new ResourceNotFoundException("Técnico não encontrado");
        }

        return tecnico;
    }

    private Usuario buscarUsuarioAtivoPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!Boolean.TRUE.equals(usuario.getAtivo())) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        return usuario;
    }

    private TecnicoResponse toResponse(Tecnico tecnico) {
        Long usuarioId = tecnico.getUsuario() != null ? tecnico.getUsuario().getId() : null;

        return new TecnicoResponse(
                tecnico.getId(),
                tecnico.getNome(),
                tecnico.getEmail(),
                tecnico.getEspecialidade(),
                tecnico.getAtivo(),
                tecnico.getCriadoEm(),
                usuarioId
        );
    }
}
