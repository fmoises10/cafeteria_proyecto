package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // CREAR
    @PostMapping
    public Usuario guardar(@RequestBody Usuario usuario) {
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    // LISTAR
    @GetMapping
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // BUSCAR
    @GetMapping("/{id}")
    public Usuario buscar(@PathVariable Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    // ACTUALIZAR
    @PutMapping("/{id}")
    public Usuario actualizar(
            @PathVariable Long id,
            @RequestBody Usuario datos) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return null;
        }

        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setRol(datos.getRol());

        return usuarioRepository.save(usuario);
    }

    // DESACTIVAR
    @PutMapping("/{id}/desactivar")
    public Usuario desactivar(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return null;
        }

        usuario.setActivo(false);

        return usuarioRepository.save(usuario);
    }

    // REACTIVAR
    @PutMapping("/{id}/activar")
    public Usuario activar(@PathVariable Long id) {

        Usuario usuario = usuarioRepository.findById(id).orElse(null);

        if (usuario == null) {
            return null;
        }

        usuario.setActivo(true);

        return usuarioRepository.save(usuario);
    }
}