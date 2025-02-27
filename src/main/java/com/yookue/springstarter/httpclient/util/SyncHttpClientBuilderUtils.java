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


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.http.config.LookupRegistryUtils;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import com.yookue.springstarter.httpclient.property.SyncHttpClientProperties;


/**
 * Utilities for building http client with {@link com.yookue.springstarter.httpclient.property.SyncHttpClientProperties}
 *
 * @author David Hsing
 * @see com.yookue.springstarter.httpclient.property.SyncHttpClientProperties
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "DuplicatedCode"})
public abstract class SyncHttpClientBuilderUtils {
    @Nonnull
    public static HttpClientBuilder clientBuilder(@Nonnull SyncHttpClientProperties properties) throws BeanInstantiationException {
        HttpClientBuilder builder = HttpClientBuilder.create();
        if (StringUtils.isNotBlank(properties.getProxyHost()) && properties.getProxyPort() != null && properties.getProxyPort() > 0) {
            builder.setProxy(new HttpHost(properties.getProxyHost(), properties.getProxyPort()));
        }
        if (BooleanUtils.isFalse(properties.getAuthCachingEnabled())) {
            builder.disableAuthCaching();
        }
        if (BooleanUtils.isFalse(properties.getAutomaticRetriesEnabled())) {
            builder.disableAutomaticRetries();
        }
        builder.setConnectionManagerShared(BooleanUtils.isTrue(properties.getConnectionManagerShared()));
        if (BooleanUtils.isFalse(properties.getConnectionStateEnabled())) {
            builder.disableConnectionState();
        }
        if (BooleanUtils.isFalse(properties.getCookieManagementEnabled())) {
            builder.disableCookieManagement();
        }
        if (BooleanUtils.isTrue(properties.getEvictExpiredConnections())) {
            builder.evictExpiredConnections();
        }
        if (BooleanUtils.isTrue(properties.getEvictIdleConnections()) && properties.getMaxIdleTime() != null) {
            builder.evictIdleConnections(TimeValue.ofMilliseconds(properties.getMaxIdleTime().toMillis()));
        }
        if (BooleanUtils.isFalse(properties.getRedirectHandlingEnabled())) {
            builder.disableRedirectHandling();
        }
        if (BooleanUtils.isTrue(properties.getUseSystemProperties())) {
            builder.useSystemProperties();
        }
        if (properties.getRoutePlanner() != null) {
            builder.setRoutePlanner(BeanUtils.instantiateClass(properties.getRoutePlanner()));
        }
        if (properties.getCookieStore() != null) {
            builder.setDefaultCookieStore(BeanUtils.instantiateClass(properties.getCookieStore()));
        }
        if (properties.getKeepAliveStrategy() != null) {
            builder.setKeepAliveStrategy(BeanUtils.instantiateClass(properties.getKeepAliveStrategy()));
        }
        if (properties.getReuseStrategy() != null) {
            builder.setConnectionReuseStrategy(BeanUtils.instantiateClass(properties.getReuseStrategy()));
        }
        if (properties.getProxyAuthStrategy() != null) {
            builder.setProxyAuthenticationStrategy(BeanUtils.instantiateClass(properties.getProxyAuthStrategy()));
        }
        if (properties.getTargetAuthStrategy() != null) {
            builder.setTargetAuthenticationStrategy(BeanUtils.instantiateClass(properties.getTargetAuthStrategy()));
        }
        if (properties.getRedirectStrategy() != null) {
            builder.setRedirectStrategy(BeanUtils.instantiateClass(properties.getRedirectStrategy()));
        }
        if (properties.getCredentialsProvider() != null) {
            builder.setDefaultCredentialsProvider(BeanUtils.instantiateClass(properties.getCredentialsProvider()));
        }
        if (properties.getSchemePortResolver() != null) {
            builder.setSchemePortResolver(BeanUtils.instantiateClass(properties.getSchemePortResolver()));
        }
        if (properties.getUserTokenHandler() != null) {
            builder.setUserTokenHandler(BeanUtils.instantiateClass(properties.getUserTokenHandler()));
        }
        if (!CollectionUtils.isEmpty(properties.getRequestInterceptors())) {
            for (Class<? extends HttpRequestInterceptor> interceptor : properties.getRequestInterceptors()) {
                builder.addRequestInterceptorLast(BeanUtils.instantiateClass(interceptor));
            }
        }
        if (!CollectionUtils.isEmpty(properties.getResponseInterceptors())) {
            for (Class<? extends HttpResponseInterceptor> interceptor : properties.getResponseInterceptors()) {
                builder.addResponseInterceptorLast(BeanUtils.instantiateClass(interceptor));
            }
        }
        if (!CollectionUtils.isEmpty(properties.getAuthSchemeFactories())) {
            Map<String, AuthSchemeFactory> nameFactories = new LinkedHashMap<>();
            for (Map.Entry<String, Class<? extends AuthSchemeFactory>> entry : properties.getAuthSchemeFactories().entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                    nameFactories.put(entry.getKey(), BeanUtils.instantiateClass(entry.getValue()));
                }
            }
            if (!CollectionUtils.isEmpty(nameFactories)) {
                builder.setDefaultAuthSchemeRegistry(LookupRegistryUtils.registryWithin(nameFactories));
            }
        }
        if (!CollectionUtils.isEmpty(properties.getCookieSpecFactories())) {
            Map<String, CookieSpecFactory> nameFactories = new LinkedHashMap<>();
            for (Map.Entry<String, Class<? extends CookieSpecFactory>> entry : properties.getCookieSpecFactories().entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                    nameFactories.put(entry.getKey(), BeanUtils.instantiateClass(entry.getValue()));
                }
            }
            if (!CollectionUtils.isEmpty(nameFactories)) {
                builder.setDefaultCookieSpecRegistry(LookupRegistryUtils.registryWithin(nameFactories));
            }
        }
        // Default headers
        if (!CollectionUtils.isEmpty(properties.getDefaultHeaders())) {
            List<Header> headers = new ArrayList<>(properties.getDefaultHeaders().size());
            for (Map.Entry<String, String> entry : properties.getDefaultHeaders().entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey())) {
                    headers.add(new BasicHeader(entry.getKey(), entry.getValue()));
                }
            }
            builder.setDefaultHeaders(headers);
        }
        // Default request config
        RequestConfig requestConfig = AbstractHttpClientBuilderUtils.buildRequestConfig(properties.getDefaultRequestConfig());
        if (requestConfig != null) {
            builder.setDefaultRequestConfig(requestConfig);
        }
        // Sync customized properties
        if (StringUtils.isNotBlank(properties.getUserAgent())) {
            builder.setUserAgent(properties.getUserAgent());
        }
        if (BooleanUtils.isFalse(properties.getDefaultUserAgentEnabled())) {
            builder.disableDefaultUserAgent();
        }
        if (properties.getConnectionManager() != null) {
            builder.setConnectionManager(BeanUtils.instantiateClass(properties.getConnectionManager()));
        }
        if (properties.getRequestExecutor() != null) {
            builder.setRequestExecutor(BeanUtils.instantiateClass(properties.getRequestExecutor()));
        }
        if (properties.getBackoffManager() != null) {
            builder.setBackoffManager(BeanUtils.instantiateClass(properties.getBackoffManager()));
        }
        if (properties.getConnectionBackoffStrategy() != null) {
            builder.setConnectionBackoffStrategy(BeanUtils.instantiateClass(properties.getConnectionBackoffStrategy()));
        }
        if (!CollectionUtils.isEmpty(properties.getExecInterceptors())) {
            for (Map.Entry<String, Class<? extends ExecChainHandler>> entry : properties.getExecInterceptors().entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                    builder.addExecInterceptorLast(entry.getKey(), BeanUtils.instantiateClass(entry.getValue()));
                }
            }
        }
        if (!CollectionUtils.isEmpty(properties.getContentDecoderFactories())) {
            LinkedHashMap<String, InputStreamFactory> nameFactories = new LinkedHashMap<>();
            for (Map.Entry<String, Class<? extends InputStreamFactory>> entry : properties.getContentDecoderFactories().entrySet()) {
                if (StringUtils.isNotBlank(entry.getKey()) && entry.getValue() != null) {
                    nameFactories.put(entry.getKey(), BeanUtils.instantiateClass(entry.getValue()));
                }
            }
            if (!CollectionUtils.isEmpty(nameFactories)) {
                builder.setContentDecoderRegistry(nameFactories);
            }
        }
        return builder;
    }
}
