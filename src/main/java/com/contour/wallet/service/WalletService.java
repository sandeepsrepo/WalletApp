package com.contour.wallet.service;

import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.NoChangeException;
import com.contour.wallet.exceptions.WalletAccessException;

import java.util.List;

public interface WalletService {

    boolean creditWallet(List<Integer> coins);

    boolean clearWallet();

    List<Integer> walletBalance();

    List<Integer> debitWallet(Integer payAmount) throws InsufficientBalanceException, WalletAccessException, NoChangeException;
}
