package com.contour.wallet.utils;

import com.contour.wallet.exceptions.NoChangeException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class Calculator {

    public HashMap<Integer, Integer> aggregateCoins(List<Integer> coins) {
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coins.stream()
                .filter(coin -> coin != 0)
                .forEach(coin -> {
                    if (coinMap.get(coin) == null) {
                        coinMap.put(coin, 1);
                    } else {
                        Integer currentCoins = coinMap.get(coin);
                        coinMap.put(coin, ++currentCoins);
                    }
                });
        return coinMap;
    }

    public List<Integer> collectAndOrderCoins(HashMap<Integer, Integer> aggregateCoins) {

        List<Integer> coinCollection = new ArrayList<>();
        aggregateCoins.forEach((coinValue, coinCount) -> {
            for (int i = 0; i < coinCount; i++) {
                coinCollection.add(coinValue);
            }
        });

        return coinCollection.stream().sorted().collect(Collectors.toList());
    }

    public HashMap<Integer, Integer> coinsToDeduct(HashMap<Integer, Integer> coinInWallet,
                                                   Integer payAmount) throws NoChangeException{

        HashMap<Integer, Integer> reArrangedCoins = new HashMap<>();
        // Pay with one exact without change [1,1,2,3,3] -> Pay 1 -> [1,2,3,3]
        // Pay with multiple exact without change [1,1,2,3,3] -> Pay 4 -> [3,3]
        // Pay with multiple exact without change [1,1,2,3,3] -> Pay 5 -> [2,3]
        // Pay with multiple exact without change [1,3] -> Pay 2 -> [2]
        return smallestChangeForPayment(coinInWallet, payAmount);

    }

    private HashMap<Integer, Integer> smallestChangeForPayment(HashMap<Integer, Integer> reArrangedCoins,
                                                               Integer payAmount) throws NoChangeException{

        List<Integer> orderedCollectionOfCoins = collectAndOrderCoins(reArrangedCoins);
        HashMap<Integer, Integer> coinsToUpdate = new HashMap<>();

        Integer currentCoinValue = 0;
        Integer changeCoinValue;

        for (int i = 0; i < orderedCollectionOfCoins.size(); i++) {

            currentCoinValue += orderedCollectionOfCoins.get(i);

            if (currentCoinValue < payAmount) {
                updateCoinsToChange(reArrangedCoins, orderedCollectionOfCoins, coinsToUpdate, i);
                continue;
            } else if (currentCoinValue > payAmount) {
                updateCoinsToChange(reArrangedCoins, orderedCollectionOfCoins, coinsToUpdate, i);
                changeCoinValue = currentCoinValue - payAmount;
                splitToNewCoins(coinsToUpdate, reArrangedCoins, changeCoinValue);
                break;
            } else {
                updateCoinsToChange(reArrangedCoins, orderedCollectionOfCoins, coinsToUpdate, i);
                break;
            }
        }

        return coinsToUpdate;
    }

    private void splitToNewCoins(HashMap<Integer, Integer> coinsToUpdate,
                                 HashMap<Integer, Integer> coins, Integer coinChange) throws NoChangeException{

        List<Integer> coinSet = coins.keySet().stream().sorted().collect(Collectors.toList());
        for (int i = coinSet.size() - 1; i >= 0; i--) {
            Integer coin = coinSet.get(i);
            if (coin > coinChange) {
                continue;
            } else if (coin < coinChange) {
                do {
                    addCoinsChange(coinsToUpdate, coin);
                    coinChange = coinChange - coin;

                } while (coinChange >= coin);
            } else {
                addCoinsChange(coinsToUpdate, coin);
                coinChange = coinChange - coin;
            }
            if(coinChange == 0){
                break;
            }
        }

        if(coinChange != 0){
            throw new NoChangeException("No Change Found", coinChange);
        }

    }

    private void updateCoinsToChange(HashMap<Integer, Integer> reArrangedCoins, List<Integer> orderedCollectionOfCoins, HashMap<Integer, Integer> coinsToUpdate, int i) {
        if (coinsToUpdate.get(orderedCollectionOfCoins.get(i)) != null) {
            Integer currentCointCount = coinsToUpdate.get(orderedCollectionOfCoins.get(i));
            coinsToUpdate.put(orderedCollectionOfCoins.get(i), --currentCointCount);
        } else {
            Integer currentCointCount = reArrangedCoins.get(orderedCollectionOfCoins.get(i));
            coinsToUpdate.put(orderedCollectionOfCoins.get(i), --currentCointCount);
        }
    }

    private void addCoinsChange(HashMap<Integer, Integer> coinsToUpdate, int coinChange) {
        if (coinsToUpdate.get(coinChange) != null) {
            Integer currentCointCount = coinsToUpdate.get(coinChange);
            coinsToUpdate.put(coinChange, ++currentCointCount);
        } else {
            coinsToUpdate.put(coinChange, 1);
        }
    }
}
