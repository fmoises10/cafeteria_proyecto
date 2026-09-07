package com.example.demo.controller;

import com.example.demo.model.Contenedor;
import com.example.demo.model.Consumo;
import com.example.demo.model.Insumo;
import com.example.demo.repository.ContenedorRepository;
import com.example.demo.repository.ConsumoRepository;
import com.example.demo.repository.InsumoRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/contenedores")
public class ContenedorController {

    private final ContenedorRepository contenedorRepository;
    private final InsumoRepository insumoRepository;
    private final ConsumoRepository consumoRepository;

    public ContenedorController(
            ContenedorRepository contenedorRepository,
            InsumoRepository insumoRepository,
            ConsumoRepository consumoRepository) {

        this.contenedorRepository = contenedorRepository;
        this.insumoRepository = insumoRepository;
        this.consumoRepository = consumoRepository;
    }

    @PostMapping
    public Contenedor guardar(@RequestBody Contenedor contenedor) {

        return contenedorRepository.save(contenedor);
    }

    @GetMapping
    public List<Contenedor> listar() {

        return contenedorRepository.findAll();
    }

    @GetMapping("/{id}")
    public Contenedor buscar(@PathVariable Long id) {

        return contenedorRepository
                .findById(id)
                .orElse(null);
    }

    @PutMapping("/{id}")
    public Contenedor actualizar(
            @PathVariable Long id,
            @RequestBody Contenedor datos) {

        Contenedor contenedor =
                contenedorRepository
                        .findById(id)
                        .orElse(null);

        if (contenedor == null) {
            return null;
        }

        contenedor.setCodigoBarras(
                datos.getCodigoBarras()
        );

        contenedor.setCapacidadMaxima(
                datos.getCapacidadMaxima()
        );

        contenedor.setSaldo(
                datos.getSaldo()
        );

        contenedor.setDosis(
                datos.getDosis()
        );

        contenedor.setInsumoId(
                datos.getInsumoId()
        );

        return contenedorRepository.save(contenedor);
    }

    @PostMapping("/consumir")
    public String consumir(
            @RequestParam String codigoBarras) {

        Contenedor contenedor =
                contenedorRepository
                        .findAll()
                        .stream()
                        .filter(c ->
                                c.getCodigoBarras() != null &&
                                c.getCodigoBarras()
                                        .equals(codigoBarras))
                        .findFirst()
                        .orElse(null);

        if (contenedor == null) {

            return "Código de barras no encontrado.";
        }

        if (contenedor.getSaldo()
                < contenedor.getDosis()) {

            return "Saldo insuficiente en el contenedor.";
        }

        Insumo insumo =
                insumoRepository
                        .findById(
                                contenedor.getInsumoId()
                        )
                        .orElse(null);

        if (insumo == null) {

            return "El insumo asociado no existe.";
        }

        if (insumo.getStock()
                < contenedor.getDosis()) {

            return "Stock general insuficiente.";
        }

        contenedor.setSaldo(
                contenedor.getSaldo()
                        - contenedor.getDosis()
        );

        insumo.setStock(
                insumo.getStock()
                        - contenedor.getDosis()
        );

        contenedorRepository.save(contenedor);

        insumoRepository.save(insumo);

        Consumo consumo = new Consumo();

        consumo.setContenedorId(
                contenedor.getId()
        );

        consumo.setInsumoId(
                insumo.getId()
        );

        consumo.setCantidad(
                contenedor.getDosis()
        );

        consumo.setFecha(
                LocalDateTime.now()
        );

        consumoRepository.save(consumo);

        return "Consumo realizado correctamente.";
    }
}