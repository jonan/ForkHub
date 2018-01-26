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

import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.api.service.IssueService;
import com.github.mobile.api.service.MilestoneService;
import com.github.mobile.core.issue.IssueStore;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.ui.milestone.IssueDialog;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Milestone;

import static com.github.mobile.RequestCodes.ISSUE_MILESTONE_UPDATE;

/**
 * Task to add an issue to a milestone
 */
public class AddIssueTask extends ProgressDialogTask<com.github.mobile.api.model.Milestone> {

    @Inject
    private IssueService service;

    @Inject
    private MilestoneService milestoneService;

    @Inject
    private IssueStore store;

    private final IssueDialog issueDialog;

    private final IRepositoryIdProvider repositoryId;

    private int issueNumber;

    private final int milestoneNumber;

    /**
     * Create task to add an issue to a milestone
     *
     * @param activity
     * @param repositoryId
     * @param milestoneNumber
     */
    public AddIssueTask(final DialogFragmentActivity activity,
                        final IRepositoryIdProvider repositoryId, final int milestoneNumber) {
        super(activity);

        this.repositoryId = repositoryId;
        this.milestoneNumber = milestoneNumber;
        issueDialog = new IssueDialog(activity, ISSUE_MILESTONE_UPDATE,
                repositoryId, service);
    }

    @Override
    protected com.github.mobile.api.model.Milestone run(Account account) throws Exception {
        org.eclipse.egit.github.core.Issue editedIssue = new org.eclipse.egit.github.core.Issue();
        editedIssue.setNumber(issueNumber);
        editedIssue.setMilestone(new Milestone().setNumber(milestoneNumber));
        store.editIssue(repositoryId, editedIssue);
        String[] rep = repositoryId.generateId().split("/");
        return milestoneService.getMilestone(rep[0], rep[1], milestoneNumber).execute().body();
    }

    /**
     * Prompt for issue selection
     *
     * @return this task
     */
    public AddIssueTask prompt() {
        issueDialog.show();
        return this;
    }

    /**
     * Add issue to the milestone
     *
     * @param issue
     * @return this task
     */
    public AddIssueTask edit(Issue issue) {
        if (issue != null)
            issueNumber = issue.number;
        else
            issueNumber = -1;

        showIndeterminate(R.string.updating_milestone);

        super.execute();

        return this;
    }
}
