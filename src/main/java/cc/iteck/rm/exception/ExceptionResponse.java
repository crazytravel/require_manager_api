/*
 * Copyright (c) 2019 Mercedes-Benz. All rights reserved.
 */
package cc.iteck.rm.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ExceptionResponse {
    private Date timestamp;
    private int status;
    private String errorCode;
    private String message;
    private String path;
}
