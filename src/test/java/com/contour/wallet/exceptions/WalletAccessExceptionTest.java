package com.contour.wallet.exceptions;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class WalletAccessExceptionTest {

    @Test
    public void testWalletAccessException() {
        WalletAccessException insufficientBalanceException = new WalletAccessException("Error while accessing wallet");
        Assertions.assertThat(insufficientBalanceException.getMessage()).isEqualTo("Error while accessing wallet");
    }
}
