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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.util.CollectionUtils;
import com.yookue.springstarter.httpclient.facade.HttpResponseCallback;
import com.yookue.springstarter.httpclient.support.HttpResponsePacket;
import lombok.extern.slf4j.Slf4j;


/**
 * Utilities for fetching and handling http response
 *
 * @author David Hsing
 * @see org.apache.hc.client5.http.entity.EntityBuilder
 * @see org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder
 * @see org.apache.hc.core5.http.io.support.ClassicRequestBuilder
 * @reference "https://hc.apache.org/httpcomponents-client-5.1.x/examples.html"
 */
@Slf4j
@SuppressWarnings({"unused", "BooleanMethodIsAlwaysInverted", "UnusedReturnValue", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public abstract class SyncHttpClientExecutorUtils {
    @Nonnull
    public static HttpResponsePacket fetchResponse(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request) throws IOException {
        return fetchResponse(client, request, null);
    }

    @Nonnull
    public static HttpResponsePacket fetchResponse(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request, @Nullable HttpContext context) throws IOException {
        HttpClientResponseHandler<HttpResponsePacket> handler = new HttpClientResponseHandler<HttpResponsePacket>() {
            @Nonnull
            @Override
            public HttpResponsePacket handleResponse(@Nonnull ClassicHttpResponse response) {
                return new HttpResponsePacket(response);
            }
        };
        return client.execute(request, handler);
    }

    public static void executeCallback(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request, @Nonnull HttpResponseCallback callback) throws HttpException, IOException, URISyntaxException {
        executeCallback(client, request, null, callback);
    }

    public static void executeCallback(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request, @Nullable HttpContext context, @Nonnull HttpResponseCallback callback) throws HttpException, IOException, URISyntaxException {
        executeCallback(client, request, null, callback, false);
    }

    public static void executeCallback(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request, @Nullable HttpContext context, @Nonnull HttpResponseCallback callback, boolean closeResponse) throws HttpException, IOException, URISyntaxException {
        if (log.isDebugEnabled()) {
            log.debug("Preparing to visit: {}", request.getUri());
        }
        if (!request.containsHeader(HttpHeaders.ACCEPT)) {
            request.addHeader(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
        }
        HttpResponse response = client.execute(request, context);
        if (response != null) {
            callback.process(new HttpResponsePacket(response));
        }
        if (closeResponse && response instanceof CloseableHttpResponse) {
            ((CloseableHttpResponse) response).close();
        }
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String pathname) throws IOException {
        return downloadSimply(client, uri, pathname, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String pathname, @Nullable RequestConfig config) throws IOException {
        if (StringUtils.isNoneBlank(uri, pathname)) {
            return downloadSimply(client, uri, Files.newOutputStream(Paths.get(pathname)), config);
        }
        return false;
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output) throws IOException {
        return downloadSimply(client, uri, output, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output, @Nullable RequestConfig config) throws IOException {
        return downloadSimply(client, uri, output, config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IOException {
        return downloadSimply(client, uri, Files.newOutputStream(output.toPath()), config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output) throws IOException {
        return downloadSimply(client, uri, output, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output, @Nullable RequestConfig config) throws IOException {
        return downloadSimply(client, uri, output, config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IOException {
        if (StringUtils.isNotBlank(uri)) {
            config = ObjectUtils.defaultIfNull(config, RequestConfigUtils.withDefaultTimeouts());
            charset = ObjectUtils.defaultIfNull(charset, StandardCharsets.UTF_8);
            ClassicRequestBuilder builder = ClassicRequestBuilder.get(uri).setCharset(charset);
            if (!CollectionUtils.isEmpty(parameters)) {
                parameters.stream().filter(Objects::nonNull).forEach(builder::addParameter);
            }
            ClassicHttpRequest request = builder.build();
            if (request instanceof HttpUriRequestBase) {
                ((HttpUriRequestBase) request).setConfig(config);
            }
            HttpClientResponseHandler<Boolean> handler = new HttpClientResponseHandler<Boolean>() {
                @Nonnull
                @Override
                public Boolean handleResponse(@Nonnull ClassicHttpResponse response) throws IOException {
                    if (response.getCode() == HttpStatus.SC_OK && response.getEntity() != null) {
                        response.getEntity().writeTo(output);
                        output.close();
                        return true;
                    }
                    return false;
                }
            };
            return client.execute(request, handler);
        }
        return false;
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input) throws IllegalAccessException, IOException {
        return uploadSimply(client, uri, formField, input, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input, @Nullable RequestConfig config) throws IllegalAccessException, IOException {
        return uploadSimply(client, uri, formField, input, config, null, null);
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IllegalAccessException, IOException {
        if (StringUtils.isNoneBlank(uri, formField)) {
            if (!input.exists() || !input.isFile()) {
                throw new FileNotFoundException();
            }
            if (!input.canRead()) {
                throw new IllegalAccessException();
            }
            config = ObjectUtils.defaultIfNull(config, RequestConfigUtils.withDefaultTimeouts());
            charset = ObjectUtils.defaultIfNull(charset, StandardCharsets.UTF_8);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create().setCharset(charset);
            builder.addBinaryBody(formField, input, ContentType.MULTIPART_FORM_DATA, input.getName());
            if (!CollectionUtils.isEmpty(parameters)) {
                parameters.stream().filter(Objects::nonNull).forEach(pair -> builder.addTextBody(pair.getName(), pair.getValue()));
            }
            ClassicHttpRequest request = ClassicRequestBuilder.post(uri).setCharset(charset).build();
            if (request instanceof HttpUriRequestBase) {
                ((HttpUriRequestBase) request).setConfig(config);
            }
            HttpResponsePacket packet = fetchResponse(client, request);
            return packet.isOkStatus();
        }
        return false;
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname) throws IllegalAccessException, IOException {
        return uploadSimply(client, uri, formField, pathname, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname, @Nullable RequestConfig config) throws IllegalAccessException, IOException {
        return uploadSimply(client, uri, formField, pathname, config, null, null);
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IllegalAccessException, IOException {
        if (StringUtils.isNoneBlank(uri, formField, pathname)) {
            return uploadSimply(client, uri, formField, new File(pathname), config, parameters, charset);
        }
        return false;
    }
}
