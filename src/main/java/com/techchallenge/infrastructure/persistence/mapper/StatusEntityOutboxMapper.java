package com.techchallenge.infrastructure.persistence.mapper;

import com.techchallenge.domain.entity.StatusOutbox;
import com.techchallenge.infrastructure.persistence.entity.StatusOutboxEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StatusEntityOutboxMapper {
    public StatusOutboxEntity toStatusOutboxEntity(StatusOutbox outbox) {
        return StatusOutboxEntity.builder()
                .id(outbox.getId())
                .status(outbox.getStatus())
                .send(outbox.getSend())
                .createTime(outbox.getCreateTime())
                .orderId(outbox.getOrderId()).build();
    }

    public StatusOutbox toStatusOutbox(StatusOutboxEntity outbox) {
        return new StatusOutbox().toOutBox(outbox.getId(), outbox.getStatus(),
                outbox.getSend(), outbox.getCreateTime(), outbox.getOrderId());
    }
    public List<StatusOutbox> toStatusOutboxList(List<StatusOutboxEntity> outboxs) {
        return outboxs.stream().map(this::toStatusOutbox).toList();
    }
}
