package com.example.springboot.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * 自定义异常
 * 运行时异常
 */
@Getter
@Setter
public class CustomerException extends RuntimeException {
    private String code;
    private String msg;

    public CustomerException(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CustomerException(String msg) {
        this.code = "500";
        this.msg = msg;
    }

    public CustomerException() {
    }
}
