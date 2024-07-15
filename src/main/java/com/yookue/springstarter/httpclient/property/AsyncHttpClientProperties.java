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


import java.util.Map;
import java.util.concurrent.ThreadFactory;
import org.apache.hc.client5.http.async.AsyncExecChainHandler;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.core5.http.config.CharCodingConfig;
import org.apache.hc.core5.http.config.Http1Config;
import org.apache.hc.core5.http2.config.H2Config;
import org.apache.hc.core5.reactor.IOReactorConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for async http client
 *
 * @author David Hsing
 * @see com.yookue.springstarter.httpclient.property.AbstractHttpClientProperties
 */
@Getter
@Setter
@ToString
public class AsyncHttpClientProperties extends AbstractHttpClientProperties {
    private Class<? extends AsyncClientConnectionManager> connectionManager;
    private Class<? extends CharCodingConfig> charCodingConfig;
    private Class<? extends Http1Config> h1Config;
    private Class<? extends H2Config> h2Config;
    private Class<? extends IOReactorConfig> ioReactorConfig;
    private Class<? extends ThreadFactory> threadFactory;
    private Map<String, Class<? extends AsyncExecChainHandler>> execInterceptors;
}
