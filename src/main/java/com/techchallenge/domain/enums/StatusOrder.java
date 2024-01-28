package com.techchallenge.domain.enums;

import java.util.Arrays;

public enum StatusOrder {
	RECEBIDO, EM_PREPARACAO, PRONTO, FINALIZADO;

	public static StatusOrder getByName(String name){
		return Arrays.stream(values()).filter(v -> v.name().equals(name)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Invalid Status Order!"));
	}

	public static StatusOrder getByStatus(StatusOrder status){
		return status;
	}


	public String getValue(){
		return this.name();
	}
}
