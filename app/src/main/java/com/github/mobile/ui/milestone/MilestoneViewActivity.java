package com.github.mobile.ui.milestone;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.Intents;
import com.github.mobile.R;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.repo.RepositoryViewActivity;

import org.eclipse.egit.github.core.Repository;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;

/**
 * Created by Александр on 20.12.2017.
 */

public class MilestoneViewActivity extends DialogFragmentActivity {
    /**
     * Create intent for this activity
     *
     * @param repository
     * @return intent
     */
    public static Intent createIntent(Repository repository, int position) {
        return new Intents.Builder("repo.milestone.VIEW").repo(repository).toIntent();
    }

    private Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.repo_milestones);

        repository = getSerializableExtra(EXTRA_REPOSITORY);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(repository.getName());
        actionBar.setSubtitle(R.string.milestone);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = RepositoryViewActivity.createIntent(repository);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
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
