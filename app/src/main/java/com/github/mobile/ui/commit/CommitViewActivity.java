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
package com.github.mobile.ui.commit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_BASES;
import static com.github.mobile.Intents.EXTRA_POSITION;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.commit.CommitUtils;
import com.github.mobile.ui.FragmentProvider;
import com.github.mobile.ui.PagerActivity;
import com.github.mobile.ui.ViewPager;
import com.github.mobile.ui.repo.RepositoryViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.Collection;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;

/**
 * Activity to display a commit
 */
public class CommitViewActivity extends PagerActivity {

    /**
     * Create intent for this activity
     *
     * @param repository
     * @param id
     * @return intent
     */
    public static Intent createIntent(final Repository repository,
            final String id) {
        return createIntent(repository, 0, id);
    }

    /**
     * Create intent for this activity
     *
     * @param repository
     * @param position
     * @param commits
     * @return intent
     */
    public static Intent createIntent(final Repository repository,
            final int position, final Collection<RepositoryCommit> commits) {
        String[] ids = new String[commits.size()];
        int index = 0;
        for (RepositoryCommit commit : commits)
            ids[index++] = commit.getSha();
        return createIntent(repository, position, ids);
    }

    /**
     * Create intent for this activity
     *
     * @param repository
     * @param position
     * @param ids
     * @return intent
     */
    public static Intent createIntent(final Repository repository,
            final int position, final String... ids) {
        Builder builder = new Builder("commits.VIEW");
        builder.add(EXTRA_POSITION, position);
        builder.add(EXTRA_BASES, ids);
        builder.repo(repository);
        return builder.toIntent();
    }

    private ViewPager pager;

    private Repository repository;

    private CharSequence[] ids;

    private int initialPosition;

    @Inject
    private AvatarLoader avatars;

    private CommitPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pager);

        pager = finder.find(R.id.vp_pages);

        repository = getSerializableExtra(EXTRA_REPOSITORY);
        ids = getCharSequenceArrayExtra(EXTRA_BASES);
        initialPosition = getIntExtra(EXTRA_POSITION);

        adapter = new CommitPagerAdapter(this, repository, ids);
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(this);
        pager.scheduleSetItem(initialPosition, this);
        onPageSelected(initialPosition);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setSubtitle(repository.generateId());
        avatars.bind(actionBar, repository.getOwner());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
    public void onPageSelected(int position) {
        super.onPageSelected(position);

        final String id = CommitUtils.abbreviate(ids[position].toString());
        getSupportActionBar().setTitle(getString(R.string.commit_prefix) + id);
    }

    @Override
    protected FragmentProvider getProvider() {
        return adapter;
    }
}
