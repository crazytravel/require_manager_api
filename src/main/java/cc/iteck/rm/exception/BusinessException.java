/*
 * Copyright (c) 2019 Mercedes-Benz. All rights reserved.
 */
package cc.iteck.rm.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, String msg) {
        super(msg);
        this.errorCode = errorCode;
    }

}
