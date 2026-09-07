package com.example.demo.controller;

import com.example.demo.model.DetalleVenta;
import com.example.demo.model.Producto;
import com.example.demo.model.Venta;
import com.example.demo.repository.DetalleVentaRepository;
import com.example.demo.repository.ProductoRepository;
import com.example.demo.repository.VentaRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final ProductoRepository productoRepository;

    public TicketController(
            VentaRepository ventaRepository,
            DetalleVentaRepository detalleVentaRepository,
            ProductoRepository productoRepository) {

        this.ventaRepository = ventaRepository;
        this.detalleVentaRepository = detalleVentaRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping("/{ventaId}")
    public Map<String, Object> obtenerTicket(
            @PathVariable Long ventaId) {

        Venta venta = ventaRepository
                .findById(ventaId)
                .orElse(null);

        if (venta == null) {
            return null;
        }

        List<DetalleVenta> detalles =
                detalleVentaRepository.findByVentaId(ventaId);

        List<Map<String, Object>> productos =
                new ArrayList<>();

        for (DetalleVenta detalle : detalles) {

            Producto producto =
                    productoRepository
                            .findById(detalle.getProductoId())
                            .orElse(null);

            Map<String, Object> item =
                    new HashMap<>();

            item.put(
                    "nombre",
                    producto != null
                            ? producto.getNombre()
                            : "Producto no encontrado"
            );

            item.put(
                    "cantidad",
                    detalle.getCantidad()
            );

            item.put(
                    "precio",
                    detalle.getPrecio()
            );

            item.put(
                    "subtotal",
                    detalle.getSubtotal()
            );

            productos.add(item);
        }

        Map<String, Object> ticket =
                new HashMap<>();

        ticket.put("id", venta.getId());
        ticket.put("fecha", venta.getFecha());
        ticket.put("metodoPago", venta.getMetodoPago());
        ticket.put("total", venta.getTotal());
        ticket.put("productos", productos);

        return ticket;
    }
}