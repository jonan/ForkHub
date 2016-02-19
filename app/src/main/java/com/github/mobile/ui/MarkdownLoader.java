/*
 * Copyright 2012 GitHub Inc.
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
package com.github.mobile.ui;

import com.google.inject.Inject;

import com.github.mobile.accounts.AuthenticatedUserLoader;
import com.github.mobile.util.HtmlUtils;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.service.MarkdownService;

import android.accounts.Account;
import android.content.Context;
import android.text.Html.ImageGetter;
import android.util.Log;

import java.io.IOException;

import static org.eclipse.egit.github.core.service.MarkdownService.MODE_MARKDOWN;

/**
 * Markdown loader
 */
public class MarkdownLoader extends AuthenticatedUserLoader<CharSequence> {

    private static final String TAG = "MarkdownLoader";

    private final ImageGetter imageGetter;

    private final IRepositoryIdProvider repository;

    private final String raw;

    private boolean encode;

    @Inject
    private MarkdownService service;

    /**
     * @param context
     * @param repository
     * @param raw
     * @param imageGetter
     * @param encode
     */
    public MarkdownLoader(Context context, IRepositoryIdProvider repository,
            String raw, ImageGetter imageGetter, boolean encode) {
        super(context);

        this.repository = repository;
        this.raw = raw;
        this.imageGetter = imageGetter;
        this.encode = encode;
    }

    @Override
    protected CharSequence getAccountFailureData() {
        return null;
    }

    @Override
    public CharSequence load(Account account) {
        try {
            String html;
            if (repository != null)
                html = service.getRepositoryHtml(repository, raw);
            else
                html = service.getHtml(raw, MODE_MARKDOWN);

            if (encode)
                return HtmlUtils.encode(html, imageGetter);
            else
                return html;
        } catch (IOException e) {
            Log.d(TAG, "Loading rendered markdown failed", e);
            return null;
        }
    }
}
