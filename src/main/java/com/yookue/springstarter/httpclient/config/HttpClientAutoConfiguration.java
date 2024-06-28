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

package com.yookue.springstarter.httpclient.config;


import javax.annotation.Nonnull;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.core5.http.HttpVersion;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import com.yookue.springstarter.httpclient.property.HttpClientProperties;
import com.yookue.springstarter.httpclient.util.AsyncHttpClientBuilderUtils;
import com.yookue.springstarter.httpclient.util.SyncHttpClientBuilderUtils;


/**
 * Configuration for sync http client
 *
 * @author David Hsing
 * @see org.apache.hc.client5.http.impl.classic.HttpClients
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = HttpClientAutoConfiguration.PROPERTIES_PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnClass(value = HttpVersion.class)
@EnableConfigurationProperties(value = HttpClientProperties.class)
@AutoConfigureOrder(Ordered.LOWEST_PRECEDENCE - 1000)
public class HttpClientAutoConfiguration {
    public static final String PROPERTIES_PREFIX = "spring.http-client";    // $NON-NLS-1$
    public static final String SYNC_HTTP_CLIENT = "syncHttpClient";    // $NON-NLS-1$
    public static final String ASYNC_HTTP_CLIENT = "asyncHttpClient";    // $NON-NLS-1$

    @Bean(name = SYNC_HTTP_CLIENT)
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX + ".sync-client", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(value = HttpClient.class)
    @ConditionalOnMissingBean(name = SYNC_HTTP_CLIENT)
    public HttpClient syncHttpClient(@Nonnull HttpClientProperties properties) {
        return SyncHttpClientBuilderUtils.clientBuilder(properties.getSyncClient()).build();
    }

    @Bean(name = ASYNC_HTTP_CLIENT)
    @ConditionalOnProperty(prefix = PROPERTIES_PREFIX + ".async-client", name = "enabled", havingValue = "true", matchIfMissing = true)
    @ConditionalOnClass(value = HttpAsyncClient.class)
    @ConditionalOnMissingBean(name = ASYNC_HTTP_CLIENT)
    public HttpAsyncClient asyncHttpClient(@Nonnull HttpClientProperties properties) {
        return AsyncHttpClientBuilderUtils.clientBuilder(properties.getAsyncClient()).build();
    }
}
