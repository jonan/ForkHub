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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_ISSUE_NUMBERS;
import static com.github.mobile.Intents.EXTRA_POSITION;
import static com.github.mobile.Intents.EXTRA_REPOSITORIES;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import android.accounts.Account;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.core.issue.IssueStore;
import com.github.mobile.core.issue.IssueUtils;
import com.github.mobile.core.repo.RefreshRepositoryTask;
import com.github.mobile.ui.FragmentProvider;
import com.github.mobile.ui.PagerActivity;
import com.github.mobile.ui.ViewPager;
import com.github.mobile.ui.repo.RepositoryViewActivity;
import com.github.mobile.ui.user.UriLauncherActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;

/**
 * Activity to display a collection of issues or pull requests in a pager
 */
public class IssuesViewActivity extends PagerActivity {

    private static final String EXTRA_PULL_REQUESTS = "pullRequests";

    /**
     * Create an intent to show a single issue
     *
     * @param issue
     * @return intent
     */
    public static Intent createIntent(final Issue issue) {
        return createIntent(Collections.singletonList(issue), 0);
    }

    /**
     * Create an intent to show issue
     *
     * @param issue
     * @param repository
     * @return intent
     */
    public static Intent createIntent(final Issue issue,
            final Repository repository) {
        return createIntent(Collections.singletonList(issue), repository, 0);
    }

    /**
     * Create an intent to show issues with an initial selected issue
     *
     * @param issues
     * @param repository
     * @param position
     * @return intent
     */
    public static Intent createIntent(final Collection<? extends Issue> issues,
            final Repository repository, final int position) {
        int[] numbers = new int[issues.size()];
        boolean[] pullRequests = new boolean[issues.size()];
        int index = 0;
        for (Issue issue : issues) {
            numbers[index] = issue.getNumber();
            pullRequests[index] = IssueUtils.isPullRequest(issue);
            index++;
        }
        return new Builder("issues.VIEW").add(EXTRA_ISSUE_NUMBERS, numbers)
                .add(EXTRA_REPOSITORY, repository)
                .add(EXTRA_POSITION, position)
                .add(EXTRA_PULL_REQUESTS, pullRequests).toIntent();
    }

    /**
     * Create an intent to show issues with an initial selected issue
     *
     * @param issues
     * @param position
     * @return intent
     */
    public static Intent createIntent(Collection<? extends Issue> issues,
            int position) {
        final int count = issues.size();
        int[] numbers = new int[count];
        boolean[] pullRequests = new boolean[count];
        ArrayList<RepositoryId> repos = new ArrayList<RepositoryId>(count);
        int index = 0;
        for (Issue issue : issues) {
            numbers[index] = issue.getNumber();
            pullRequests[index] = IssueUtils.isPullRequest(issue);
            index++;

            RepositoryId repoId = null;
            if (issue instanceof RepositoryIssue) {
                Repository issueRepo = ((RepositoryIssue) issue)
                        .getRepository();
                if (issueRepo != null) {
                    User owner = issueRepo.getOwner();
                    if (owner != null)
                        repoId = RepositoryId.create(owner.getLogin(),
                                issueRepo.getName());
                }
            }
            if (repoId == null)
                repoId = RepositoryId.createFromUrl(issue.getHtmlUrl());
            repos.add(repoId);
        }

        Builder builder = new Builder("issues.VIEW");
        builder.add(EXTRA_ISSUE_NUMBERS, numbers);
        builder.add(EXTRA_REPOSITORIES, repos);
        builder.add(EXTRA_POSITION, position);
        builder.add(EXTRA_PULL_REQUESTS, pullRequests);
        return builder.toIntent();
    }

    private ViewPager pager;

    private int[] issueNumbers;

    private boolean[] pullRequests;

    private ArrayList<RepositoryId> repoIds;

    private Repository repo;

    @Inject
    private AvatarLoader avatars;

    @Inject
    private IssueStore store;

    @Inject
    private CollaboratorService collaboratorService;

    private final AtomicReference<User> user = new AtomicReference<User>();

    private boolean isCollaborator;

