package com.contour.wallet.persistance;

import com.contour.wallet.config.WalletTestConfig;
import com.contour.wallet.exceptions.WalletAccessException;
import com.contour.wallet.model.CoinEntity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletTestConfig.class})
public class AssignmentWalletDaoTest {

    @MockBean
    private WalletRepository walletRepository;

    @Autowired
    private AssignmentWalletDao assignmentWalletDao;

    @Test
    public void testAddCoins() throws Exception{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        List<CoinEntity> coins = new ArrayList<>();
        coins.add(new CoinEntity(1,1,3));
        coins.add(new CoinEntity(1,2,3));
        Mockito.when(walletRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(coins);

        Assertions.assertThat(assignmentWalletDao.addCoins(coinMap)).isNotNull();
        Assertions.assertThat(assignmentWalletDao.addCoins(coinMap)).isEqualTo(true);
    }

    @Test
    public void testAllCoinsNotAdded() throws Exception{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        List<CoinEntity> coins = new ArrayList<>();
        coins.add(new CoinEntity(1,1,3));
        coins.add(new CoinEntity(1,2,3));

        List<CoinEntity> savedCoins = new ArrayList<>();
        savedCoins.add(new CoinEntity(1,1,3));

        Mockito.when(walletRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(savedCoins);

        Assertions.assertThat(assignmentWalletDao.addCoins(coinMap)).isNotNull();
        Assertions.assertThat(assignmentWalletDao.addCoins(coinMap)).isEqualTo(false);
    }

    @Test
    public void testAddCoinsWithException(){
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        List<CoinEntity> coins = new ArrayList<>();
        coins.add(new CoinEntity(1,1,3));
        coins.add(new CoinEntity(1,2,3));

        List<CoinEntity> savedCoins = new ArrayList<>();
        savedCoins.add(new CoinEntity(1,1,3));

        Mockito.when(walletRepository.saveAll(ArgumentMatchers.anyList())).thenThrow(new RuntimeException("SQL Validation Error from repository"));

        try {
            Assertions.assertThat(assignmentWalletDao.addCoins(coinMap)).isEqualTo(false);
        }catch(Exception e){
            Assertions.assertThat(e).isInstanceOf(WalletAccessException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Error while adding the coin to the wallet");
        }
    }

    @Test
    public void testGetAllCoins() throws Exception{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        List<CoinEntity> coins = new ArrayList<>();
        coins.add(new CoinEntity(1,1,3));
        coins.add(new CoinEntity(1,2,3));
        Mockito.when(walletRepository.findAll()).thenReturn(coins);

        Assertions.assertThat(assignmentWalletDao.getAllCoins()).isNotNull();
        Assertions.assertThat(assignmentWalletDao.getAllCoins().size()).isEqualTo(2);
        Assertions.assertThat(assignmentWalletDao.getAllCoins().get(1)).isEqualTo(3);
        Assertions.assertThat(assignmentWalletDao.getAllCoins().get(2)).isEqualTo(3);
    }

    @Test
    public void testGetAllCoinsWithException(){
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        List<CoinEntity> coins = new ArrayList<>();
        coins.add(new CoinEntity(1,1,3));
        coins.add(new CoinEntity(1,2,3));
        Mockito.when(walletRepository.findAll()).thenThrow(new RuntimeException("SQL Validation Error from repository"));

        try{
            assignmentWalletDao.getAllCoins();
        }catch(Exception e){
            Assertions.assertThat(e).isInstanceOf(WalletAccessException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Error while getting the current coin list from the wallet");
        }
    }

    @Test
    public void testGetWalletBalance() throws Exception{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        Mockito.when(walletRepository.getCurrentBalance()).thenReturn(9);

        Assertions.assertThat(assignmentWalletDao.getWalletBalance()).isNotNull();
        Assertions.assertThat(assignmentWalletDao.getWalletBalance().get()).isEqualTo(9);

    }

    @Test
    public void testGetWalletBalanceWithException(){
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,3); coinMap.put(2,3);

        Mockito.when(walletRepository.getCurrentBalance()).thenReturn(9);

        try {
            Assertions.assertThat(assignmentWalletDao.getWalletBalance()).isNotNull();
        }catch(Exception e){
            Assertions.assertThat(e).isInstanceOf(WalletAccessException.class);
            Assertions.assertThat(e.getMessage()).isEqualTo("Error while getting the current balance in the wallet");

        }
    }

    @Test
    public void testUpdateWalletPayWithChange() throws Exception{
        HashMap<Integer, Integer> coinInWallet = new HashMap<>();
        coinInWallet.put(3,2); coinInWallet.put(5,2);

        //paying 12
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(3,0); coinMap.put(5,0); coinMap.put(4,1);

        Mockito.when(walletRepository.updateByCoinValue(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(1);
        Mockito.when(walletRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(ImmutableList.of(new CoinEntity()));

        assignmentWalletDao.updateWallet(coinMap, coinInWallet);

        Mockito.verify(walletRepository, Mockito.times(2)).updateByCoinValue(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(walletRepository, Mockito.times(1)).saveAll(ArgumentMatchers.anyList());

    }

    @Test
    public void testUpdateWalletPayExact() throws Exception{
        HashMap<Integer, Integer> coinInWallet = new HashMap<>();
        coinInWallet.put(3,2); coinInWallet.put(5,2);

        //paying 11
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(3,0); coinMap.put(5,1);

        Mockito.when(walletRepository.updateByCoinValue(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt())).thenReturn(1);
        Mockito.when(walletRepository.saveAll(ArgumentMatchers.anyList())).thenReturn(ImmutableList.of(new CoinEntity()));

        assignmentWalletDao.updateWallet(coinMap, coinInWallet);

        Mockito.verify(walletRepository, Mockito.times(2)).updateByCoinValue(ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verify(walletRepository, Mockito.times(0)).saveAll(ArgumentMatchers.anyList());

    }
}
