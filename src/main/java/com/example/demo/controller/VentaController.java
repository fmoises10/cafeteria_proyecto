package com.example.demo.controller;

import com.example.demo.model.Venta;
import com.example.demo.repository.VentaRepository;
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
    public Venta guardar(@RequestBody Venta venta) {

        venta.setFecha(LocalDateTime.now());

        return ventaRepository.save(venta);
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