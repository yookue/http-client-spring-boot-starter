/*
 * Copyright (c) 2020 Yookue Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yookue.springstarter.httpclient.exception;


import org.apache.hc.core5.http.HttpException;


/**
 * {@link org.apache.hc.core5.http.HttpException} for representing unexpected response
 *
 * @author David Hsing
 * @see org.apache.hc.core5.http.NoHttpResponseException
 */
@SuppressWarnings("unused")
public class UnexpectedHttpResponseException extends HttpException {
    /**
     * Create a new exception with the specified detail message
     *
     * @param message exception message
     */
    public UnexpectedHttpResponseException(String message) {
        super(message);
    }

    /**
     * Create a new exception with the specified detail message and cause
     *
     * @param message the exception detail message
     * @param cause the {@code Throwable} that caused this exception, or {@code null}
     */
    public UnexpectedHttpResponseException(String message, Throwable cause) {
        super(message);
    }
}
