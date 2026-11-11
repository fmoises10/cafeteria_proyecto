
package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/reportes")
public class ReporteController {

        private final VentaRepository ventaRepository;
        private final CajaRepository cajaRepository;
        private final ConsumoRepository consumoRepository;
        private final ContenedorRepository contenedorRepository;
        private final InsumoRepository insumoRepository;
        private final UsuarioRepository usuarioRepository;

        public ReporteController(
                        VentaRepository ventaRepository,
                        CajaRepository cajaRepository,
                        ConsumoRepository consumoRepository,
                        ContenedorRepository contenedorRepository,
                        InsumoRepository insumoRepository,
                        UsuarioRepository usuarioRepository) {

                this.ventaRepository = ventaRepository;
                this.cajaRepository = cajaRepository;
                this.consumoRepository = consumoRepository;
                this.contenedorRepository = contenedorRepository;
                this.insumoRepository = insumoRepository;
                this.usuarioRepository = usuarioRepository;
        }

        // REPORTE DE VENTAS

        @GetMapping("/ventas")
        public Map<String, Object> reporteVentas() {

                List<Venta> ventas = ventaRepository.findAll();

                Map<Long, Usuario> usuarios = new HashMap<>();

                for (Usuario usuario : usuarioRepository.findAll()) {
                        usuarios.put(usuario.getId(), usuario);
                }

                double totalVentas = ventas.stream()
                                .mapToDouble(Venta::getTotal)
                                .sum();

                double efectivo = ventas.stream()
                                .filter(v -> "EFECTIVO".equals(v.getMetodoPago()))
                                .mapToDouble(Venta::getTotal)
                                .sum();

                double qr = ventas.stream()
                                .filter(v -> "QR".equals(v.getMetodoPago()))
                                .mapToDouble(Venta::getTotal)
                                .sum();

                double tarjeta = ventas.stream()
                                .filter(v -> "TARJETA".equals(v.getMetodoPago()))
                                .mapToDouble(Venta::getTotal)
                                .sum();

                List<Map<String, Object>> detalleVentas = new ArrayList<>();

                for (Venta venta : ventas) {

                        Usuario usuario = usuarios.get(venta.getUsuarioId());

                        String nombreUsuario = usuario != null
                                        ? usuario.getNombre()
                                        : "No identificado";

                        String rolUsuario = usuario != null
                                        ? usuario.getRol()
                                        : "-";

                        Map<String, Object> registro = new LinkedHashMap<>();

                        registro.put("id", venta.getId());
                        registro.put("fecha", venta.getFecha());
                        registro.put("total", venta.getTotal());
                        registro.put("metodoPago", venta.getMetodoPago());
                        registro.put("usuarioId", venta.getUsuarioId());

                        registro.put("usuario", nombreUsuario);
                        registro.put("rol", rolUsuario);

                        // Compatibilidad con la versión actual de reportes.html
                        registro.put("vendedor", nombreUsuario);

                        detalleVentas.add(registro);
                }

                Map<String, Object> reporte = new HashMap<>();

                reporte.put("cantidadVentas", ventas.size());
                reporte.put("totalVentas", totalVentas);
                reporte.put("efectivo", efectivo);
                reporte.put("qr", qr);
                reporte.put("tarjeta", tarjeta);
                reporte.put("ventas", detalleVentas);

                return reporte;
        }

        // REPORTE DE CAJAS

        @GetMapping("/cajas")
        public List<Caja> reporteCajas() {
                return cajaRepository.findAll();
        }

        // REPORTE DE CONSUMOS

        @GetMapping("/consumos")
        public Map<String, Object> reporteConsumos() {

                List<Consumo> consumos = consumoRepository.findAll();

                Map<Long, Contenedor> contenedores = new HashMap<>();

                for (Contenedor c : contenedorRepository.findAll()) {
                        contenedores.put(c.getId(), c);
                }

                Map<Long, Insumo> insumos = new HashMap<>();

                for (Insumo i : insumoRepository.findAll()) {
                        insumos.put(i.getId(), i);
                }

                Map<Long, Usuario> usuarios = new HashMap<>();

                for (Usuario u : usuarioRepository.findAll()) {
                        usuarios.put(u.getId(), u);
                }

                Map<Long, Map<String, Object>> totalesPorInsumo = new LinkedHashMap<>();

                List<Map<String, Object>> detalle = new ArrayList<>();

                for (Consumo consumo : consumos) {

                        Contenedor contenedor = contenedores.get(consumo.getContenedorId());

                        Insumo insumo = insumos.get(consumo.getInsumoId());

                        Usuario usuario = usuarios.get(consumo.getUsuarioId());

                        String nombreInsumo = insumo != null
                                        ? insumo.getNombre()
                                        : "Insumo no encontrado";

                        String unidad = insumo != null
                                        ? insumo.getUnidad()
                                        : "";

                        String codigoContenedor = contenedor != null
                                        ? contenedor.getCodigoBarras()
                                        : "-";

                        String nombreUsuario = usuario != null
                                        ? usuario.getNombre()
                                        : "Usuario no encontrado";

                        Map<String, Object> registro = new LinkedHashMap<>();

                        registro.put("id", consumo.getId());
                        registro.put("fecha", consumo.getFecha());
                        registro.put("insumo", nombreInsumo);
                        registro.put("unidad", unidad);
                        registro.put("cantidad", consumo.getCantidad());
                        registro.put("contenedor", codigoContenedor);
                        registro.put("usuario", nombreUsuario);

                        detalle.add(registro);

                        Long clave = consumo.getInsumoId();

                        Map<String, Object> resumen = totalesPorInsumo.computeIfAbsent(clave, k -> {

                                Map<String, Object> nuevo = new LinkedHashMap<>();

                                nuevo.put("insumo", nombreInsumo);
                                nuevo.put("unidad", unidad);
                                nuevo.put("cantidadTotal", 0.0);

                                return nuevo;
                        });

                        double cantidadActual = (double) resumen.get("cantidadTotal");

                        resumen.put(
                                        "cantidadTotal",
                                        cantidadActual + consumo.getCantidad());
                }

                Map<String, Object> reporte = new LinkedHashMap<>();

                reporte.put("cantidadRegistros", consumos.size());

                reporte.put(
                                "totalPorInsumo",
                                new ArrayList<>(totalesPorInsumo.values()));

                reporte.put("consumos", detalle);

                return reporte;
        }
}
