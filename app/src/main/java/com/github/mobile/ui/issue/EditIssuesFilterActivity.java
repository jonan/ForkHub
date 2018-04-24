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

import static android.view.View.GONE;
import static com.github.mobile.Intents.EXTRA_ISSUE_FILTER;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.Set;

import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;

/**
 * Activity to create or edit an issues filter for a repository
 */
public class EditIssuesFilterActivity extends DialogFragmentActivity {

    /**
     * Create intent for creating an issue filter for the given repository
     *
     * @param filter
     * @return intent
     */
    public static Intent createIntent(IssueFilter filter) {
        return new Builder("repo.issues.filter.VIEW").add(EXTRA_ISSUE_FILTER,
                filter).toIntent();
    }

    private static final int REQUEST_LABELS = 1;

    private static final int REQUEST_MILESTONE = 2;

    private static final int REQUEST_ASSIGNEE = 3;

    @Inject
    private CollaboratorService collaborators;

    @Inject
    private MilestoneService milestones;

    @Inject
    private LabelService labels;

    @Inject
    private AvatarLoader avatars;

    private LabelsDialog labelsDialog;

    private MilestoneDialog milestoneDialog;

    private AssigneeDialog assigneeDialog;

    private IssueFilter filter;

    private TextView labelsText;

    private TextView milestoneText;

    private TextView assigneeText;

    private ImageView avatarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.issues_filter_edit);

        labelsText = finder.find(R.id.tv_labels);
        milestoneText = finder.find(R.id.tv_milestone);
        assigneeText = finder.find(R.id.tv_assignee);
        avatarView = finder.find(R.id.iv_avatar);

        if (savedInstanceState != null)
            filter = (IssueFilter) savedInstanceState
                    .getSerializable(EXTRA_ISSUE_FILTER);

        if (filter == null)
            filter = (IssueFilter) getIntent().getSerializableExtra(
                    EXTRA_ISSUE_FILTER);

        final Repository repository = filter.getRepository();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(repository.isHasIssues() ?
                R.string.filter_issues_title : R.string.filter_pull_requests_title);
        actionBar.setSubtitle(repository.generateId());

        OnClickListener assigneeListener = new OnClickListener() {

            public void onClick(View v) {
                if (assigneeDialog == null)
                    assigneeDialog = new AssigneeDialog(
                            EditIssuesFilterActivity.this, REQUEST_ASSIGNEE,
                            repository, collaborators);
                assigneeDialog.show(filter.getAssignee());
            }
        };

        findViewById(R.id.tv_assignee_label)
                .setOnClickListener(assigneeListener);
        assigneeText.setOnClickListener(assigneeListener);

        OnClickListener milestoneListener = new OnClickListener() {

            public void onClick(View v) {
                if (milestoneDialog == null)
                    milestoneDialog = new MilestoneDialog(
                            EditIssuesFilterActivity.this, REQUEST_MILESTONE,
                            repository, milestones);
                com.github.mobile.api.model.Milestone milestone = filter.getMilestone();
                if (milestone == null) {
                    milestoneDialog.show(null);
                } else {
                    milestoneDialog.show(milestone.getOldModel());
                }
            }
        };

        findViewById(R.id.tv_milestone_label)
                .setOnClickListener(milestoneListener);
        milestoneText.setOnClickListener(milestoneListener);

        OnClickListener labelsListener = new OnClickListener() {

            public void onClick(View v) {
                if (labelsDialog == null)
                    labelsDialog = new LabelsDialog(
                            EditIssuesFilterActivity.this, REQUEST_LABELS,
                            repository, labels);
                labelsDialog.show(filter.getLabels());
            }
        };

        findViewById(R.id.tv_labels_label)
                .setOnClickListener(labelsListener);
        labelsText.setOnClickListener(labelsListener);

        updateAssignee();
        updateMilestone();
        updateLabels();

        RadioButton openButton = (RadioButton) findViewById(R.id.rb_open);

        openButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked)
                    filter.setOpen(true);
            }
        });

        RadioButton closedButton = (RadioButton) findViewById(R.id.rb_closed);

        closedButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            public void onCheckedChanged(CompoundButton buttonView,
                    boolean isChecked) {
                if (isChecked)
                    filter.setOpen(false);
            }
        });

        if (filter.isOpen())
            openButton.setChecked(true);
        else
            closedButton.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu options) {
        getMenuInflater().inflate(R.menu.issue_filter, options);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_apply:
            Intent intent = new Intent();
            intent.putExtra(EXTRA_ISSUE_FILTER, filter);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXTRA_ISSUE_FILTER, filter);
    }

    private void updateLabels() {
        Set<Label> selected = filter.getLabels();
        if (selected != null)
            LabelDrawableSpan.setText(labelsText, selected);
        else
            labelsText.setText(R.string.none);
    }

    private void updateMilestone() {
        com.github.mobile.api.model.Milestone selected = filter.getMilestone();
        if (selected != null)
            milestoneText.setText(selected.getOldModel().getTitle());
        else
            milestoneText.setText(R.string.none);
    }

    private void updateAssignee() {
        User selected = filter.getAssignee();
        if (selected != null) {
            avatars.bind(avatarView, selected);
            assigneeText.setText(selected.getLogin());
        } else {
            avatarView.setVisibility(GONE);
            assigneeText.setText(R.string.assignee_anyone);
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        if (RESULT_OK != resultCode)
            return;

        switch (requestCode) {
        case REQUEST_LABELS:
            filter.setLabels(LabelsDialogFragment.getSelected(arguments));
            updateLabels();
            break;
        case REQUEST_MILESTONE:
            filter.setMilestone(new com.github.mobile.api.model.Milestone(MilestoneDialogFragment.getSelected(arguments)));
            updateMilestone();
            break;
        case REQUEST_ASSIGNEE:
            filter.setAssignee(AssigneeDialogFragment.getSelected(arguments));
            updateAssignee();
            break;
        }
    }
}
