package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public Usuario login(@RequestBody LoginRequest datos) {

        Usuario usuario = usuarioRepository
                .findByEmail(datos.getEmail())
                .orElse(null);

        if (usuario == null) {
            return null;
        }

        if (!usuario.getPassword().equals(datos.getPassword())) {
            return null;
        }

        if (!usuario.isActivo()) {
            return null;
        }

        return usuario;
    }
}