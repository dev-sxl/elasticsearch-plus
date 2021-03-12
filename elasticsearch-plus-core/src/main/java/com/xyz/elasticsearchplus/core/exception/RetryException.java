package com.xyz.elasticsearchplus.core.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RetryException extends CommonException {
    public RetryException(String msg) {
        this(null, msg);
    }

    public RetryException(String code, String msg) {
        super(code, msg);
    }

    public RetryException(String code, String msg, Throwable t) {
        super(code, msg, t);
    }
}
