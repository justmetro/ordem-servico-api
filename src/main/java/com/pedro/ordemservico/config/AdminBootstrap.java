package com.pedro.ordemservico.config;

import com.pedro.ordemservico.entity.Usuario;
import com.pedro.ordemservico.enums.Role;
import com.pedro.ordemservico.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminBootstrap.class);
    private static final String ADMIN_EMAIL = "admin@email.com";
    private static final String ADMIN_PASSWORD = "123456";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminBootstrap(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Usuario admin = usuarioRepository.findByEmail(ADMIN_EMAIL)
                .orElseGet(() -> usuarioRepository.count() == 0 ? new Usuario() : null);

        if (admin == null) {
            return;
        }

        admin.setNome("Administrador");
        admin.setEmail(ADMIN_EMAIL);
        admin.setSenha(passwordEncoder.encode(ADMIN_PASSWORD));
        admin.setRole(Role.ADMIN);
        admin.setAtivo(true);

        usuarioRepository.save(admin);
        LOGGER.info("Usuario admin inicial criado ou atualizado");
    }
}
