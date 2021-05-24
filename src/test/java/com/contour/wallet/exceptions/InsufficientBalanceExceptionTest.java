package com.contour.wallet.exceptions;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.Collections;

public class InsufficientBalanceExceptionTest {

    @Test
    public void testInsuffientBalanceException() {
        InsufficientBalanceException insufficientBalanceException = new InsufficientBalanceException("Insufficient Balance", ImmutableList.of());
        Assertions.assertThat(insufficientBalanceException.getCurrentWalletBalance()).isEqualTo(Collections.emptyList());
        Assertions.assertThat(insufficientBalanceException.getMessage()).isEqualTo("Insufficient Balance");
    }
}
