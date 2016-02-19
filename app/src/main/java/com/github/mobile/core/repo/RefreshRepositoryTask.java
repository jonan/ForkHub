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
package com.github.mobile.core.repo;

import com.google.inject.Inject;

import com.github.mobile.ui.ProgressDialogTask;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

/**
 * Task to refresh a repository
 */
public class RefreshRepositoryTask extends ProgressDialogTask<Repository> {

    private static final String TAG = "RefreshRepositoryTask";

    @Inject
    private RepositoryService service;

    private final IRepositoryIdProvider repo;

    /**
     * Create task for context and id provider
     *
     * @param context
     * @param repo
     */
    public RefreshRepositoryTask(Context context, IRepositoryIdProvider repo) {
        super(context);

        this.repo = repo;
    }

    @Override
    protected Repository run(Account account) throws Exception {
        return service.getRepository(repo);
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception loading repository", e);
    }
}