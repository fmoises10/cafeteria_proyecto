package com.example.demo.controller;

import com.example.demo.model.Consumo;
import com.example.demo.repository.ConsumoRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/consumos")
public class ConsumoController {

    private final ConsumoRepository consumoRepository;

    public ConsumoController(
            ConsumoRepository consumoRepository) {

        this.consumoRepository = consumoRepository;
    }

    @PostMapping
    public Consumo guardar(@RequestBody Consumo consumo) {

        consumo.setFecha(LocalDateTime.now());

        return consumoRepository.save(consumo);
    }

    @GetMapping
    public List<Consumo> listar() {

        return consumoRepository.findAll();
    }

    @GetMapping("/{id}")
    public Consumo buscar(@PathVariable Long id) {

        return consumoRepository
                .findById(id)
                .orElse(null);
    }
}