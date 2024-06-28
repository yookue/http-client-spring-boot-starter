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

package com.yookue.springstarter.httpclient.property;


import java.io.Serializable;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.SystemUtils;
import org.apache.hc.client5.http.AuthenticationStrategy;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.SchemePortResolver;
import org.apache.hc.client5.http.UserTokenHandler;
import org.apache.hc.client5.http.auth.AuthSchemeFactory;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.cookie.CookieSpecFactory;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.protocol.RedirectStrategy;
import org.apache.hc.client5.http.routing.HttpRoutePlanner;
import org.apache.hc.core5.http.ConnectionReuseStrategy;
import org.apache.hc.core5.http.HttpRequestInterceptor;
import org.apache.hc.core5.http.HttpResponseInterceptor;
import org.apache.hc.core5.util.VersionInfo;
import org.springframework.boot.convert.DurationUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Abstract properties for http client
 *
 * @author David Hsing
 * @see org.apache.hc.client5.http.impl.classic.HttpClientBuilder
 * @see org.apache.hc.client5.http.impl.async.HttpAsyncClientBuilder
 */
@Getter
@Setter
@ToString
public abstract class AbstractHttpClientProperties implements Serializable {
    /**
     * Indicates whether to enable this starter or not
     * <p>
     * Default is {@code true}
     */
    private Boolean enabled = true;

    private String userAgent = "Apache-HttpClient";    // $NON-NLS-1$

    private Boolean authCachingEnabled;
    private Boolean automaticRetriesEnabled;
    private Boolean connectionManagerShared;
    private Boolean connectionStateEnabled;
    private Boolean cookieManagementEnabled;
    private Boolean evictExpiredConnections;
    private Boolean evictIdleConnections;
    private Boolean redirectHandlingEnabled;
    private Boolean useSystemProperties;

    @DurationUnit(value = ChronoUnit.SECONDS)
    private Duration maxIdleTime;

    private Class<? extends HttpRoutePlanner> routePlanner;
    private Class<? extends CookieStore> cookieStore;
    private Class<? extends ConnectionKeepAliveStrategy> keepAliveStrategy;
    private Class<? extends ConnectionReuseStrategy> reuseStrategy;
    private Class<? extends AuthenticationStrategy> proxyAuthStrategy;
    private Class<? extends AuthenticationStrategy> targetAuthStrategy;
    private Class<? extends RedirectStrategy> redirectStrategy;
    private Class<? extends HttpRequestRetryStrategy> retryStrategy;
    private Class<? extends CredentialsProvider> credentialsProvider;
    private Class<? extends SchemePortResolver> schemePortResolver;
    private Class<? extends UserTokenHandler> userTokenHandler;
    private List<Class<? extends HttpRequestInterceptor>> requestInterceptors;
    private List<Class<? extends HttpResponseInterceptor>> responseInterceptors;
    private Map<String, Class<? extends AuthSchemeFactory>> authSchemeFactories;
    private Map<String, Class<? extends CookieSpecFactory>> cookieSpecFactories;
    private Map<String, String> defaultHeaders;
    private final DefaultRequestConfig defaultRequestConfig = new DefaultRequestConfig();

    public AbstractHttpClientProperties() {
        VersionInfo versionInfo = VersionInfo.loadVersionInfo("org.apache.hc.core5", null);    // $NON-NLS-1$
        if (versionInfo != null) {
            userAgent += String.format("/%s (Java/%s)", versionInfo.getRelease(), SystemUtils.JAVA_VERSION);    // $NON-NLS-1$
        }
    }


    /**
     * Properties for constructing default {@link org.apache.hc.client5.http.config.RequestConfig}
     *
     * @author David Hsing
     */
    @Getter
    @Setter
    @ToString
    public static class DefaultRequestConfig implements Serializable {
        private Boolean enabled;
        private Boolean expectContinueEnabled;
        private String proxyHost;
        private Integer proxyPort;
        private String cookieSpec;
        private Boolean redirectsEnabled = Boolean.TRUE;
        private Boolean circularRedirectsAllowed;
        private Integer maxRedirects;
        private Boolean authenticationEnabled = Boolean.TRUE;
        private List<String> targetPreferredAuthSchemes;
        private List<String> proxyPreferredAuthSchemes;

        @DurationUnit(value = ChronoUnit.SECONDS)
        private Duration connectTimeout = Duration.ofSeconds(30L);

        @DurationUnit(value = ChronoUnit.SECONDS)
        private Duration requestTimeout = Duration.ofSeconds(30L);

        @DurationUnit(value = ChronoUnit.SECONDS)
        private Duration responseTimeout = Duration.ofMinutes(3L);

        private Duration connectionKeepAlive;
        private Boolean contentCompressionEnabled = Boolean.TRUE;
        private Boolean hardCancellationEnabled = Boolean.TRUE;
    }
}
