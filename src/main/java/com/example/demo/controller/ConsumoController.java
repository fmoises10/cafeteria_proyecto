
package com.example.demo.controller;

import com.example.demo.model.Consumo;
import com.example.demo.repository.ConsumoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    private final ConsumoRepository consumoRepository;

    public ConsumoController(ConsumoRepository consumoRepository) {
        this.consumoRepository = consumoRepository;
    }

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Consumo consumo,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity
                    .status(401)
                    .body("Debe iniciar sesión para registrar un consumo");
        }

        consumo.setUsuarioId(usuarioId);
        consumo.setFecha(LocalDateTime.now());

        return ResponseEntity.ok(consumoRepository.save(consumo));
    }

    @GetMapping
    public List<Consumo> listar() {
        return consumoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Consumo buscar(@PathVariable Long id) {
        return consumoRepository.findById(id).orElse(null);
    }
}
