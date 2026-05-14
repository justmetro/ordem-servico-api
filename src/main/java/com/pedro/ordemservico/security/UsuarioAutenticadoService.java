package com.pedro.ordemservico.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class UsuarioAutenticadoService {

    private static final String USUARIO_SISTEMA = "sistema";

    public String getEmailUsuarioAtual() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return USUARIO_SISTEMA;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return emailOuSistema(userDetails.getUsername());
        }
        if (principal instanceof String email) {
            return emailOuSistema(email);
        }

        return emailOuSistema(authentication.getName());
    }

    public boolean possuiRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String roleNormalizada = normalizarRole(role);
        return authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(roleNormalizada::equals);
    }

    public boolean isAdmin() {
        return possuiRole("ADMIN");
    }

    public boolean isTecnico() {
        return possuiRole("TECNICO");
    }

    public boolean isSolicitante() {
        return possuiRole("SOLICITANTE");
    }

    private String emailOuSistema(String email) {
        if (email == null || email.isBlank()) {
            return USUARIO_SISTEMA;
        }
        return email;
    }

    private String normalizarRole(String role) {
        if (role == null || role.isBlank()) {
            return "";
        }
        String roleNormalizada = role.trim().toUpperCase();
        if (!roleNormalizada.startsWith("ROLE_")) {
            roleNormalizada = "ROLE_" + roleNormalizada;
        }
        return roleNormalizada;
    }
}
