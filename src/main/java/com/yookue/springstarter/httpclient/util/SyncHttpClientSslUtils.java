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


import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import javax.net.ssl.SSLContext;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.URIScheme;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.ssl.SSLContextBuilder;


/**
 * Utilities for building http client with SSL connections
 *
 * @author David Hsing
 * @reference "https://www.cnblogs.com/huiy/p/14761639.html"
 * @reference "https://www.zhyea.com/2018/10/22/resolve-pkix-unable-to-find-valid-certification-path-to-requested-target.html"
 */
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public abstract class SyncHttpClientSslUtils {
    @Nonnull
    @SuppressWarnings({})
    public static SSLConnectionSocketFactory connectionSocketFactoryTrusting() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        SSLContext context = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
        return SSLConnectionSocketFactoryBuilder.create().setSslContext(context).build();
    }

    @Nonnull
    public static PoolingHttpClientConnectionManagerBuilder poolingConnectionManagerBuilderTrusting() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        return PoolingHttpClientConnectionManagerBuilder.create().setSSLSocketFactory(connectionSocketFactoryTrusting());
    }

    @Nonnull
    public static BasicHttpClientConnectionManager basicConnectionManagerTrusting() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        RegistryBuilder<ConnectionSocketFactory> builder = RegistryBuilder.create();
        Registry<ConnectionSocketFactory> registry = builder.register(URIScheme.HTTP.id, PlainConnectionSocketFactory.getSocketFactory()).register(URIScheme.HTTPS.id, connectionSocketFactoryTrusting()).build();
        return new BasicHttpClientConnectionManager(registry);
    }

    @Nonnull
    public static PoolingHttpClientConnectionManager poolingConnectionManagerTrusting() throws KeyManagementException, KeyStoreException, NoSuchAlgorithmException {
        return poolingConnectionManagerBuilderTrusting().build();
    }

    @Nullable
    public static SSLConnectionSocketFactory connectionSocketFactoryTrustingQuietly() {
        try {
            return connectionSocketFactoryTrusting();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static PoolingHttpClientConnectionManagerBuilder poolingConnectionManagerBuilderTrustingQuietly() {
        try {
            return poolingConnectionManagerBuilderTrusting();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static BasicHttpClientConnectionManager basicConnectionManagerTrustingQuietly() {
        try {
            return basicConnectionManagerTrusting();
        } catch (Exception ignored) {
        }
        return null;
    }

    @Nullable
    public static PoolingHttpClientConnectionManager poolingConnectionManagerTrustingQuietly() {
        try {
            return poolingConnectionManagerTrusting();
        } catch (Exception ignored) {
        }
        return null;
    }
}
