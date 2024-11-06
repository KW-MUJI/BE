package com.muji_backend.kw_muji.common.exception;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String timestamp;
}