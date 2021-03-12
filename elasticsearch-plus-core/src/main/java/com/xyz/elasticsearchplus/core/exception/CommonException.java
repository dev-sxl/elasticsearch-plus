package com.xyz.elasticsearchplus.core.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class CommonException extends RuntimeException implements Serializable {
    private String code;
    private String msg;

    public CommonException(String code, String msg) {
        this(code, msg, null);
    }

    public CommonException(String code, String msg, Throwable cause) {
        super(msg, cause);
        this.code = code;
        this.msg = msg;
    }
}
