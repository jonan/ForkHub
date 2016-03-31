/*
 * Copyright 2016 Jon Ander Peñalba
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
package com.github.mobile.ui.team;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.mobile.R;
import com.github.mobile.ui.FragmentPagerAdapter;

/**
 * Pager adapter for a team's different views
 */
public class TeamPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    /**
     * @param activity
     */
    public TeamPagerAdapter(final AppCompatActivity activity) {
        super(activity);

        resources = activity.getResources();
    }

    @Override
    public Fragment getItem(final int position) {
        switch (position) {
        case 0:
            return new TeamMembersFragment();
        case 1:
            return new TeamRepositoryListFragment();
        default:
            return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return resources.getString(R.string.tab_members);
        case 1:
            return resources.getString(R.string.tab_repositories);
        default:
            return null;
        }
    }
}
