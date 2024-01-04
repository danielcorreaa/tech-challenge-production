package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.entity.ProductionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProductionRepository extends JpaRepository<ProductionEntity, String> {

    @Query("select prod from ProductionEntity prod JOIN FETCH prod.products where prod.orderId = (:orderId)")
    Optional<ProductionEntity> findById(@Param("orderId") String orderId);



}
