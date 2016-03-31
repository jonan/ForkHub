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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mobile.R;
import com.github.mobile.core.gist.RandomGistTask;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.user.HomeActivity;
import com.github.mobile.util.TypefaceUtils;

/**
 * Activity to display view pagers of different Gist queries
 */
public class GistsActivity extends TabPagerActivity<GistQueriesPagerAdapter> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(R.string.gists_title);
        actionBar.setDisplayHomeAsUpEnabled(true);

        configureTabPager();
    }

    private void randomGist() {
        new RandomGistTask(this).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionsMenu) {
        getMenuInflater().inflate(R.menu.gists, optionsMenu);

        return super.onCreateOptionsMenu(optionsMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_random:
            randomGist();
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

    @Override
    protected GistQueriesPagerAdapter createAdapter() {
        return new GistQueriesPagerAdapter(this);
    }

    @Override
    protected String getIcon(int position) {
        switch (position) {
        case 0:
            return TypefaceUtils.ICON_PERSON;
        case 1:
            return TypefaceUtils.ICON_STAR;
        case 2:
            return TypefaceUtils.ICON_ORGANIZATION;
        default:
            return super.getIcon(position);
        }
    }
}
