package com.contour.wallet.service;

import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.WalletAccessException;
import com.contour.wallet.persistance.WalletDao;
import com.contour.wallet.utils.Calculator;
import com.google.common.collect.ImmutableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class AssignmentWalletService implements WalletService {

    private static Logger LOGGER = LoggerFactory.getLogger(AssignmentWalletService.class);

    @Autowired
    private Calculator calculator;

    @Autowired
    private WalletDao assignmentWalletDao;

    @Override
    public boolean creditWallet(List<Integer> coins) {
        HashMap<Integer, Integer> aggregateCoins = calculator.aggregateCoins(coins);
        try {

            return assignmentWalletDao.addCoins(aggregateCoins);

        } catch (WalletAccessException wae) {
            LOGGER.error("Error while updating the wallet : {}", wae);
            return false;
        }
    }

    @Override
    public List<Integer> walletBalance() {
        try {

            HashMap<Integer, Integer> aggregateCoins = assignmentWalletDao.getAllCoins();
            return calculator.collectAndOrderCoins(aggregateCoins);

        } catch (WalletAccessException wae) {
            LOGGER.error("Error while updating the wallet : {}", wae);
            throw new RuntimeException(wae);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Integer> debitWallet(Integer payAmount) throws InsufficientBalanceException, WalletAccessException {

        try {
            Optional<Integer> currentBalance = assignmentWalletDao.getWalletBalance();

            if (currentBalance.isPresent() && payAmount <= currentBalance.get()) {
                HashMap<Integer, Integer> coinInWallet = assignmentWalletDao.getAllCoins();
                HashMap<Integer, Integer> coinToUpdate = calculator.coinsToDeduct(coinInWallet, payAmount);

                if (!CollectionUtils.isEmpty(coinToUpdate)) {
                    assignmentWalletDao.updateWallet(coinToUpdate, coinInWallet);
                    return walletBalance();
                }

            } else {
                throw new InsufficientBalanceException("Insufficient balance in the wallet", walletBalance());
            }
        }catch(WalletAccessException e){
            LOGGER.error("Error while updating the wallet : {}", e);
            throw new InsufficientBalanceException("Insufficient balance in the wallet", walletBalance());
        }
        return ImmutableList.of();
    }

}
