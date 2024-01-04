package com.techchallenge.infrastructure.persistence.repository;

import com.techchallenge.infrastructure.persistence.entity.StatusOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StatusEntityOutboxRespository extends JpaRepository<StatusOutboxEntity, Long> {
    @Query("select s from StatusOutboxEntity s where s.send = false ORDER BY s.createTime ASC")
    List<StatusOutboxEntity> findBySend();

}
