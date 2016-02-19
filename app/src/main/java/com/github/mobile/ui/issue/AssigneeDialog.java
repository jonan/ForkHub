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
package com.github.mobile.ui.issue;

import com.github.mobile.R;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.util.ToastUtils;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;

import android.accounts.Account;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.String.CASE_INSENSITIVE_ORDER;

/**
 * Dialog helper to display a list of assignees to select one from
 */
public class AssigneeDialog {

    private static final String TAG = "AssigneeDialog";

    private CollaboratorService service;

    private Map<String, User> collaborators;

    private final int requestCode;

    private final DialogFragmentActivity activity;

    private final IRepositoryIdProvider repository;

    /**
     * Create dialog helper to display assignees
     *
     * @param activity
     * @param requestCode
     * @param repository
     * @param service
     */
    public AssigneeDialog(final DialogFragmentActivity activity,
            final int requestCode, final IRepositoryIdProvider repository,
            final CollaboratorService service) {
        this.activity = activity;
        this.requestCode = requestCode;
        this.repository = repository;
        this.service = service;
    }

    private void load(final User selectedAssignee) {
        new ProgressDialogTask<List<User>>(activity) {

            @Override
            public List<User> run(Account account) throws Exception {
                List<User> users = service.getCollaborators(repository);
                Map<String, User> loadedCollaborators = new TreeMap<String, User>(
                        CASE_INSENSITIVE_ORDER);
                for (User user : users)
                    loadedCollaborators.put(user.getLogin(), user);
                collaborators = loadedCollaborators;
                return users;
            }

            @Override
            protected void onSuccess(List<User> all) throws Exception {
                super.onSuccess(all);

                show(selectedAssignee);
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                Log.d(TAG, "Exception loading collaborators", e);
                ToastUtils.show(activity, e, R.string.error_collaborators_load);
            }

            @Override
            public void execute() {
                showIndeterminate(R.string.loading_collaborators);

                super.execute();
            }
        }.execute();
    }

    /**
     * Show dialog with given assignee selected
     *
     * @param selectedAssignee
     */
    public void show(User selectedAssignee) {
        if (collaborators == null) {
            load(selectedAssignee);
            return;
        }

        final ArrayList<User> users = new ArrayList<User>(
                collaborators.values());
        int checked = -1;
        if (selectedAssignee != null)
            for (int i = 0; i < users.size(); i++)
                if (selectedAssignee.getLogin().equals(users.get(i).getLogin()))
                    checked = i;
        AssigneeDialogFragment.show(activity, requestCode,
                activity.getString(R.string.select_assignee), null, users,
                checked);
    }
}
