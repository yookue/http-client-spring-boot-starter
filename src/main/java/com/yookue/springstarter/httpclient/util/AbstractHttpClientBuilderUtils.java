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


import javax.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import com.yookue.springstarter.httpclient.property.AbstractHttpClientProperties;


/**
 * Utilities for building http client with {@link com.yookue.springstarter.httpclient.property.AbstractHttpClientProperties}
 *
 * @author David Hsing
 * @see com.yookue.springstarter.httpclient.property.AbstractHttpClientProperties
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue"})
public abstract class AbstractHttpClientBuilderUtils {
    @Nullable
    public static RequestConfig buildRequestConfig(@Nullable AbstractHttpClientProperties.DefaultRequestConfig properties) {
        if (properties == null || BooleanUtils.isFalse(properties.getEnabled())) {
            return null;
        }
        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setExpectContinueEnabled(BooleanUtils.isTrue(properties.getExpectContinueEnabled()));
        if (StringUtils.isNotBlank(properties.getProxyHost()) && properties.getProxyPort() != null && properties.getProxyPort() > 0) {
            builder.setProxy(new HttpHost(properties.getProxyHost(), properties.getProxyPort()));
        }
        if (StringUtils.isNotBlank(properties.getCookieSpec())) {
            builder.setCookieSpec(properties.getCookieSpec());
        }
        builder.setRedirectsEnabled(BooleanUtils.isTrue(properties.getRedirectsEnabled()));
        builder.setCircularRedirectsAllowed(BooleanUtils.isTrue(properties.getCircularRedirectsAllowed()));
        if (properties.getMaxRedirects() != null) {
            builder.setMaxRedirects(properties.getMaxRedirects());
        }
        builder.setAuthenticationEnabled(BooleanUtils.isTrue(properties.getAuthenticationEnabled()));
        builder.setTargetPreferredAuthSchemes(properties.getTargetPreferredAuthSchemes());
        builder.setProxyPreferredAuthSchemes(properties.getProxyPreferredAuthSchemes());
        if (properties.getRequestTimeout() != null) {
            builder.setConnectionRequestTimeout(Timeout.ofMilliseconds(properties.getRequestTimeout().toMillis()));
        }
        if (properties.getConnectTimeout() != null) {
            builder.setConnectTimeout(Timeout.ofMilliseconds(properties.getConnectTimeout().toMillis()));
        }
        if (properties.getResponseTimeout() != null) {
            builder.setResponseTimeout(Timeout.ofMilliseconds(properties.getResponseTimeout().toMillis()));
        }
        if (properties.getConnectionKeepAlive() != null) {
            builder.setConnectionKeepAlive(TimeValue.ofMilliseconds(properties.getConnectionKeepAlive().toMillis()));
        }
        builder.setContentCompressionEnabled(BooleanUtils.isTrue(properties.getContentCompressionEnabled()));
        builder.setHardCancellationEnabled(BooleanUtils.isTrue(properties.getHardCancellationEnabled()));
        return builder.build();
    }
}
