package com.team7.Idam.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String message; // ex. 400, 404, 500
    private int status;     // ex. "Bad Request"
    private String error;   // ex. "Invalid Input"
}
