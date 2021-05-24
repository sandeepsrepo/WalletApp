package com.contour.wallet.exceptions;

import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class NoChangeExceptionTest {

    @Test
    public void testNoChangeException() {
        NoChangeException noChangeException = new NoChangeException("No Change", 1);
        Assertions.assertThat(noChangeException.getChange()).isEqualTo(1);
        Assertions.assertThat(noChangeException.getMessage()).isEqualTo("No Change");
    }
}
