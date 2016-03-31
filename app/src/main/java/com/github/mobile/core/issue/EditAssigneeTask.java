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
package com.github.mobile.core.issue;

import static com.github.mobile.RequestCodes.ISSUE_ASSIGNEE_UPDATE;
import android.accounts.Account;

import com.github.mobile.R;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.ui.issue.AssigneeDialog;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;

/**
 * Task to edit the assignee
 */
public class EditAssigneeTask extends ProgressDialogTask<Issue> {

    @Inject
    private CollaboratorService service;

    @Inject
    private IssueStore store;

    private final AssigneeDialog assigneeDialog;

    private final IRepositoryIdProvider repositoryId;

    private final int issueNumber;

    private User assignee;

    /**
     * Create task to edit a milestone
     *
     * @param activity
     * @param repositoryId
     * @param issueNumber
     */
    public EditAssigneeTask(final DialogFragmentActivity activity,
            final IRepositoryIdProvider repositoryId, final int issueNumber) {
        super(activity);

        this.repositoryId = repositoryId;
        this.issueNumber = issueNumber;
        assigneeDialog = new AssigneeDialog(activity, ISSUE_ASSIGNEE_UPDATE,
                repositoryId, service);
    }

    /**
     * Prompt for assignee selection
     *
     * @param assignee
     *            current assignee
     * @return this task
     */
    public EditAssigneeTask prompt(User assignee) {
        assigneeDialog.show(assignee);
        return this;
    }

    /**
     * Edit issue to have given assignee
     *
     * @param user
     * @return this task
     */
    public EditAssigneeTask edit(User user) {
        showIndeterminate(R.string.updating_assignee);

        this.assignee = user;

        execute();
        return this;
    }

    @Override
    protected Issue run(Account account) throws Exception {
        Issue editedIssue = new Issue();
        if (assignee != null)
            editedIssue.setAssignee(assignee);
        else
            editedIssue.setAssignee(new User().setLogin(""));
        editedIssue.setNumber(issueNumber);
        return store.editIssue(repositoryId, editedIssue);
    }
}
