package com.paymentchain.transactions.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {

    PENDIENTE ("01"),
    LIQUIDADA("02"),
    RECHAZADA("03"),
    CANCELADA("04");


    private final String codigo;

    public static Status codigo(String codigo){
        for (Status status : values()){
            if (status.codigo.equals(codigo)){
                return status;
            }
        }
        throw new IllegalArgumentException("Codigo status invalido");
    }



}
