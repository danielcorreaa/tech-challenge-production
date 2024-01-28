package com.techchallenge.domain.entity;

import java.time.LocalDateTime;

public class StatusOutbox {

    private Long id;
    private String orderId;
    private String status;
    private LocalDateTime createTime;
    private Boolean send;

    public StatusOutbox(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
        this.createTime = LocalDateTime.now();
        this.send = Boolean.FALSE;
    }

    public StatusOutbox() {
    }

    public StatusOutbox send(){
        this.send = Boolean.TRUE;
        return this;
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public Boolean getSend() {
        return send;
    }

    public StatusOutbox toOutBox(Long id, String status, Boolean send, LocalDateTime createTime, String orderId) {
        this.id = id;
        this.orderId = orderId;
        this.status = status;
        this.createTime = createTime;
        this.send = send;
        return this;
    }
}
