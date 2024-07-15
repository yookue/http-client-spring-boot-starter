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
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.springframework.util.CollectionUtils;
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
    public static <T> T executeHandler(@Nonnull HttpClient client, @Nonnull ClassicHttpRequest request, @Nullable HttpContext context, @Nonnull HttpClientResponseHandler<? extends T> handler) throws IOException, URISyntaxException {
        if (log.isDebugEnabled()) {
            log.debug("Preparing to visit: {}", request.getUri());
        }
        if (!request.containsHeader(HttpHeaders.ACCEPT)) {
            request.addHeader(HttpHeaders.ACCEPT, ContentType.WILDCARD.getMimeType());
        }
        return client.execute(request, context, handler);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String pathname) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, pathname, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String pathname, @Nullable RequestConfig config) throws IOException, URISyntaxException {
        return StringUtils.isNoneBlank(uri, pathname) && downloadSimply(client, uri, Files.newOutputStream(Paths.get(pathname)), config);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, output, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output, @Nullable RequestConfig config) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, output, config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull File output, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, Files.newOutputStream(output.toPath()), config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, output, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output, @Nullable RequestConfig config) throws IOException, URISyntaxException {
        return downloadSimply(client, uri, output, config, null, null);
    }

    public static boolean downloadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull OutputStream output, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IOException, URISyntaxException {
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
            HttpClientResponseHandler<Boolean> handler = (@Nonnull ClassicHttpResponse response) -> {
                if (response.getCode() == HttpStatus.SC_OK && response.getEntity() != null) {
                    response.getEntity().writeTo(output);
                    output.close();
                    return true;
                }
                return false;
            };
            return BooleanUtils.isTrue(executeHandler(client, request, null, handler));
        }
        return false;
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input) throws IllegalAccessException, IOException, URISyntaxException {
        return uploadSimply(client, uri, formField, input, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input, @Nullable RequestConfig config) throws IllegalAccessException, IOException, URISyntaxException {
        return uploadSimply(client, uri, formField, input, config, null, null);
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname) throws IllegalAccessException, IOException, URISyntaxException {
        return uploadSimply(client, uri, formField, pathname, RequestConfigUtils.withDefaultTimeouts());
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname, @Nullable RequestConfig config) throws IllegalAccessException, IOException, URISyntaxException {
        return uploadSimply(client, uri, formField, pathname, config, null, null);
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull String pathname, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IllegalAccessException, IOException, URISyntaxException {
        return StringUtils.isNoneBlank(uri, formField, pathname) && uploadSimply(client, uri, formField, new File(pathname), config, parameters, charset);
    }

    public static boolean uploadSimply(@Nonnull HttpClient client, @Nonnull String uri, @Nonnull String formField, @Nonnull File input, @Nullable RequestConfig config, @Nullable List<NameValuePair> parameters, @Nullable Charset charset) throws IllegalAccessException, IOException, URISyntaxException {
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
            HttpClientResponseHandler<Boolean> handler = (@Nonnull ClassicHttpResponse response) -> response.getCode() == HttpStatus.SC_OK;
            return BooleanUtils.isTrue(executeHandler(client, request, null, handler));
        }
        return false;
    }
}
