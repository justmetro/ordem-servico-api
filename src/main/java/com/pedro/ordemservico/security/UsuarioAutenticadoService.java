package com.pedro.ordemservico.security;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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

    private String emailOuSistema(String email) {
        if (email == null || email.isBlank()) {
            return USUARIO_SISTEMA;
        }
        return email;
    }
}
