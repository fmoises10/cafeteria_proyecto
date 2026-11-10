
package com.example.demo.controller;

import com.example.demo.model.Caja;
import com.example.demo.model.Usuario;
import com.example.demo.model.Venta;
import com.example.demo.repository.CajaRepository;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.repository.VentaRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cajas")
public class CajaController {

        private final CajaRepository cajaRepository;
        private final VentaRepository ventaRepository;
        private final UsuarioRepository usuarioRepository;

        public CajaController(
                        CajaRepository cajaRepository,
                        VentaRepository ventaRepository,
                        UsuarioRepository usuarioRepository) {

                this.cajaRepository = cajaRepository;
                this.ventaRepository = ventaRepository;
                this.usuarioRepository = usuarioRepository;
        }

        private List<Venta> obtenerVentasPeriodo(Caja caja) {

                return ventaRepository.findAll()
                                .stream()
                                .filter(v -> v.getFecha() != null
                                                && !v.getFecha().isBefore(caja.getFechaApertura())
                                                && (caja.getFechaCierre() == null
                                                                || !v.getFecha().isAfter(caja.getFechaCierre())))
                                .toList();
        }

        private double calcularEfectivoEsperado(Caja caja) {

                if (caja.getFechaApertura() == null) {
                        return caja.getMontoInicial();
                }

                double ventasEfectivo = obtenerVentasPeriodo(caja)
                                .stream()
                                .filter(v -> "EFECTIVO".equalsIgnoreCase(v.getMetodoPago()))
                                .mapToDouble(Venta::getTotal)
                                .sum();

                return caja.getMontoInicial() + ventasEfectivo;
        }

        private void completarNombres(Caja caja) {

                if (caja.getUsuarioAperturaId() != null) {
                        usuarioRepository.findById(caja.getUsuarioAperturaId())
                                        .map(Usuario::getNombre)
                                        .ifPresent(caja::setUsuarioAperturaNombre);
                }

                if (caja.getUsuarioCierreId() != null) {
                        usuarioRepository.findById(caja.getUsuarioCierreId())
                                        .map(Usuario::getNombre)
                                        .ifPresent(caja::setUsuarioCierreNombre);
                }
        }

        @GetMapping("/actual")
        public Caja cajaActual() {

                Caja caja = cajaRepository
                                .findFirstByAbiertaTrue()
                                .orElse(null);

                if (caja == null) {
                        return null;
                }

                caja.setEfectivoEsperado(calcularEfectivoEsperado(caja));
                completarNombres(caja);

                return caja;
        }

        @GetMapping("/actual/resumen")
        public ResponseEntity<?> resumenCajaActual() {

                Caja caja = cajaRepository
                                .findFirstByAbiertaTrue()
                                .orElse(null);

                if (caja == null) {
                        return ResponseEntity.badRequest()
                                        .body("No hay una caja abierta.");
                }

                List<Venta> ventas = obtenerVentasPeriodo(caja);

                List<Venta> ventasEfectivo = ventas.stream()
                                .filter(v -> "EFECTIVO".equalsIgnoreCase(v.getMetodoPago()))
                                .toList();

                Map<String, Double> totalesPorMetodoPago = ventas.stream()
                                .collect(Collectors.groupingBy(
                                                v -> v.getMetodoPago() == null
                                                                ? "SIN DEFINIR"
                                                                : v.getMetodoPago(),
                                                LinkedHashMap::new,
                                                Collectors.summingDouble(Venta::getTotal)));

                double totalEfectivo = ventasEfectivo.stream()
                                .mapToDouble(Venta::getTotal)
                                .sum();

                Map<String, Object> resumen = new LinkedHashMap<>();

                resumen.put("idCaja", caja.getId());
                resumen.put("montoInicial", caja.getMontoInicial());
                resumen.put("cantidadVentasEfectivo", ventasEfectivo.size());
                resumen.put("totalVentasEfectivo", totalEfectivo);
                resumen.put("efectivoEsperado", caja.getMontoInicial() + totalEfectivo);
                resumen.put("totalesPorMetodoPago", totalesPorMetodoPago);
                resumen.put("movimientosEfectivo", ventasEfectivo);

                return ResponseEntity.ok(resumen);
        }

        @PostMapping("/abrir")
        public ResponseEntity<?> abrirCaja(
                        @RequestParam double montoInicial,
                        HttpSession session) {

                Long usuarioId = (Long) session.getAttribute("usuarioId");

                if (usuarioId == null) {
                        return ResponseEntity.status(401)
                                        .body("Debe iniciar sesión para abrir la caja.");
                }

                if (montoInicial < 0) {
                        return ResponseEntity.badRequest()
                                        .body("El monto inicial no puede ser negativo.");
                }

                Caja cajaAbierta = cajaRepository
                                .findFirstByAbiertaTrue()
                                .orElse(null);

                if (cajaAbierta != null) {
                        return ResponseEntity.badRequest()
                                        .body("Ya existe una caja abierta.");
                }

                Caja caja = new Caja();

                caja.setMontoInicial(montoInicial);
                caja.setMontoFinal(0);
                caja.setEfectivoEsperado(montoInicial);
                caja.setDiferencia(0);
                caja.setFechaApertura(LocalDateTime.now());
                caja.setUsuarioAperturaId(usuarioId);
                caja.setAbierta(true);

                return ResponseEntity.ok(cajaRepository.save(caja));
        }

        @PostMapping("/cerrar")
        public ResponseEntity<?> cerrarCaja(
                        @RequestParam double montoFinal,
                        HttpSession session) {

                Long usuarioId = (Long) session.getAttribute("usuarioId");

                if (usuarioId == null) {
                        return ResponseEntity.status(401)
                                        .body("Debe iniciar sesión para cerrar la caja.");
                }

                if (montoFinal < 0) {
                        return ResponseEntity.badRequest()
                                        .body("El monto final no puede ser negativo.");
                }

                Caja caja = cajaRepository
                                .findFirstByAbiertaTrue()
                                .orElse(null);

                if (caja == null) {
                        return ResponseEntity.badRequest()
                                        .body("No hay una caja abierta.");
                }

                caja.setFechaCierre(LocalDateTime.now());

                double efectivoEsperado = calcularEfectivoEsperado(caja);
                double diferencia = montoFinal - efectivoEsperado;

                caja.setEfectivoEsperado(efectivoEsperado);
                caja.setMontoFinal(montoFinal);
                caja.setDiferencia(diferencia);
                caja.setUsuarioCierreId(usuarioId);
                caja.setAbierta(false);

                return ResponseEntity.ok(cajaRepository.save(caja));
        }

        @GetMapping
        public List<Caja> listar() {

                List<Caja> cajas = cajaRepository.findAll();

                for (Caja caja : cajas) {
                        caja.setEfectivoEsperado(calcularEfectivoEsperado(caja));
                        completarNombres(caja);
                }

                return cajas;
        }
}
