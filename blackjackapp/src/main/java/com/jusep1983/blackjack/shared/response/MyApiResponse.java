package com.jusep1983.blackjack.shared.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MyApiResponse<T> {
    private int status;
    private String message;
    private T data;
}
