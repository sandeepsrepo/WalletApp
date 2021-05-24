package com.contour.wallet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Response<T> {

    @NonNull
    private int status;

    @NonNull
    private String statusMessage;

    private T Data;
}
