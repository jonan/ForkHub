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

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.github.mobile.R;
import com.github.mobile.ui.FragmentPagerAdapter;
import com.github.mobile.ui.repo.RepositoryListFragment;
import com.github.mobile.ui.repo.StarredRepositoryListFragment;
import com.github.mobile.ui.team.TeamListFragment;

import java.util.HashSet;
import java.util.Set;

/**
 * Pager adapter for a user's different views
 */
public class HomePagerAdapter extends FragmentPagerAdapter {

    private boolean defaultUser;

    private final FragmentManager fragmentManager;

    private final Resources resources;

    private final Set<String> tags = new HashSet<String>();

    /**
     * @param activity
     * @param defaultUser
     */
    public HomePagerAdapter(final AppCompatActivity activity,
            final boolean defaultUser) {
        super(activity);

        fragmentManager = activity.getSupportFragmentManager();
        resources = activity.getResources();
        this.defaultUser = defaultUser;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            return defaultUser ? new UserReceivedNewsFragment()
                    : new OrganizationNewsFragment();
        case 1:
            return new RepositoryListFragment();
        case 2:
            return defaultUser ? new StarredRepositoryListFragment()
                    : new OrgMembersFragment();
        case 3:
            return defaultUser ? new MyFollowersFragment() : new TeamListFragment();
        case 4:
            return new MyFollowingFragment();
        default:
            return null;
        }
    }

    /**
     * This methods clears any fragments that may not apply to the newly
     * selected org.
     *
     * @param isDefaultUser
     * @return this adapter
     */
    public HomePagerAdapter clearAdapter(boolean isDefaultUser) {
        defaultUser = isDefaultUser;

        if (tags.isEmpty())
            return this;

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        for (String tag : tags) {
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            if (fragment != null)
                transaction.remove(fragment);
        }
        transaction.commit();
        tags.clear();

        return this;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Object fragment = super.instantiateItem(container, position);
        if (fragment instanceof Fragment)
            tags.add(((Fragment) fragment).getTag());
        return fragment;
    }

    @Override
    public int getCount() {
        return defaultUser ? 5 : 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return resources.getString(R.string.tab_news);
        case 1:
            return resources.getString(R.string.tab_repositories);
        case 2:
            return resources.getString(defaultUser ? R.string.tab_stars
                    : R.string.tab_members);
        case 3:
            return resources.getString(defaultUser ? R.string.tab_followers_self
                    : R.string.tab_teams);
        case 4:
            return resources.getString(R.string.tab_following_self);
        default:
            return null;
        }
    }
}
