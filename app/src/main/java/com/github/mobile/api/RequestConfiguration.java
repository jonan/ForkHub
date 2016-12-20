/*
 * Copyright 2016 Jon Ander Peñalba
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.mobile.api;

import android.text.TextUtils;

import com.github.mobile.accounts.GitHubAccount;
import com.google.inject.Provider;

import org.eclipse.egit.github.core.util.EncodingUtils;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

public class RequestConfiguration implements okhttp3.Interceptor {

    private static final String HEADER_USER_AGENT = "ForkHub/2.0";
    private static final String HEADER_ACCEPT = "application/vnd.github.v3.full+json";

    private final Provider<GitHubAccount> accountProvider;

    public RequestConfiguration(final Provider<GitHubAccount> accountProvider) {
        this.accountProvider = accountProvider;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();

        String credentials;

        GitHubAccount account = accountProvider.get();
        String token = account.getAuthToken();
        if (!TextUtils.isEmpty(token)) {
            credentials = "token " + token;
        } else {
            credentials = "Basic " + EncodingUtils.toBase64(account.getUsername() + ':' + account.getPassword());
        }

        Request newRequest = originalRequest.newBuilder()
                .header("Authorization", credentials)
                .header("User-Agent", HEADER_USER_AGENT)
                .addHeader("Accept", HEADER_ACCEPT)
                .build();

        return chain.proceed(newRequest);
    }
}
