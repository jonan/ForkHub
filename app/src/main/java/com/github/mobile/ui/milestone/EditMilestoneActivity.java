package com.github.mobile.ui.milestone;


import android.accounts.Account;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.github.mobile.Intents;
import com.github.mobile.R;
import org.eclipse.egit.github.core.Milestone;

import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.TextWatcherAdapter;
import com.github.mobile.ui.issue.AssigneeDialog;
import com.github.mobile.ui.issue.EditIssueActivity;
import com.github.mobile.ui.issue.LabelsDialog;
import com.github.mobile.ui.issue.MilestoneDialog;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;

import static com.github.mobile.Intents.EXTRA_ISSUE;
import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.RequestCodes.ISSUE_ASSIGNEE_UPDATE;
import static com.github.mobile.RequestCodes.ISSUE_LABELS_UPDATE;
import static com.github.mobile.RequestCodes.ISSUE_MILESTONE_UPDATE;

/*
 * Activity to edit or create a milestone
 */
public class EditMilestoneActivity extends DialogFragmentActivity {


    /**
     * Create intent for this activity
     *
     * @param repository
     * @return intent
     */
    public static Intent createIntent(Repository repository) {
        return new Intents.Builder("repo.milestones.edit.VIEW").repo(repository).toIntent();
    }


    /**
     * Create intent to edit a milestone
     *
     * @param milestone
     * @param issue
     * @return intent
     */
    public static Intent createIntent(final Milestone milestone,
                                      final Issue issue) {
        Intents.Builder builder = new Intents.Builder("repo.milestones.edit.VIEW");
        if (issue != null)
            builder.add(EXTRA_ISSUE, issue);
        if (milestone != null)
            builder.milestone(milestone);
        return builder.toIntent();
    }

    private EditText titleText;

    private EditText descriptionText;

    private TextView dateText;

    private Button twoWeeksButton;

    private Button monthButton;

    private TextView clearText;

    private ScrollView milestoneContent;

    @Inject
    private AvatarLoader avatars;

    @Inject
    private MilestoneService milestoneService;

    @Inject
    private CollaboratorService collaboratorService;

    @Inject
    private LabelService labelService;

    private Milestone milestone;

    private RepositoryId repository;

    private MenuItem saveItem;

    private MilestoneDialog milestoneDialog;

    private AssigneeDialog assigneeDialog;

    private LabelsDialog labelsDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milestone_edit);

        titleText = finder.find(R.id.et_milestone_title);
        descriptionText = finder.find(R.id.et_milestone_description);
        dateText = finder.find(R.id.tv_milestone_date);
        twoWeeksButton = finder.find(R.id.b_two_weeks);
        monthButton = finder.find(R.id.b_month);
        clearText = finder.find(R.id.tv_clear);

        checkCollaboratorStatus();

        Intent intent = getIntent();

        if (savedInstanceState != null)
            milestone = (Milestone) savedInstanceState.getSerializable(EXTRA_MILESTONE);
        if (milestone == null)
            milestone = (Milestone) intent.getSerializableExtra(EXTRA_MILESTONE);
        if (milestone == null)
            milestone = new Milestone();

        //todo RepositoryId.create?

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //todo staff with actionbar

        titleText.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveMenu(s);
            }
        });

        updateSaveMenu();
        titleText.setText(milestone.getTitle());
        descriptionText.setText(milestone.getDescription());
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        if (RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            case ISSUE_MILESTONE_UPDATE:
                //todo
                /*issue.setMilestone(MilestoneDialogFragment.getSelected(arguments));
                updateMilestone();
                break;*/
            case ISSUE_ASSIGNEE_UPDATE:
                //todo
            case ISSUE_LABELS_UPDATE:
                //todo
        }
    }

    private void showMainContent() {
        finder.find(R.id.sv_milestone_content).setVisibility(View.VISIBLE);
        finder.find(R.id.pb_loading).setVisibility(View.GONE);
    }

    private void showCollaboratorOptions() {
        //todo
    }

    private void updateMilestone() {
        //todo
    }

    private void updateAssignee() {
        //todo
    }

    private void updateLabels() {
        //todo
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_MILESTONE, milestone);
    }

    private void updateSaveMenu() {
        if (titleText != null)
            updateSaveMenu(titleText.getText());
    }

    private void updateSaveMenu(final CharSequence text) {
        if (saveItem != null)
            saveItem.setEnabled(!TextUtils.isEmpty(text));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu options) {
        getMenuInflater().inflate(R.menu.milestone_edit, options);
        saveItem = options.findItem(R.id.m_apply);
        updateSaveMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //todo
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkCollaboratorStatus() {
        new AuthenticatedUserTask<Boolean>(this) {

            @Override
            public Boolean run(Account account) throws Exception {
                return collaboratorService.isCollaborator(
                        repository, AccountUtils.getLogin(EditMilestoneActivity.this));
            }

            @Override
            protected void onSuccess(Boolean isCollaborator) throws Exception {
                super.onSuccess(isCollaborator);

                showMainContent();
                if (isCollaborator)
                    showCollaboratorOptions();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                showMainContent();
            }
        }.execute();
    }
}
