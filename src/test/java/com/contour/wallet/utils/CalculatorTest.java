package com.contour.wallet.utils;

import com.contour.wallet.config.WalletTestConfig;
import com.contour.wallet.exceptions.NoChangeException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletTestConfig.class})
public class CalculatorTest {

    @Autowired
    private Calculator calculator;

    @Test
    public void testAggregateCoins(){

        List<Integer> coins = ImmutableList.of(1,3,1,2,1);
        HashMap<Integer, Integer> coinMap = calculator.aggregateCoins(coins);

        Assertions.assertThat(coinMap).isNotNull();
        Assertions.assertThat(coinMap.size()).isEqualTo(3);
        Assertions.assertThat(coinMap.get(1)).isEqualTo(3);
        Assertions.assertThat(coinMap.get(2)).isEqualTo(1);
        Assertions.assertThat(coinMap.get(3)).isEqualTo(1);

    }

    @Test
    public void testAggregateCoinsWithZeros(){

        List<Integer> coins = ImmutableList.of(1,3,1,2,1,0);
        HashMap<Integer, Integer> coinMap = calculator.aggregateCoins(coins);

        Assertions.assertThat(coinMap).isNotNull();
        Assertions.assertThat(coinMap.size()).isEqualTo(3);
        Assertions.assertThat(coinMap.get(1)).isEqualTo(3);
        Assertions.assertThat(coinMap.get(2)).isEqualTo(1);
        Assertions.assertThat(coinMap.get(3)).isEqualTo(1);

    }

    @Test
    public void testCollectAndOrderCoins(){
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,2); coinMap.put(2, 1); coinMap.put(3,1);
        List<Integer> coinsList = calculator.collectAndOrderCoins(coinMap);

        Assertions.assertThat(coinsList.size()).isEqualTo(4);
        Assertions.assertThat(coinsList).isSorted();
    }

    @Test
    public void testCoinsToDeductWithOneExactCoin() throws NoChangeException{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,2); coinMap.put(2, 1); coinMap.put(3,1);

        HashMap<Integer, Integer> coinUpdateMap = calculator.coinsToDeduct(coinMap, 1);

        // One coin row in DB to update
        Assertions.assertThat(coinUpdateMap.size()).isEqualTo(1);
        Assertions.assertThat(coinUpdateMap.get(1)).isEqualTo(1);

    }

    @Test
    public void testCoinsToDeductWithMultipleExactCoin() throws NoChangeException{
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(1,2); coinMap.put(2, 1); coinMap.put(3,1);

        HashMap<Integer, Integer> coinUpdateMap = calculator.coinsToDeduct(coinMap, 4);

        // Two coin row from DB to delete since the coin count is 0
        Assertions.assertThat(coinUpdateMap.size()).isEqualTo(2);
        Assertions.assertThat(coinUpdateMap.get(1)).isEqualTo(0);
        Assertions.assertThat(coinUpdateMap.get(2)).isEqualTo(0);

    }

    @Test
    public void testCoinsToDeductWithOneExactCoinWithChange(){
        HashMap<Integer, Integer> coinMap = new HashMap<>();
        coinMap.put(2,2); coinMap.put(5, 1); coinMap.put(7,1);

        //To pay 6
        try {
            HashMap<Integer, Integer> coinUpdateMap = calculator.coinsToDeduct(coinMap, 6);
        }catch(Exception e){
            Assertions.assertThat(e).isInstanceOf(NoChangeException.class);
            Assertions.assertThat(((NoChangeException)e).getChange()).isEqualTo(1);
        }

    }
}
