package com.example.demo.controller;

import com.example.demo.model.Caja;
import com.example.demo.model.Venta;
import com.example.demo.repository.CajaRepository;
import com.example.demo.repository.VentaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

    private final VentaRepository ventaRepository;
    private final CajaRepository cajaRepository;

    public ReporteController(
            VentaRepository ventaRepository,
            CajaRepository cajaRepository) {

        this.ventaRepository = ventaRepository;
        this.cajaRepository = cajaRepository;
    }

    @GetMapping("/ventas")
    public Map<String, Object> reporteVentas() {

        List<Venta> ventas =
                ventaRepository.findAll();

        double totalVentas = ventas.stream()
                .mapToDouble(Venta::getTotal)
                .sum();

        double efectivo = ventas.stream()
                .filter(v ->
                        "EFECTIVO".equals(v.getMetodoPago()))
                .mapToDouble(Venta::getTotal)
                .sum();

        double qr = ventas.stream()
                .filter(v ->
                        "QR".equals(v.getMetodoPago()))
                .mapToDouble(Venta::getTotal)
                .sum();

        double tarjeta = ventas.stream()
                .filter(v ->
                        "TARJETA".equals(v.getMetodoPago()))
                .mapToDouble(Venta::getTotal)
                .sum();

        Map<String, Object> reporte =
                new HashMap<>();

        reporte.put("cantidadVentas", ventas.size());
        reporte.put("totalVentas", totalVentas);
        reporte.put("efectivo", efectivo);
        reporte.put("qr", qr);
        reporte.put("tarjeta", tarjeta);
        reporte.put("ventas", ventas);

        return reporte;
    }

    @GetMapping("/cajas")
    public List<Caja> reporteCajas() {

        return cajaRepository.findAll();
    }
}