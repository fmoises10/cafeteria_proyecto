package com.example.demo.repository;

import com.example.demo.model.Contenedor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContenedorRepository extends JpaRepository<Contenedor, Long> {
}