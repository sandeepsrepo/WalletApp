package com.contour.wallet.model;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Arrays;

public class ResponseTest {

    private Response<Integer> response = new Response<Integer>(200, "Test", 3);

    @Test
    public void testCoinEntityFields(){

        Assertions.assertThat(response.getStatus()).isEqualTo(200);
        Assertions.assertThat(response.getStatusMessage()).isEqualTo("Test");
        Assertions.assertThat(response.getData()).isEqualTo(3);
    }

    @Test
    public void testNoOfFieldsResponse() {
        Field[] allFields = CoinEntity.class.getDeclaredFields();

        Assertions.assertThat(allFields.length).isEqualTo(3);

        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("status")
                        && field.getType().equals(Integer.class))
        );
        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("statusMessage")
                        && field.getType().equals(String.class))
        );
        Assertions.assertThat(Arrays.stream(allFields).anyMatch(field ->
                field.getName().equals("data")
                        && field.getType().equals(Integer.class))
        );
    }
}
