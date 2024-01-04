package com.techchallenge.application.gateway;


public interface MessageGateway {
    void send(String orderId, String status);
}
