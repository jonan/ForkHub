package com.github.mobile.ui.milestone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.Intents;
import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.api.model.Milestone;
import com.github.mobile.api.service.IssueService;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.core.milestone.AddIssueTask;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.issue.IssuesFragment;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import static com.github.mobile.Intents.EXTRA_ISSUE_FILTER;
import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.RequestCodes.ISSUE_MILESTONE_UPDATE;
import static com.github.mobile.RequestCodes.MILESTONE_EDIT;

/**
 * Activity to display milestone detailed view
 */
public class MilestoneViewActivity extends DialogFragmentActivity {
    /**
     * Create intent for this activity
     *
     * @param repository
     * @return intent
     */
    public static Intent createIntent(Repository repository, Milestone milestone, int position) {
        return new Intents.Builder("repo.milestone.VIEW")
                .repo(repository)
                .milestone(milestone).toIntent();
    }

    private Repository repository;
    private Milestone milestone;
    private IssueDialog issueDialog;

    private AddIssueTask issueTask;

    @Inject
    private IssueService issueService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.milestone_view);

        repository = getSerializableExtra(EXTRA_REPOSITORY);
        milestone = getSerializableExtra(EXTRA_MILESTONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(milestone.title);
        actionBar.setSubtitle(R.string.milestone);
        actionBar.setDisplayHomeAsUpEnabled(true);

        DialogFragmentActivity dialogActivity = (DialogFragmentActivity) this;

        MilestoneFragment milestoneFragment = new MilestoneFragment();

        Bundle args = new Bundle();
        if (repository != null) {
            args.putString(EXTRA_REPOSITORY_NAME, repository.getName());
            User owner = repository.getOwner();
            args.putString(EXTRA_REPOSITORY_OWNER, owner.getLogin());
            args.putSerializable(EXTRA_USER, owner);
        }
        milestoneFragment.setArguments(args);

        IssuesFragment issuesFragment = new IssuesFragment();

        IssueFilter filter = new IssueFilter(repository);
        filter.setMilestone(milestone);
        filter.setOpen(true);
        getIntent().putExtra(EXTRA_ISSUE_FILTER, filter);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.ms_description, milestoneFragment);
        transaction.add(R.id.ms_issues, issuesFragment);

        transaction.commit();

        issueTask = new AddIssueTask(dialogActivity, repository,
                milestone.number) {

            @Override
            protected void onSuccess(Milestone editedMilestone) throws Exception {
                super.onSuccess(editedMilestone);
                IssuesFragment issuesFragment = new IssuesFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.ms_issues, issuesFragment);
                transaction.commit();
                milestone = editedMilestone;
                updateMilestone();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_ms_menu_item:
                issueTask.prompt();
                return true;
            case R.id.m_edit: {
                Intent intent = EditMilestoneActivity.createIntent(milestone, repository,
                        repository.getOwner().getLogin(), repository.getName());
                startActivityForResult(intent, MILESTONE_EDIT);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.milestone_view, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(MILESTONE_EDIT == requestCode) {
            if(data != null) { // user has changed something
                milestone = (Milestone) data.getSerializableExtra(EXTRA_MILESTONE);
                updateMilestone();
            }
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        if (RESULT_OK != resultCode)
            return;

        switch (requestCode) {
            case ISSUE_MILESTONE_UPDATE:
                Issue issue = IssueDialogFragment.getSelected(arguments);
                issueTask.edit(issue);
                break;
        }
    }

    private void updateMilestone() {
        if(milestone != null) {
            MilestoneFragment milestoneFragment = new MilestoneFragment();

            ActionBar actionBar = getSupportActionBar();
            actionBar.setTitle(milestone.title);
            actionBar.setSubtitle(R.string.milestone);
            actionBar.setDisplayHomeAsUpEnabled(true);

            Bundle args = new Bundle();
            if (repository != null) {
                args.putString(EXTRA_REPOSITORY_NAME, repository.getName());
                User owner = repository.getOwner();
                args.putString(EXTRA_REPOSITORY_OWNER, owner.getLogin());
                args.putSerializable(EXTRA_USER, owner);
                args.putSerializable(EXTRA_MILESTONE, milestone);
            }
            milestoneFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            transaction.replace(R.id.ms_description, milestoneFragment);
            transaction.commit();
        }
    }
}
