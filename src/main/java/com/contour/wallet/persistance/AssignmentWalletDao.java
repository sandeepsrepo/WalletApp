package com.contour.wallet.persistance;

import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.WalletAccessException;
import com.contour.wallet.model.CoinEntity;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class AssignmentWalletDao implements WalletDao {

    private static Logger LOGGER = LoggerFactory.getLogger(AssignmentWalletDao.class);

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public boolean addCoins(HashMap<Integer, Integer> coinMap) throws WalletAccessException {

        List<CoinEntity> coins = new ArrayList<>();
        AtomicInteger coinId = new AtomicInteger(1);

        try {
            Iterable<CoinEntity> currentList = walletRepository.findAll();
            walletRepository.deleteAll(currentList);

            coinMap.forEach((coinValue, coinCount) -> {
                CoinEntity coin = new CoinEntity(coinId.getAndIncrement(), coinValue, coinCount);
                coins.add(coin);
            });

            Iterable<CoinEntity> result = walletRepository.saveAll(coins);

            List resultList = Lists.newArrayList(result);
            return resultList.size() == coinMap.size();
        } catch (Exception e) {
            LOGGER.error("Error while adding the coin to the wallet : {}", e);
            throw new WalletAccessException("Error while adding the coin to the wallet");
        }
    }

    @Override
    public boolean removeAllCoins() throws WalletAccessException {

        List<CoinEntity> coins = new ArrayList<>();
        AtomicInteger coinId = new AtomicInteger(1);

        try {
            Iterable<CoinEntity> currentList = walletRepository.findAll();
            walletRepository.deleteAll(currentList);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error while adding the coin to the wallet : {}", e);
            throw new WalletAccessException("Error while adding the coin to the wallet");
        }
    }

    @Override
    public HashMap<Integer, Integer> getAllCoins() throws WalletAccessException {
        HashMap<Integer, Integer> coinMap = new HashMap<>();

        try {
            Iterable<CoinEntity> result = walletRepository.findAll();

            result.forEach(coin -> {
                coinMap.put(coin.getCoinValue(), coin.getCoinCount());
            });
        } catch (Exception e) {
            LOGGER.error("Error while getting the current coin coin to the wallet : {}", e);
            throw new WalletAccessException("Error while getting the current coin list from the wallet");
        }

        return coinMap;
    }

    @Override
    public Optional<Integer> getWalletBalance() throws WalletAccessException {
        try {
            return Optional.of(walletRepository.getCurrentBalance());
        } catch (Exception e) {
            LOGGER.error("Error while getting current balance in the wallet : {}", e);
            throw new WalletAccessException("Error while getting the current balance in the wallet");
        }
    }

    @Override
    public void updateWallet(HashMap<Integer, Integer> coinMap, HashMap<Integer, Integer> coinInWallet) throws WalletAccessException {

        List<CoinEntity> coinsToUpdate = new ArrayList<>();
        List<CoinEntity> coinsToAdd = new ArrayList<>();

        try {
            coinMap.forEach((coinValue, coinCount) -> {
                if (coinCount == 0) {
                    CoinEntity coin = new CoinEntity(coinValue, coinCount);
                    coinsToUpdate.add(coin);
                } else if (coinCount > 0 && coinInWallet.keySet().stream().anyMatch(walletCoin -> walletCoin == coinValue)) {
                    CoinEntity coin = new CoinEntity(coinValue, coinCount);
                    coinsToUpdate.add(coin);
                } else if (coinInWallet.keySet().stream().anyMatch(walletCoin -> walletCoin != coinValue)) {
                    CoinEntity coin = new CoinEntity(coinValue, coinCount);
                    coinsToAdd.add(coin);
                } else {
                }
            });

            if (CollectionUtils.isNotEmpty(coinsToUpdate)) {
                coinsToUpdate.stream().forEach(coin -> walletRepository.updateByCoinValue(coin.getCoinValue(), coin.getCoinCount()));
            }

            if (CollectionUtils.isNotEmpty(coinsToAdd)) {
                walletRepository.saveAll(coinsToAdd);
            }
        } catch (Exception e) {
            LOGGER.error("Error while updating the wallet : {}", e);
            throw new WalletAccessException("Error while updating the wallet");
        }
    }
}
