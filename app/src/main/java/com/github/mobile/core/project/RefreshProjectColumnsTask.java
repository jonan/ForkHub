/*
 * Copyright 2017 Jon Ander Peñalba
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
package com.github.mobile.core.project;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.github.mobile.api.model.Project;
import com.github.mobile.api.model.ProjectColumn;
import com.github.mobile.api.service.PaginationService;
import com.github.mobile.api.service.ProjectService;
import com.github.mobile.ui.ProgressDialogTask;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collection;

/**
 * Task to refresh a project's columns
 */
public class RefreshProjectColumnsTask extends ProgressDialogTask<Collection<ProjectColumn>> {

    private static final String TAG = "RefreshProjectColumnsTask";

    @Inject
    private ProjectService service;

    private final Project project;

    /**
     * Create task for context and project
     *
     * @param context
     * @param project
     */
    public RefreshProjectColumnsTask(Context context, Project project) {
        super(context);

        this.project = project;
    }

    @Override
    protected Collection<ProjectColumn> run(Account account) throws Exception {
        return new PaginationService<ProjectColumn>() {
            @Override
            public Collection<ProjectColumn> getSinglePage(int page, int itemsPerPage) throws IOException {
                return service.getColumns(project.id, page).execute().body();
            }
        }.getAll();
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception loading columns for project", e);
    }
}
