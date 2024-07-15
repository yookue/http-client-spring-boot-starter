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

package com.yookue.springstarter.httpclient.util;


import jakarta.annotation.Nullable;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.util.Timeout;


/**
 * Utilities for {@link org.apache.hc.client5.http.config.RequestConfig}
 *
 * @author David Hsing
 * @see org.apache.hc.client5.http.config.RequestConfig
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class RequestConfigUtils {
    public static final Timeout REQUEST_TIMEOUT = Timeout.ofSeconds(30L);
    public static final Timeout CONNECT_TIMEOUT = Timeout.ofSeconds(30L);
    public static final Timeout RESPONSE_TIMEOUT = Timeout.ofMinutes(3L);

    /**
     * Return a {@code RequestConfig} with default timeout settings
     *
     * @return a {@code RequestConfig} with default timeout settings
     */
    public static RequestConfig withDefaultTimeouts() {
        return withSpecifiedTimeouts(REQUEST_TIMEOUT, CONNECT_TIMEOUT, RESPONSE_TIMEOUT);
    }

    /**
     * Return a {@code RequestConfig} with specified timeout settings
     *
     * @param requestTimeout connect request timeout
     * @param connectTimeout connect timeout
     * @param responseTimeout response timeout
     * @return a {@code RequestConfig} with specified timeout settings
     */
    public static RequestConfig withSpecifiedTimeouts(@Nullable Timeout requestTimeout, @Nullable Timeout connectTimeout, @Nullable Timeout responseTimeout) {
        return RequestConfig.custom().setConnectionRequestTimeout(requestTimeout).setResponseTimeout(responseTimeout).build();
    }

    /**
     * Return a {@code RequestConfig} restricted with default timeout settings if it is infinity
     *
     * @param config a {@code RequestConfig} that may have infinity timeout settings
     * @return a {@code RequestConfig} restricted with default timeout settings if it is infinity
     */
    @Nullable
    public static RequestConfig restrictWithDefaultTimeouts(@Nullable RequestConfig config) {
        if (config == null) {
            return null;
        }
        RequestConfig.Builder builder = RequestConfig.copy(config);
        if (config.getConnectionRequestTimeout() == null || config.getConnectionRequestTimeout().isDisabled()) {
            builder.setConnectionRequestTimeout(REQUEST_TIMEOUT);
        }
        if (config.getResponseTimeout() == null || config.getResponseTimeout().isDisabled()) {
            builder.setResponseTimeout(RESPONSE_TIMEOUT);
        }
        return builder.build();
    }
}
