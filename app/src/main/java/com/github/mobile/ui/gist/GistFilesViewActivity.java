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

import com.google.inject.Inject;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.gist.FullGist;
import com.github.mobile.core.gist.GistStore;
import com.github.mobile.core.gist.RefreshGistTask;
import com.github.mobile.ui.FragmentProvider;
import com.github.mobile.ui.PagerActivity;
import com.github.mobile.ui.ViewPager;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.viewpagerindicator.TitlePageIndicator;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.User;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.ProgressBar;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;
import static com.github.mobile.Intents.EXTRA_GIST_ID;
import static com.github.mobile.Intents.EXTRA_POSITION;

/**
 * Activity to page through the content of all the files in a Gist
 */
public class GistFilesViewActivity extends PagerActivity {

    /**
     * Create intent to show files with an initial selected file
     *
     * @param gist
     * @param position
     * @return intent
     */
    public static Intent createIntent(Gist gist, int position) {
        return new Builder("gist.files.VIEW").gist(gist.getId())
                .add(EXTRA_POSITION, position).toIntent();
    }

    private String gistId;

    private int initialPosition;

    private ViewPager pager;

    private ProgressBar loadingBar;

    private TitlePageIndicator indicator;

    private Gist gist;

    @Inject
    private GistStore store;

    @Inject
    private AvatarLoader avatars;

    @Inject
    private HttpImageGetter imageGetter;

    private GistFilesPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gistId = getStringExtra(EXTRA_GIST_ID);
        initialPosition = getIntExtra(EXTRA_POSITION);

        setContentView(R.layout.pager_with_title);

        pager = finder.find(R.id.vp_pages);
        loadingBar = finder.find(R.id.pb_loading);
        indicator = finder.find(R.id.tpi_header);

        if (initialPosition < 0)
            initialPosition = 0;

        getSupportActionBar().setTitle(getString(R.string.gist_title) + gistId);

        gist = store.getGist(gistId);
        if (gist != null)
            configurePager();
        else {
            ViewUtils.setGone(loadingBar, false);
            ViewUtils.setGone(pager, true);
            ViewUtils.setGone(indicator, true);
            new RefreshGistTask(this, gistId, imageGetter) {

                @Override
                protected void onSuccess(FullGist gist) throws Exception {
                    super.onSuccess(gist);

                    GistFilesViewActivity.this.gist = gist.getGist();
                    configurePager();
                }

            }.execute();
        }
    }

    private void configurePager() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        User owner = gist.getOwner();
        if (owner != null) {
            actionBar.setSubtitle(owner.getLogin());
            avatars.bind(actionBar, owner);
        } else
            actionBar.setSubtitle(R.string.anonymous);

        ViewUtils.setGone(loadingBar, true);
        ViewUtils.setGone(pager, false);
        ViewUtils.setGone(indicator, false);

        adapter = new GistFilesPagerAdapter(this, gist);
        pager.setAdapter(adapter);
        indicator.setViewPager(pager);

        if (initialPosition < adapter.getCount()) {
            pager.scheduleSetItem(initialPosition);
            onPageSelected(initialPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            if (gist != null) {
                Intent intent = GistsViewActivity.createIntent(gist);
                intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP
                        | FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected FragmentProvider getProvider() {
        return adapter;
    }
}
