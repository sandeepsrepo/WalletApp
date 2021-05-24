package com.contour.wallet.controller;

import com.contour.wallet.config.WalletTestConfig;
import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.exceptions.WalletAccessException;
import com.contour.wallet.model.Response;
import static org.assertj.core.api.Assertions.assertThat;

import com.contour.wallet.service.AssignmentWalletService;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {WalletTestConfig.class})
public class WalletControllerTest {

    @MockBean
    private AssignmentWalletService assignmentWalletService;

    @Autowired
    private WalletController walletController;

    @Test
    public void testCreditWalletNoData(){
        List<Integer> coins = null;
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid coins");
    }

    @Test
    public void testCreditWalletWithItemsMoreThanZeroCoins(){
        List<Integer> coins = Arrays.asList();
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid coins");
    }

    @Test
    public void testCreditWalletWithOnlyZeros(){
        List<Integer> coins = Arrays.asList(0,0,0,0);
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid non-zero coins");
    }

    @Test
    public void testCreditWalletWithInvalidNegativeCoins(){
        List<Integer> coins = Arrays.asList(1,-2,2,3);
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide all coins with positive values");
    }

    @Test
    public void testCreditWalletWithValidCoins(){
        List<Integer> coins = Arrays.asList(1,2,3);
        when(assignmentWalletService.creditWallet(ArgumentMatchers.anyList())).thenReturn(true);
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatusMessage()).isEqualTo(HttpStatus.OK.getReasonPhrase());
    }

    @Test
    public void testCreditWalletWithServerException(){
        List<Integer> coins = Arrays.asList(1,2,3);
        when(assignmentWalletService.creditWallet(ArgumentMatchers.anyList())).thenThrow(new RuntimeException("TestExcetion"));
        Response response = walletController.creditWallet(coins);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getStatusMessage()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }

    @Test
    public void testWalletBalance(){
        List<Integer> coins = Arrays.asList(3,1,2);
        when(assignmentWalletService.walletBalance()).thenReturn(coins);
        Response response = walletController.walletBalance();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatusMessage()).isEqualTo(HttpStatus.OK.getReasonPhrase());
        assertThat(response.getData()).isEqualTo(coins);
    }

    @Test
    public void testWalletBalanceWithException(){
        when(assignmentWalletService.walletBalance()).thenThrow(new RuntimeException("Unknown Exception"));
        Response response = walletController.walletBalance();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(response.getStatusMessage()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        assertThat(response.getData()).isEqualTo(null);
    }

    @Test
    public void testPayNullValue(){
        Integer coin = null;
        Response response = walletController.payFromWallet(coin);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid amount to pay");
    }

    @Test
    public void testPayNegativeValue(){
        Integer coin = -3;
        Response response = walletController.payFromWallet(coin);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid non-zero amount to pay");
    }

    @Test
    public void testPayZero(){
        Integer coin = 0;
        Response response = walletController.payFromWallet(coin);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getStatusMessage()).isEqualTo("Please provide valid non-zero amount to pay");
    }

    @Test
    public void testPay() throws InsufficientBalanceException, WalletAccessException {
        Integer coin = 2;
        List<Integer> coins = ImmutableList.of(1,3,4);
        when(assignmentWalletService.debitWallet(coin)).thenReturn(coins);
        Response response = walletController.payFromWallet(coin);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getStatusMessage()).isEqualTo(HttpStatus.OK.getReasonPhrase());
        assertThat(response.getData()).isEqualTo(coins);
    }

}
