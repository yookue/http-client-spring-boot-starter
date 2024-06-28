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

package com.yookue.springstarter.httpclient;


import java.io.IOException;
import org.apache.hc.client5.http.classic.HttpClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import com.yookue.springstarter.httpclient.config.HttpClientAutoConfiguration;
import com.yookue.springstarter.httpclient.util.SyncHttpClientExecutorUtils;


@SpringBootTest(classes = MockApplicationInitializer.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class MockApplicationTest {
    @Autowired(required = false)
    @Qualifier(value = HttpClientAutoConfiguration.SYNC_HTTP_CLIENT)
    private HttpClient syncHttpClient;

    @Test
    void syncHttpClient() throws IOException {
        Assertions.assertNotNull(syncHttpClient, "Sync http client can not be null");
        String uri = "https://www.baidu.com/img/bd_logo.png", pathname = "D:/bd_logo.png";
        boolean download = SyncHttpClientExecutorUtils.downloadSimply(syncHttpClient, uri, pathname);
        Assertions.assertTrue(download, "downloadSimply must be true");
    }
}
