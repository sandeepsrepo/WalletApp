package com.contour.wallet.persistance;

import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.WalletAccessException;

import java.util.HashMap;
import java.util.Optional;

public interface WalletDao {

    boolean addCoins(HashMap<Integer, Integer> coinMap) throws WalletAccessException;

    boolean removeAllCoins() throws WalletAccessException;

    HashMap<Integer, Integer> getAllCoins() throws WalletAccessException;

    Optional<Integer> getWalletBalance() throws WalletAccessException;

    void updateWallet(HashMap<Integer, Integer> coinMap, HashMap<Integer, Integer> coinInWallet) throws WalletAccessException;
}
