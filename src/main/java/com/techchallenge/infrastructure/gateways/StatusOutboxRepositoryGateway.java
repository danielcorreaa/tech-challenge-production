package com.techchallenge.infrastructure.gateways;

import com.techchallenge.application.gateway.StatusOutboxGateway;
import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.infrastructure.persistence.entity.StatusOutboxEntity;
import com.techchallenge.infrastructure.persistence.mapper.StatusEntityOutboxMapper;
import com.techchallenge.infrastructure.persistence.repository.StatusEntityOutboxRespository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatusOutboxRepositoryGateway implements StatusOutboxGateway {

    private StatusEntityOutboxRespository statusEntityOutboxRespository;
    private StatusEntityOutboxMapper mapper;

    public StatusOutboxRepositoryGateway(StatusEntityOutboxRespository statusEntityOutboxRespository, StatusEntityOutboxMapper mapper) {
        this.statusEntityOutboxRespository = statusEntityOutboxRespository;
        this.mapper = mapper;
    }

    @Override
    public void insert(StatusOutbox outbox) {
        StatusOutboxEntity entity =  mapper.toStatusOutboxEntity(outbox);
        statusEntityOutboxRespository.save(entity);
    }

    @Override
    public List<StatusOutbox> findByNotSend() {
        List<StatusOutboxEntity> outboxs = statusEntityOutboxRespository.findBySend();
        return mapper.toStatusOutboxList(outboxs);
    }
}
