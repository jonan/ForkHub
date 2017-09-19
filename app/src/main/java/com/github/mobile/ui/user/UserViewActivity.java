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
package com.github.mobile.ui.user;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_POSITION;
import static com.github.mobile.Intents.EXTRA_USER;

import android.accounts.Account;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.user.FollowUserTask;
import com.github.mobile.core.user.FollowingUserTask;
import com.github.mobile.core.user.RefreshUserTask;
import com.github.mobile.core.user.UnfollowUserTask;
import com.github.mobile.core.user.UserComparator;
import com.github.mobile.persistence.AccountDataManager;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.UriLauncherActivity;
import com.github.mobile.util.ToastUtils;
import com.github.mobile.util.TypefaceUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;

import org.eclipse.egit.github.core.User;

/**
 * Activity to view a user's various pages
 */
public class UserViewActivity extends TabPagerActivity<UserPagerAdapter>
        implements OrganizationSelectionProvider {

    public static final int TAB_ACTIVITY = 0;
    public static final int TAB_REPOSITORIES = 1;
    public static final int TAB_STARS = 2;
    public static final int TAB_FOLLOWERS = 3;
    public static final int TAB_FOLLOWEES = 4;

    public static final int TAB_MEMBERS = 2;
    public static final int TAB_TEAMS = 3;

    /**
     * Create intent for this activity
     *
     * @param user
     * @return intent
     */
    public static Intent createIntent(User user) {
        return new Builder("user.VIEW").user(user).toIntent();
    }

    /**
     * Create intent for this activity
     *
     * @param user
     * @return intent
     */
    public static Intent createIntent(com.github.mobile.api.model.User user) {
        return new Builder("user.VIEW").user(user.getOldModel()).toIntent();
    }

    /**
     * Create intent for this activity and open the given tab
     *
     * @param user
     * @param tab
     * @return intent
     */
    public static Intent createIntent(User user, int tab) {
        return new Builder("user.VIEW").user(user).add(EXTRA_POSITION, tab).toIntent();
    }

    @Inject
    private AccountDataManager accountDataManager;

    @Inject
    private Provider<UserComparator> userComparatorProvider;

    private User user;

    private ProgressBar loadingBar;

    private boolean isOrganization;

    private boolean isMember;

    private boolean isFollowing;

    private boolean followingStatusChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = (User) getIntent().getSerializableExtra(EXTRA_USER);
        loadingBar = finder.find(R.id.pb_loading);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(user.getLogin());

        if (!TextUtils.isEmpty(user.getAvatarUrl()) && User.TYPE_USER.equals(user.getType())) {
            configurePager();
        } else {
            ViewUtils.setGone(loadingBar, false);
            setGone(true);
            new RefreshUserTask(this, user.getLogin()) {
                @Override
                protected User run(Account account) throws Exception {
                    isMember = false;
                    for (User org : accountDataManager.getOrgs(false)) {
                        if (user.getLogin().equals(org.getLogin())) {
                            isMember = true;
                        }
                    }
                    return super.run(account);
                }

                @Override
                protected void onSuccess(User fullUser) throws Exception {
                    super.onSuccess(fullUser);

                    user = fullUser;
                    getIntent().putExtra(EXTRA_USER, user);
                    configurePager();
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    super.onException(e);

                    ToastUtils.show(UserViewActivity.this,
                            R.string.error_person_load);
                    ViewUtils.setGone(loadingBar, true);
                }
            }.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        getMenuInflater().inflate(R.menu.user, optionsMenu);

        return super.onCreateOptionsMenu(optionsMenu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem followItem = menu.findItem(R.id.m_follow);

        if (!isOrganization) {
            followItem.setVisible(followingStatusChecked);
            followItem.setTitle(isFollowing ? R.string.unfollow : R.string.follow);
        } else {
            followItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_follow:
            followUser();
            return true;
        case R.id.m_open_browser:
            UriLauncherActivity.launchUriInBrowser(this, Uri.parse("https://github.com/" + user.getLogin()));
            return true;
        case android.R.id.home:
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }

    }

    private void configurePager() {
        isOrganization = User.TYPE_ORG.equals(user.getType());
        configureTabPager();
        ViewUtils.setGone(loadingBar, true);
        setGone(false);
        checkFollowingUserStatus();
        int initialPosition = getIntExtra(EXTRA_POSITION);
        if (initialPosition > -1 && initialPosition < adapter.getCount()) {
            pager.setItem(initialPosition);
        }
    }

    @Override
    public User addListener(OrganizationSelectionListener listener) {
        return user;
    }

    @Override
    public OrganizationSelectionProvider removeListener(
            OrganizationSelectionListener listener) {
        return this;
    }

    @Override
    protected UserPagerAdapter createAdapter() {
        return new UserPagerAdapter(this, isOrganization, isMember);
    }

    @Override
    protected int getContentView() {
        return R.layout.tabbed_progress_pager;
    }

    @Override
    protected String getIcon(int position) {
        switch (position) {
        case TAB_ACTIVITY:
            return TypefaceUtils.ICON_RSS;
        case TAB_REPOSITORIES:
            return TypefaceUtils.ICON_REPO;
        case TAB_FOLLOWERS:
            return TypefaceUtils.ICON_EYE;
        case TAB_FOLLOWEES:
            return TypefaceUtils.ICON_BROADCAST;
        default:
            return super.getIcon(position);
        }
    }

    private void followUser() {
        if (isFollowing)
            new UnfollowUserTask(this, user.getLogin()) {

                @Override
                protected void onSuccess(User user) throws Exception {
                    super.onSuccess(user);

                    isFollowing = !isFollowing;
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    super.onException(e);

                    ToastUtils.show(UserViewActivity.this,
                            R.string.error_unfollowing_person);
                }
            }.start();
        else
            new FollowUserTask(this, user.getLogin()) {

                @Override
                protected void onSuccess(User user) throws Exception {
                    super.onSuccess(user);

                    isFollowing = !isFollowing;
                }

                @Override
                protected void onException(Exception e) throws RuntimeException {
                    super.onException(e);

                    ToastUtils.show(UserViewActivity.this,
                            R.string.error_following_person);
                }
            }.start();
    }

    private void checkFollowingUserStatus() {
        followingStatusChecked = false;
        new FollowingUserTask(this, user.getLogin()) {

            @Override
            protected void onSuccess(Boolean following) throws Exception {
                super.onSuccess(following);

                isFollowing = following;
                followingStatusChecked = true;
                invalidateOptionsMenu();
            }
        }.execute();
    }
}
