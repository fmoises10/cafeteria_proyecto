package com.example.demo.controller;

import com.example.demo.model.Insumo;
import com.example.demo.repository.InsumoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/insumos")
public class InsumoController {

    private final InsumoRepository insumoRepository;

    public InsumoController(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    // REGISTRAR INSUMO
    @PostMapping
    public Insumo guardar(@RequestBody Insumo insumo) {
        return insumoRepository.save(insumo);
    }

    // LISTAR INSUMOS
    @GetMapping
    public List<Insumo> listar() {
        return insumoRepository.findAll();
    }

    // BUSCAR INSUMO
    @GetMapping("/{id}")
    public Insumo buscar(@PathVariable Long id) {
        return insumoRepository.findById(id).orElse(null);
    }

    // ACTUALIZAR INSUMO
    @PutMapping("/{id}")
    public Insumo actualizar(
            @PathVariable Long id,
            @RequestBody Insumo datos) {

        Insumo insumo = insumoRepository.findById(id).orElse(null);

        if (insumo == null) {
            return null;
        }

        insumo.setNombre(datos.getNombre());
        insumo.setUnidad(datos.getUnidad());
        insumo.setStock(datos.getStock());

        return insumoRepository.save(insumo);
    }
}