    private IssuesPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pager);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        issueNumbers = getIntArrayExtra(EXTRA_ISSUE_NUMBERS);
        pullRequests = getBooleanArrayExtra(EXTRA_PULL_REQUESTS);
        repoIds = getSerializableExtra(EXTRA_REPOSITORIES);
        repo = getSerializableExtra(EXTRA_REPOSITORY);

        if (repo != null) {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setSubtitle(repo.generateId());
            user.set(repo.getOwner());
            avatars.bind(actionBar, user);
        }

        // Load avatar if single issue and user is currently unset or missing
        // avatar URL
        if (issueNumbers.length == 1
                && (user.get() == null || user.get().getAvatarUrl() == null))
            new RefreshRepositoryTask(this, repo != null ? repo : repoIds.get(0)) {

                @Override
                protected void onSuccess(Repository fullRepository)
                        throws Exception {
                    super.onSuccess(fullRepository);

                    avatars.bind(getSupportActionBar(),
                            fullRepository.getOwner());
                }
            }.execute();

        isCollaborator = false;
        checkCollaboratorStatus();
    }

    private void configurePager() {
        int initialPosition = getIntExtra(EXTRA_POSITION);
        pager = finder.find(R.id.vp_pages);

        if (repo != null)
            adapter = new IssuesPagerAdapter(this, repo, issueNumbers, isCollaborator);
        else
            adapter = new IssuesPagerAdapter(this, repoIds, issueNumbers, store, isCollaborator);
        pager.setAdapter(adapter);

        pager.addOnPageChangeListener(this);
        pager.scheduleSetItem(initialPosition, this);
        onPageSelected(initialPosition);
    }

    private void updateTitle(final int position) {
        int number = issueNumbers[position];
        boolean pullRequest = pullRequests[position];

        if (pullRequest)
            getSupportActionBar().setTitle(
                    getString(R.string.pull_request_title) + number);
        else
            getSupportActionBar().setTitle(
                    getString(R.string.issue_title) + number);
    }

    @Override
    public void onPageSelected(final int position) {
        super.onPageSelected(position);

        if (repo != null) {
            updateTitle(position);
            return;
        }

        if (repoIds == null)
            return;

        ActionBar actionBar = getSupportActionBar();
        RepositoryId repoId = repoIds.get(position);
        if (repoId != null) {
            updateTitle(position);
            actionBar.setSubtitle(repoId.generateId());
            RepositoryIssue issue = store.getIssue(repoId,
                    issueNumbers[position]);
            if (issue != null) {
                Repository fullRepo = issue.getRepository();
                if (fullRepo != null && fullRepo.getOwner() != null) {
                    user.set(fullRepo.getOwner());
                    avatars.bind(actionBar, user);
                } else
                    actionBar.setLogo(null);
            } else
                actionBar.setLogo(null);
        } else {
            actionBar.setSubtitle(null);
            actionBar.setLogo(null);
        }
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        adapter.onDialogResult(pager.getCurrentItem(), requestCode, resultCode,
                arguments);
    }

    @Override
    public void startActivity(Intent intent) {
        Intent converted = UriLauncherActivity.convert(intent);
        if (converted != null)
            super.startActivity(converted);
        else
            super.startActivity(intent);
    }

    @Override
    protected FragmentProvider getProvider() {
        return adapter;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Repository repository = repo;
            if (repository == null) {
                int position = pager.getCurrentItem();
                RepositoryId repoId = repoIds.get(position);
                if (repoId != null) {
                    RepositoryIssue issue = store.getIssue(repoId,
                            issueNumbers[position]);
                    if (issue != null)
                        repository = issue.getRepository();
                }
            }
            if (repository != null) {
                Intent intent = RepositoryViewActivity.createIntent(repository);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP
                        | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void checkCollaboratorStatus() {
        new AuthenticatedUserTask<Boolean>(this) {

            @Override
            protected Boolean run(Account account) throws Exception {
                return collaboratorService.isCollaborator(repo != null ? repo : repoIds.get(0),
                        AccountUtils.getLogin(IssuesViewActivity.this));
            }

            @Override
            protected void onThrowable(Throwable t) throws RuntimeException {
                invalidateOptionsMenu();
                configurePager();
            }

            @Override
            protected void onSuccess(Boolean collaborator) throws Exception {
                super.onSuccess(collaborator);

                isCollaborator = collaborator;
                invalidateOptionsMenu();
                configurePager();
            }
        }.execute();
    }
}
