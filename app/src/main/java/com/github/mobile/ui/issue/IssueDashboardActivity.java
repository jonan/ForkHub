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
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.github.mobile.R;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.user.HomeActivity;
import com.github.mobile.util.TypefaceUtils;

/**
 * Dashboard activity for issues
 */
public class IssueDashboardActivity extends
        TabPagerActivity<IssueDashboardPagerAdapter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.dashboard_issues_title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        configureTabPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected IssueDashboardPagerAdapter createAdapter() {
        return new IssueDashboardPagerAdapter(this);
    }

    @Override
    protected String getIcon(int position) {
        switch (position) {
        case 0:
            return TypefaceUtils.ICON_EYE;
        case 1:
            return TypefaceUtils.ICON_PERSON;
        case 2:
            return TypefaceUtils.ICON_PLUS;
        case 3:
            return TypefaceUtils.ICON_RADIO_TOWER;
        default:
            return super.getIcon(position);
        }
    }
}
