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

import static android.app.SearchManager.APP_DATA;
import static android.app.SearchManager.QUERY;
import static android.content.Intent.ACTION_SEARCH;
import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.R;
import com.github.mobile.ui.repo.RepositoryViewActivity;
import com.github.mobile.ui.roboactivities.RoboActionBarActivity;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.ToastUtils;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;

/**
 * Activity to search issues
 */
public class IssueSearchActivity extends RoboActionBarActivity {

    @Inject
    private AvatarLoader avatars;

    private Repository repository;

    private SearchIssueListFragment issueFragment;

    private String query;

    @Override
    public boolean onCreateOptionsMenu(Menu options) {
        getMenuInflater().inflate(R.menu.search, options);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_search:
            onSearchRequested();
            return true;
        case R.id.m_clear:
            IssueSearchSuggestionsProvider.clear(this);
            ToastUtils.show(this, R.string.search_history_cleared);
            return true;
        case android.R.id.home:
            Intent intent = RepositoryViewActivity.createIntent(repository);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.issue_search);

        ActionBar actionBar = getSupportActionBar();
        Bundle appData = getIntent().getBundleExtra(APP_DATA);
        if (appData != null) {
            repository = (Repository) appData.getSerializable(EXTRA_REPOSITORY);
            if (repository != null) {
                actionBar.setSubtitle(repository.generateId());
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }
        avatars.bind(actionBar, repository.getOwner());

        issueFragment = (SearchIssueListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.search_issue_list_fragment);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        issueFragment.setListShown(false);
        handleIntent(intent);
        issueFragment.refresh();
    }

    @Override
    public boolean onSearchRequested() {
        startSearch(query, true, null, false);
        return true;
    }

    private void handleIntent(Intent intent) {
        if (ACTION_SEARCH.equals(intent.getAction()))
            search(intent.getStringExtra(QUERY));
    }

    private void search(final String query) {
        this.query = query;
        getSupportActionBar().setTitle(query);
        IssueSearchSuggestionsProvider.save(this, query);
        issueFragment.setQuery(query);
    }
}
