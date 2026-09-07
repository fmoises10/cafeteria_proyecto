package com.example.demo.controller;

import com.example.demo.model.DetalleVenta;
import com.example.demo.repository.DetalleVentaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/detalles-venta")
public class DetalleVentaController {

    private final DetalleVentaRepository detalleVentaRepository;

    public DetalleVentaController(
            DetalleVentaRepository detalleVentaRepository) {

        this.detalleVentaRepository = detalleVentaRepository;
    }

    @PostMapping
    public DetalleVenta guardar(@RequestBody DetalleVenta detalle) {

        detalle.setSubtotal(
                detalle.getCantidad() * detalle.getPrecio()
        );

        return detalleVentaRepository.save(detalle);
    }

    @GetMapping
    public List<DetalleVenta> listar() {
        return detalleVentaRepository.findAll();
    }

    @GetMapping("/venta/{ventaId}")
    public List<DetalleVenta> listarPorVenta(
            @PathVariable Long ventaId) {

        return detalleVentaRepository.findByVentaId(ventaId);
    }
}