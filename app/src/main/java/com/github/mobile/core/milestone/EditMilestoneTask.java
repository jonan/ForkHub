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
package com.github.mobile.core.milestone;

import android.accounts.Account;
import android.app.Activity;
import android.util.Log;

import com.github.mobile.R;
import com.github.mobile.api.model.Milestone;
import com.github.mobile.api.service.MilestoneService;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.util.ToastUtils;
import com.google.inject.Inject;

public class EditMilestoneTask extends ProgressDialogTask<Milestone> {
    private static final String TAG = "EditMilestoneTask";

    @Inject
    private MilestoneService service;

    private final String owner;
    private final String repo;
    private final Milestone milestone;

    /**
     * Create task to edit an {@link Milestone}
     *
     * @param activity
     * @param owner
     * @param repo
     * @param milestone
     */
    public EditMilestoneTask(final Activity activity,
                             final String owner,
                             final String repo,
                             final Milestone milestone) {
        super(activity);
        this.owner = owner;
        this.repo = repo;
        this.milestone = milestone;
    }

    /**
     * Edit milestone
     *
     * @return this task
     */
    public EditMilestoneTask create() {
        showIndeterminate(R.string.updating_milestone);

        execute();
        return this;
    }

    @Override
    public Milestone run(Account account) throws Exception {
        return service.editMilestone(owner, repo, milestone.number, milestone).execute().body();
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.e(TAG, "Exception editing milestone", e);
        ToastUtils.show((Activity) getContext(), e.getMessage());
    }
}
