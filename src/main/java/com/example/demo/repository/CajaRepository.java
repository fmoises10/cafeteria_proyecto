package com.example.demo.repository;

import com.example.demo.model.Caja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {

    Optional<Caja> findFirstByAbiertaTrue();
}