
package com.example.demo.controller;

import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.LoginResponse;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
public class LoginController {

    private final UsuarioRepository usuarioRepository;

    public LoginController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PostMapping
    public ResponseEntity<?> login(
            @RequestBody LoginRequest datos,
            HttpSession session) {

        Usuario usuario = usuarioRepository
                .findByEmail(datos.getEmail())
                .orElse(null);

        if (usuario == null ||
                !usuario.getPassword().equals(datos.getPassword()) ||
                !usuario.isActivo()) {

            return ResponseEntity
                    .status(401)
                    .body("Credenciales incorrectas");
        }

        session.setAttribute("usuarioId", usuario.getId());

        LoginResponse respuesta = new LoginResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/actual")
    public ResponseEntity<?> usuarioActual(HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity
                    .status(401)
                    .body("No hay una sesión activa");
        }

        Usuario usuario = usuarioRepository
                .findById(usuarioId)
                .orElse(null);

        if (usuario == null || !usuario.isActivo()) {
            session.invalidate();

            return ResponseEntity
                    .status(401)
                    .body("Sesión no válida");
        }

        return ResponseEntity.ok(
                new LoginResponse(
                        usuario.getId(),
                        usuario.getNombre(),
                        usuario.getEmail(),
                        usuario.getRol()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {

        session.invalidate();

        return ResponseEntity.ok("Sesión cerrada correctamente");
    }
}
