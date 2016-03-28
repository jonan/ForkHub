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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.github.mobile.Intents;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_TEAM;
import static com.github.mobile.Intents.EXTRA_USER;

/**
 * Activity to view a team's various pages
 */
public class TeamViewActivity extends TabPagerActivity<TeamPagerAdapter> {

    /**
     * Create intent for this activity
     *
     * @param team
     * @param org
     * @return intent
     */
    public static Intent createIntent(final Team team, final User org) {
        Intents.Builder builder = new Intents.Builder("team.VIEW");
        builder.team(team);
        builder.user(org);
        return builder.toIntent();
    }

    @Inject
    private AvatarLoader avatars;

    private User org;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Team team = (Team) getIntent().getSerializableExtra(EXTRA_TEAM);
        org = (User) getIntent().getSerializableExtra(EXTRA_USER);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(team.getName());
        actionBar.setSubtitle(org.getLogin());

        avatars.bind(getSupportActionBar(), org);
        configureTabPager();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            Intent intent = UserViewActivity.createIntent(org);
            intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected TeamPagerAdapter createAdapter() {
        return new TeamPagerAdapter(this);
    }
}
