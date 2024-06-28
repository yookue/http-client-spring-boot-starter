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

package com.yookue.springstarter.httpclient.support;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.hc.client5.http.async.methods.SimpleBody;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.HttpStatus;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicClassicHttpResponse;
import org.apache.hc.core5.http.message.HttpResponseWrapper;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.CollectionUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;


/**
 * {@link org.apache.hc.core5.http.message.HttpResponseWrapper} for processing http response
 *
 * @author David Hsing
 * @see org.apache.hc.core5.http.HttpHeaders
 * @see org.apache.hc.core5.http.HttpStatus
 * @reference "https://hc.apache.org/httpcomponents-client-5.1.x/quickstart.html"
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@SuppressWarnings({"unused", "UnusedReturnValue", "JavadocDeclaration", "JavadocLinkAsPlainText"})
public class HttpResponsePacket extends HttpResponseWrapper implements DisposableBean {
    private final HttpResponse response;

    @Getter
    private LocalDateTime timestamp;

    public HttpResponsePacket(@Nonnull HttpResponse response) {
        super(response);
        this.response = response;
        updateTimestamp();
    }

    public void addHeaders(@Nonnull Header... headers) {
        if (ArrayUtils.isNotEmpty(headers)) {
            addHeaders(Arrays.asList(headers));
        }
    }

    public void addHeaders(@Nonnull Collection<Header> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            headers.stream().filter(Objects::nonNull).forEach(super::addHeader);
        }
    }

    public void closeResponse() throws IOException {
        if (response instanceof CloseableHttpResponse) {
            ((CloseableHttpResponse) response).close();
        }
    }

    public void closeResponseQuietly() {
        try {
            closeResponse();
        } catch (IOException ignored) {
        }
    }

    public void closeEntity() throws IOException {
        HttpEntity entity = getSyncEntity();
        if (entity != null) {
            EntityUtils.consume(entity);
        }
    }

    public void closeEntityQuietly() {
        try {
            closeEntity();
        } catch (IOException ignored) {
        }
    }

    @Nullable
    public Charset getCharset() {
        ContentType contentType = getContentTypeQuietly(false);
        return contentType == null ? null : contentType.getCharset();
    }

    @Nullable
    public String getMimeType() {
        ContentType contentType = getContentTypeQuietly(false);
        return contentType == null ? null : contentType.getMimeType();
    }

    @Nullable
    public ContentType getContentType() throws UnsupportedCharsetException {
        return getContentType(false);
    }

    /**
     * Return the "Content-Type" header value of the response
     *
     * @param strict the charset in "Content-Type" header must be "strict" supported by this environment, ignored in async response
     * @return the "Content-Type" header value of the response
     *
     * @throws UnsupportedCharsetException if the charset in "Content-Type" header is not supported
     */
    @Nullable
    public ContentType getContentType(boolean strict) throws UnsupportedCharsetException {
        if (response instanceof SimpleHttpResponse) {
            return ((SimpleHttpResponse) response).getContentType();
        }
        String contentType = getContentTypeAsString();
        if (StringUtils.isBlank(contentType)) {
            return null;
        }
        return strict ? ContentType.parse(contentType) : ContentType.parseLenient(contentType);
    }

    @Nullable
    public ContentType getContentTypeQuietly(boolean strict) {
        try {
            return getContentType(strict);
        } catch (UnsupportedCharsetException ignored) {
        }
        return null;
    }

    @Nullable
    public String getContentTypeAsString() {
        Header header = super.getFirstHeader(HttpHeaders.CONTENT_TYPE);
        return header == null ? null : header.getValue();
    }

    @Nullable
    public String getFirstHeaderAsString(@Nonnull String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Header header = super.getFirstHeader(name);
        return header == null ? null : header.getValue();
    }

    @Nullable
    public String getLastHeaderAsString(@Nonnull String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }
        Header header = super.getLastHeader(name);
        return header == null ? null : header.getValue();
    }

    @Nullable
    public HttpEntity getSyncEntity() {
        return (response instanceof BasicClassicHttpResponse) ? ((BasicClassicHttpResponse) response).getEntity() : null;
    }

    @Nullable
    public byte[] getSyncEntityAsBytes() throws IOException {
        HttpEntity entity = getSyncEntity();
        return entity == null ? null : EntityUtils.toByteArray(entity);
    }

    public String getSyncEntityAsString() throws IOException, ParseException {
        Charset charset = getCharset();
        if (charset != null && Charset.isSupported(charset.name())) {
            return getSyncEntityAsString(charset);
        }
        return getSyncEntityAsString(Charset.defaultCharset());
    }

    @Nullable
    public String getSyncEntityAsString(@Nonnull Charset charset) throws IOException, ParseException {
        HttpEntity entity = getSyncEntity();
        return entity == null ? null : EntityUtils.toString(entity, charset);
    }

    @Nullable
    public SimpleBody getAsyncBody() {
        return !(response instanceof SimpleHttpResponse) ? null : ((SimpleHttpResponse) response).getBody();
    }

    @Nullable
    public byte[] getAsyncBodyAsBytes() {
        SimpleBody body = getAsyncBody();
        return body == null ? null : body.getBodyBytes();
    }

    @Nullable
    public String getAsyncBodyAsString() {
        return getAsyncBodyAsString(ObjectUtils.defaultIfNull(getCharset(), StandardCharsets.US_ASCII));
    }

    @Nullable
    @SuppressWarnings("ConstantConditions")
    public String getAsyncBodyAsString(@Nonnull Charset charset) {
        if (!Charset.isSupported(charset.name())) {
            return null;
        }
        byte[] body = getAsyncBodyAsBytes();
        return ArrayUtils.isEmpty(body) ? null : StringUtils.toEncodedString(body, charset);
    }

    public boolean isAsyncBodyBytes() {
        SimpleBody body = getAsyncBody();
        return body != null && body.isBytes();
    }

    public boolean isAsyncBodyString() {
        SimpleBody body = getAsyncBody();
        return body != null && body.isText();
    }

    public boolean isAsyncResponse() {
        return response instanceof SimpleHttpResponse;
    }

    public boolean isOkStatus() {
        return super.getCode() == HttpStatus.SC_OK;
    }

    public boolean isSyncResponse() {
        return response instanceof BasicClassicHttpResponse;
    }

    public void removeHeaders(@Nonnull String... names) {
        if (ArrayUtils.isNotEmpty(names)) {
            removeHeaders(Arrays.asList(names));
        }
    }

    public void removeHeaders(@Nonnull Collection<String> names) {
        if (!CollectionUtils.isEmpty(names)) {
            names.stream().filter(StringUtils::isNotEmpty).forEach(super::removeHeaders);
        }
    }

    public void updateTimestamp() {
        timestamp = LocalDateTime.now();
    }

    @PreDestroy
    public void destroy() {
        closeEntityQuietly();
        closeResponseQuietly();
    }
}
