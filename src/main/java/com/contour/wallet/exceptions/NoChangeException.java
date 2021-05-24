package com.contour.wallet.exceptions;

import lombok.Data;

@Data
public class NoChangeException extends Exception {

    private Integer change;

    public NoChangeException(String errorMessage, Integer change){
        super(errorMessage);
        this.change = change;
    }
}
