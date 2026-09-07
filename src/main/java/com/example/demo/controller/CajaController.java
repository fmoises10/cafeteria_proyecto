package com.example.demo.controller;

import com.example.demo.model.Caja;
import com.example.demo.model.Venta;
import com.example.demo.repository.CajaRepository;
import com.example.demo.repository.VentaRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/cajas")
public class CajaController {

    private final CajaRepository cajaRepository;
    private final VentaRepository ventaRepository;

    public CajaController(
            CajaRepository cajaRepository,
            VentaRepository ventaRepository) {

        this.cajaRepository = cajaRepository;
        this.ventaRepository = ventaRepository;
    }

    @GetMapping("/actual")
    public Caja cajaActual() {

        Caja caja = cajaRepository
                .findFirstByAbiertaTrue()
                .orElse(null);

        if (caja == null) {
            return null;
        }

        double ventasEfectivo = ventaRepository.findAll()
                .stream()
                .filter(v ->
                        v.getMetodoPago() != null &&
                        v.getMetodoPago().equals("EFECTIVO") &&
                        v.getFecha() != null &&
                        caja.getFechaApertura() != null &&
                        v.getFecha().isAfter(caja.getFechaApertura()))
                .mapToDouble(Venta::getTotal)
                .sum();

        double efectivoEsperado =
                caja.getMontoInicial() + ventasEfectivo;

        caja.setEfectivoEsperado(efectivoEsperado);

        return caja;
    }

    @PostMapping("/abrir")
    public Caja abrirCaja(@RequestParam double montoInicial) {

        Caja cajaAbierta = cajaRepository
                .findFirstByAbiertaTrue()
                .orElse(null);

        if (cajaAbierta != null) {
            return cajaAbierta;
        }

        Caja caja = new Caja();

        caja.setMontoInicial(montoInicial);
        caja.setMontoFinal(0);
        caja.setEfectivoEsperado(montoInicial);
        caja.setDiferencia(0);
        caja.setFechaApertura(LocalDateTime.now());
        caja.setAbierta(true);

        return cajaRepository.save(caja);
    }

    @PostMapping("/cerrar")
    public Caja cerrarCaja(@RequestParam double montoFinal) {

        Caja caja = cajaRepository
                .findFirstByAbiertaTrue()
                .orElse(null);

        if (caja == null) {
            return null;
        }

        List<Venta> ventas = ventaRepository.findAll();

        double ventasEfectivo = ventas.stream()
                .filter(v ->
                        v.getMetodoPago() != null &&
                        v.getMetodoPago().equals("EFECTIVO") &&
                        v.getFecha() != null &&
                        caja.getFechaApertura() != null &&
                        v.getFecha().isAfter(caja.getFechaApertura()))
                .mapToDouble(Venta::getTotal)
                .sum();

        double efectivoEsperado =
                caja.getMontoInicial() + ventasEfectivo;

        double diferencia =
                montoFinal - efectivoEsperado;

        caja.setEfectivoEsperado(efectivoEsperado);
        caja.setMontoFinal(montoFinal);
        caja.setDiferencia(diferencia);
        caja.setFechaCierre(LocalDateTime.now());
        caja.setAbierta(false);

        return cajaRepository.save(caja);
    }

    @GetMapping
    public List<Caja> listar() {
        return cajaRepository.findAll();
    }
}