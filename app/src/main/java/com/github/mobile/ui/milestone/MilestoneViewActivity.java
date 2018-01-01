package com.github.mobile.ui.milestone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.Intents;
import com.github.mobile.R;
import com.github.mobile.api.model.Milestone;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.issue.IssuesFragment;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import static com.github.mobile.Intents.EXTRA_ISSUE_FILTER;
import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.RequestCodes.MILESTONE_EDIT;


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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_ms_menu_item:
                //todo add issues to milestone
                return true;
            case R.id.m_edit: {
                Intent intent = EditMilestoneActivity.createIntent(milestone,
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
}
