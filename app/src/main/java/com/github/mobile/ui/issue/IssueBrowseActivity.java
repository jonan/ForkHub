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

import com.google.inject.Inject;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.issue.IssueFilter;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.util.AvatarLoader;

import org.eclipse.egit.github.core.Repository;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_ISSUE_FILTER;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;

/**
 * Activity for browsing a list of issues scoped to a single {@link IssueFilter}
 */
public class IssueBrowseActivity extends DialogFragmentActivity {

    /**
     * Create intent to browse the filtered issues
     *
     * @param filter
     * @return intent
     */
    public static Intent createIntent(IssueFilter filter) {
        return new Builder("repo.issues.VIEW").repo(filter.getRepository())
                .add(EXTRA_ISSUE_FILTER, filter).toIntent();
    }

    private Repository repo;

    @Inject
    private AvatarLoader avatars;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        repo = getSerializableExtra(EXTRA_REPOSITORY);

        setContentView(R.layout.repo_issue_list);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(repo.getName());
        actionBar.setSubtitle(repo.getOwner().getLogin());
        actionBar.setDisplayHomeAsUpEnabled(true);
        avatars.bind(actionBar, repo.getOwner());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = FiltersViewActivity.createIntent();
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
}
