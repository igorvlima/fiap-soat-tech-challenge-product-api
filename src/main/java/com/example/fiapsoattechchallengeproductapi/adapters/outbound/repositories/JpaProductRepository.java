package com.example.fiapsoattechchallengeproductapi.adapters.outbound.repositories;

import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JpaProductRepository extends JpaRepository<JpaProductEntity, Long> {

    @Query("SELECT p FROM JpaProductEntity p WHERE p.category = :category AND p.active = true")
    List<JpaProductEntity> findProductByCategory(@Param("category") String category);
}