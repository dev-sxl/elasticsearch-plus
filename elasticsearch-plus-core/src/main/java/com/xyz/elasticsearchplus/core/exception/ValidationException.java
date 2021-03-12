package com.xyz.elasticsearchplus.core.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ValidationException extends CommonException {
    public ValidationException(String msg) {
        this(null, msg);
    }

    public ValidationException(String code, String msg) {
        super(code, msg);
    }

    public ValidationException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }
}
