package com.techchallenge.application.gateway;

import com.techchallenge.domain.entity.StatusOutbox;

import java.util.List;

public interface StatusOutboxGateway {
    void insert(StatusOutbox outbox);

    List<StatusOutbox> findByNotSend();
}
