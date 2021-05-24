package com.contour.wallet.controller;

import com.contour.wallet.exceptions.InsufficientBalanceException;
import com.contour.wallet.model.Response;
import com.contour.wallet.service.WalletService;
import com.google.common.base.Preconditions;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class WalletController {

    @Autowired
    private WalletService assignmentWalletService;


    @ApiOperation(value = "Initializes the wallet with a set of coins")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "ok"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "An unexpected error occurred")
    })
    @PostMapping("/wallet/credit")
    public Response creditWallet(@RequestBody List<Integer> coins) {
        try {

            Preconditions.checkArgument(coins != null, "Please provide valid coins");
            Preconditions.checkArgument(coins.size() > 0, "Please provide valid coins");
            Preconditions.checkArgument(coins.stream().reduce(0, (coin1, coin2) -> coin1 + coin2) > 0, "Please provide valid non-zero coins");
            Preconditions.checkArgument(coins.stream().filter(coinValue -> coinValue < 0).count() == 0, "Please provide all coins with positive values");


            if (assignmentWalletService.creditWallet(coins)) {
                return new Response(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase());
            } else {
                return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            }

        } catch (IllegalArgumentException iae) {
            return new Response(HttpStatus.BAD_REQUEST.value(), iae.getMessage());
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }



    @ApiOperation(value = "Checks the current balance in the wallet. Displays the coins sorted")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "ok"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "An unexpected error occurred")
    })
    @GetMapping("/wallet/check")
    public Response walletBalance() {
        try {
            List<Integer> coins = assignmentWalletService.walletBalance();
            return new Response(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), coins);

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        }
    }



    @ApiOperation(value = "Pay the specified amount from wallet with exact or without exact or with change also. Returns the balance in the wallet")
    @ApiResponses(value = {@ApiResponse(code = HttpServletResponse.SC_OK, message = "ok"),
            @ApiResponse(code = HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message = "An unexpected error occurred")
    })
    @PatchMapping("/wallet/pay")
    public Response payFromWallet(Integer payAmount) {

        try {
            Preconditions.checkArgument(payAmount != null, "Please provide valid amount to pay");
            Preconditions.checkArgument(payAmount > 0, "Please provide valid non-zero amount to pay");

            List<Integer> coins = assignmentWalletService.debitWallet(payAmount);

            return new Response(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), coins);

        } catch (IllegalArgumentException iae) {
            return new Response(HttpStatus.BAD_REQUEST.value(), iae.getMessage());

        } catch (InsufficientBalanceException ife) {
            return new Response(HttpStatus.BAD_REQUEST.value(), ife.getMessage(), ife.getCurrentWalletBalance());

        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());

        }
    }

}
