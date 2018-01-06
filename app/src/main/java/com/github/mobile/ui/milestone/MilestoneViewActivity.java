package com.github.mobile.ui.milestone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.Intents;
import com.github.mobile.R;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.issue.IssuesFragment;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_ISSUE_FILTER;
import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.milestone_view);

        repository = getSerializableExtra(EXTRA_REPOSITORY);
        milestone = getSerializableExtra(EXTRA_MILESTONE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(milestone.getTitle());
        actionBar.setSubtitle(R.string.milestone);
        actionBar.setDisplayHomeAsUpEnabled(true);

        MilestoneFragment milestoneFragment = new MilestoneFragment();
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
                //creating new milestone
                Intent i = EditMilestoneActivity.createIntent(repository);
                i.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.milestone, menu);
        return true;
    }
}
