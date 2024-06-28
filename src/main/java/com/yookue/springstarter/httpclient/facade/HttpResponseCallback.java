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

package com.yookue.springstarter.httpclient.facade;


import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.hc.core5.http.HttpException;
import com.yookue.springstarter.httpclient.support.HttpResponsePacket;


/**
 * Interface for processing http response
 *
 * @author David Hsing
 * @see org.apache.hc.core5.http.io.HttpClientResponseHandler
 */
@FunctionalInterface
public interface HttpResponseCallback {
    @SuppressWarnings("RedundantThrows")
    void process(@Nonnull HttpResponsePacket packet) throws HttpException, IOException;
}
