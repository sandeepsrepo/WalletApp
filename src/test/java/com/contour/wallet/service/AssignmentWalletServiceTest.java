package com.contour.wallet.service;

import com.contour.wallet.config.WalletTestConfig;
import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.WalletAccessException;
import com.contour.wallet.persistance.WalletDao;
import com.contour.wallet.persistance.WalletRepository;
import com.contour.wallet.utils.Calculator;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletTestConfig.class})
public class AssignmentWalletServiceTest {

    @MockBean
    private Calculator calculator;

    @MockBean
    private WalletDao assignmentWalletDao;

    @Autowired
    private AssignmentWalletService assignmentWalletService;

    @Test
    public void testCreditWallet() throws Exception{
        List<Integer> coins = ImmutableList.of(1,2,3);
        Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(coins);
        Mockito.when(assignmentWalletDao.addCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(true);

        Assertions.assertThat(assignmentWalletService.creditWallet(coins)).isEqualTo(true);
    }

    @Test
    public void testCreditWalletException() throws Exception{
        List<Integer> coins = ImmutableList.of(1,2,3);
        Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(coins);
        Mockito.when(assignmentWalletDao.addCoins(ArgumentMatchers.any(HashMap.class))).thenThrow(new WalletAccessException("Erro while accessing the wallet"));

        Assertions.assertThat(assignmentWalletService.creditWallet(coins)).isEqualTo(false);
    }

    @Test
    public void testWalletBalance() throws Exception{
        HashMap<Integer,Integer> coins = new HashMap<>();
        coins.put(1,2); coins.put(3,2);
        List<Integer> flatCoins = ImmutableList.of(1,1,3,3);
        Mockito.when(assignmentWalletDao.getAllCoins()).thenReturn(coins);
        Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(flatCoins);

        Assertions.assertThat(assignmentWalletService.walletBalance()).isEqualTo(flatCoins);
    }

    @Test
    public void testWalletBalanceException() throws Exception {
        HashMap<Integer,Integer> coins = new HashMap<>();
        coins.put(1,2); coins.put(3,2);
        List<Integer> flatCoins = ImmutableList.of(1, 1, 3, 3);
        Mockito.when(assignmentWalletDao.getAllCoins()).thenReturn(coins);
        Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.eq(coins))).thenThrow(new RuntimeException("Test Exception"));

        try{
            assignmentWalletService.walletBalance();
        }catch (Exception e){
            Assertions.assertThat(e.getMessage()).isEqualTo("Test Exception");
        }
    }

    @Test
    public void testDebitWalletInsuffifientBalance(){
        Integer payAmount = 11;
        HashMap<Integer,Integer> coins = new HashMap<>();
        coins.put(1,2); coins.put(3,2);
        List<Integer> flatCoins = ImmutableList.of(1,1,3,3);

        try {
            Mockito.when(assignmentWalletDao.getAllCoins()).thenReturn(coins);
            Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(flatCoins);
            Mockito.when(assignmentWalletDao.getWalletBalance()).thenReturn(Optional.of(8));
            assignmentWalletService.debitWallet(payAmount);
        }catch(Exception e){
            Assertions.assertThat(e).isInstanceOf(InsufficientBalanceException.class);
            Assertions.assertThat(((InsufficientBalanceException)e).getCurrentWalletBalance()).isEqualTo(flatCoins);
        }

    }


    @Test
    public void testDebitWallet() throws Exception{
        Integer payAmount = 3;
        HashMap<Integer,Integer> coins = new HashMap<>();
        coins.put(1,2); coins.put(3,2);
        List<Integer> flatCoins = ImmutableList.of(1,1,3,3);

        HashMap<Integer, Integer> coinToUpdate = new HashMap<>();
        coinToUpdate.put(1, 0); coinToUpdate.put(1, 0); coinToUpdate.put(3, 0);coinToUpdate.put(2, 1);

        Mockito.when(assignmentWalletDao.getWalletBalance()).thenReturn(Optional.of(8));
        Mockito.when(assignmentWalletDao.getAllCoins()).thenReturn(coins);
        Mockito.when(calculator.collectAndOrderCoins(ArgumentMatchers.any(HashMap.class))).thenReturn(flatCoins);
        Mockito.when(calculator.coinsToDeduct(ArgumentMatchers.any(HashMap.class), ArgumentMatchers.eq(payAmount))).thenReturn(coinToUpdate);

        Mockito.doNothing().when(assignmentWalletDao).updateWallet(ArgumentMatchers.eq(coinToUpdate), ArgumentMatchers.eq(coins));

        Assertions.assertThat(assignmentWalletService.debitWallet(payAmount)).isNotNull();
        Assertions.assertThat(assignmentWalletService.debitWallet(payAmount)).isEqualTo(flatCoins);

    }

}
