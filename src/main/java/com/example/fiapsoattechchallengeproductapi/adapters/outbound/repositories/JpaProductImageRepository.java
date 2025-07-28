package com.example.fiapsoattechchallengeproductapi.adapters.outbound.repositories;

import com.example.fiapsoattechchallengeproductapi.adapters.outbound.entities.JpaProductImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface JpaProductImageRepository extends JpaRepository<JpaProductImageEntity, Long> {

    List<JpaProductImageEntity> findByProductId(Long productId);

    @Transactional
    @Modifying
    @Query("DELETE FROM JpaProductImageEntity i WHERE i.productId = :productId")
    void deleteAllImagesByProductId(Long productId);
}
