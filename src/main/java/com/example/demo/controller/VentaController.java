
package com.example.demo.controller;

import com.example.demo.model.Venta;
import com.example.demo.repository.VentaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    private final VentaRepository ventaRepository;

    public VentaController(VentaRepository ventaRepository) {
        this.ventaRepository = ventaRepository;
    }

    @PostMapping
    public ResponseEntity<?> guardar(
            @RequestBody Venta venta,
            HttpSession session) {

        Long usuarioId = (Long) session.getAttribute("usuarioId");

        if (usuarioId == null) {
            return ResponseEntity
                    .status(401)
                    .body("Debe iniciar sesión para registrar una venta");
        }

        venta.setUsuarioId(usuarioId);
        venta.setFecha(LocalDateTime.now());

        Venta ventaGuardada = ventaRepository.save(venta);

        return ResponseEntity.ok(ventaGuardada);
    }

    @GetMapping
    public List<Venta> listar() {
        return ventaRepository.findAll();
    }

    @GetMapping("/{id}")
    public Venta buscar(@PathVariable Long id) {
        return ventaRepository.findById(id).orElse(null);
    }
}
