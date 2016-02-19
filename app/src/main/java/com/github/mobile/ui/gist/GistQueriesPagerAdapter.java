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
package com.github.mobile.ui.gist;

import com.github.mobile.R;
import com.github.mobile.ui.FragmentPagerAdapter;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

/**
 * Pager adapter for different Gist queries
 */
public class GistQueriesPagerAdapter extends FragmentPagerAdapter {

    private final Resources resources;

    /**
     * Create pager adapter
     *
     * @param activity
     */
    public GistQueriesPagerAdapter(AppCompatActivity activity) {
        super(activity);

        resources = activity.getResources();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
        case 0:
            return new MyGistsFragment();
        case 1:
            return new StarredGistsFragment();
        case 2:
            return new PublicGistsFragment();
        default:
            return null;
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
        case 0:
            return resources.getString(R.string.tab_mine);
        case 1:
            return resources.getString(R.string.tab_starred);
        case 2:
            return resources.getString(R.string.tab_all);
        default:
            return null;
        }
    }
}
