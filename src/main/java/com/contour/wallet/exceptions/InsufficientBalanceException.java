package com.contour.wallet.exceptions;

import lombok.Data;

import java.util.List;

@Data
public class InsufficientBalanceException extends Exception {

    private List<Integer> currentWalletBalance;

    public InsufficientBalanceException(String errorMessage, List<Integer> currentWalletBalance) {
        super(errorMessage);
        this.currentWalletBalance = currentWalletBalance;
    }

}
