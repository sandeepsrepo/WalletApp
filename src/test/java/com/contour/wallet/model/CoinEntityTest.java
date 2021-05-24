package com.contour.wallet.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CoinEntityTest {

    private CoinEntity coinEntity = new CoinEntity();

    @Test
    public void testCoinEntityFields(){
        coinEntity.setCoinId(1);
        coinEntity.setCoinValue(1);
        coinEntity.setCoinCount(2);

        Assertions.assertThat(coinEntity.getCoinValue()).isEqualTo(1);
        Assertions.assertThat(coinEntity.getCoinCount()).isEqualTo(2);
        Assertions.assertThat(coinEntity.getCoinId()).isEqualTo(1);
    }

    @Test
    public void testNoOfFieldsCoinEntity() {
        Field[] allFields = CoinEntity.class.getDeclaredFields();

        Assertions.assertThat(allFields.length).isEqualTo(3);

        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("coinId")
                        && field.getType().equals(Integer.class))
        );
        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("coinValue")
                        && field.getType().equals(Integer.class))
        );
        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("coinCount")
                        && field.getType().equals(Integer.class))
        );
    }
}
