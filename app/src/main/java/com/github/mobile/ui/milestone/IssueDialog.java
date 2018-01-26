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
package com.github.mobile.ui.milestone;

import android.accounts.Account;
import android.util.Log;

import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.api.service.IssueService;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.util.ToastUtils;

import org.eclipse.egit.github.core.IRepositoryIdProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Dialog helper to display a list of issues to select one from
 */
public class IssueDialog {

    private static final String TAG = "IssueDialog";

    private IssueService service;

    private ArrayList<Issue> repositoryIssues;

    private final int requestCode;

    private final DialogFragmentActivity activity;

    private final IRepositoryIdProvider repository;

    /**
     * Create dialog helper to display issue
     *
     * @param activity
     * @param requestCode
     * @param repository
     * @param service
     */
    public IssueDialog(final DialogFragmentActivity activity,
                       final int requestCode, final IRepositoryIdProvider repository,
                       final IssueService service) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.repository = repository;
        this.service = service;
    }

    /**
     * Get issues
     *
     * @return list of issues
     */
    public List<Issue> getIssues() {
        return repositoryIssues;
    }

    private void load() {
        new ProgressDialogTask<ArrayList<Issue>>(activity) {

            @Override
            public ArrayList<Issue> run(Account account) throws Exception {
                ArrayList<Issue> issues = new ArrayList<Issue>();
                String[] repid = repository.generateId().split("/");
                issues.addAll(service.getIssues(repid[0],repid[1], "none").execute().body());
                return issues;
            }

            @Override
            protected void onSuccess(ArrayList<Issue> all) throws Exception {
                super.onSuccess(all);

                repositoryIssues = all;
                show();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                Log.d(TAG, "Exception loading issues", e);
                ToastUtils.show(activity, e, R.string.error_issues_load);
            }

            @Override
            public void execute() {
                showIndeterminate(R.string.loading_issues);

                super.execute();
            }
        }.execute();
    }

    /**
     * Show dialog
     *
     */
    public void show() {
        if (repositoryIssues == null) {
            load();
            return;
        }

        IssueDialogFragment.show(activity, requestCode,
                activity.getString(R.string.ms_select_issue), null,
                repositoryIssues);
    }
}
