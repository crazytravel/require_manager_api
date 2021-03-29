/*
 * Copyright (c) 2020 Mercedes-Benz. All rights reserved.
 */
package cc.iteck.rm.exception;

import lombok.Getter;

@Getter
public class ResponseCodeException extends RuntimeException {

    private int responseCode;

    public ResponseCodeException(int responseCode) {
        this.responseCode = responseCode;
    }

    public ResponseCodeException(int responseCode, String responseBody) {
        super(responseBody);
        this.responseCode = responseCode;
    }
}
