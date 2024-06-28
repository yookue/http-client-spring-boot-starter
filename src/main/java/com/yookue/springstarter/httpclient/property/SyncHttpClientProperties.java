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
import org.apache.hc.client5.http.classic.BackoffManager;
import org.apache.hc.client5.http.classic.ConnectionBackoffStrategy;
import org.apache.hc.client5.http.classic.ExecChainHandler;
import org.apache.hc.client5.http.entity.InputStreamFactory;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.impl.io.HttpRequestExecutor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * Properties for sync http client
 *
 * @author David Hsing
 * @see com.yookue.springstarter.httpclient.property.AbstractHttpClientProperties
 */
@Getter
@Setter
@ToString
public class SyncHttpClientProperties extends AbstractHttpClientProperties {
    private Boolean defaultUserAgentEnabled;
    private Class<? extends HttpClientConnectionManager> connectionManager;
    private Class<? extends HttpRequestExecutor> requestExecutor;
    private Class<? extends BackoffManager> backoffManager;
    private Class<? extends ConnectionBackoffStrategy> connectionBackoffStrategy;
    private Map<String, Class<? extends ExecChainHandler>> execInterceptors;
    private Map<String, Class<? extends InputStreamFactory>> contentDecoderFactories;
}
