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
package com.github.mobile.ui;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;

/**
 * Activity with tabbed pages
 *
 * @param <V>
 */
public abstract class TabPagerActivity<V extends PagerAdapter & FragmentProvider>
        extends PagerActivity {

    /**
     * View pager
     */
    protected ViewPager pager;

    /**
     * Tab layout
     */
    protected TabLayout tabLayout;

    /**
     * Pager adapter
     */
    protected V adapter;

    @Override
    public void onPageSelected(final int position) {
        super.onPageSelected(position);

        setCurrentItem(position);
    }

    /**
     * Create pager adapter
     *
     * @return pager adapter
     */
    protected abstract V createAdapter();

    /**
     * Get title for position
     *
     * @param position
     * @return title
     */
    protected String getTitle(final int position) {
        return adapter.getPageTitle(position).toString();
    }

    /**
     * Get icon for position
     *
     * @param position
     * @return icon
     */
    protected String getIcon(final int position) {
        return null;
    }

    /**
     * Set tab and pager as gone or visible
     *
     * @param gone
     * @return this activity
     */
    protected TabPagerActivity<V> setGone(boolean gone) {
        ViewUtils.setGone(tabLayout, gone);
        ViewUtils.setGone(pager, gone);
        return this;
    }

    /**
     * Set current item to new position
     * <p>
     * This is guaranteed to only be called when a position changes and the
     * current item of the pager has already been updated to the given position
     * <p>
     * Sub-classes may override this method
     *
     * @param position
     */
    protected void setCurrentItem(final int position) {
        // Intentionally left blank
    }

    /**
     * Get content view to be used when {@link #onCreate(Bundle)} is called
     *
     * @return layout resource id
     */
    protected int getContentView() {
        return R.layout.pager_with_tabs;
    }

    private void createPager() {
        adapter = createAdapter();
        invalidateOptionsMenu();
        pager.setAdapter(adapter);
    }

    /**
     * Create tab using information from current adapter
     * <p>
     * This can be called when the tabs changed but must be called after an
     * initial call to {@link #configureTabPager()}
     */
    protected void createTabs() {
        tabLayout.setupWithViewPager(pager);
    }

    /**
     * Configure tabs and pager
     */
    protected void configureTabPager() {
        if (adapter == null) {
            createPager();
            createTabs();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentView());
        pager = (ViewPager) findViewById(R.id.vp_pages);
        pager.setOnPageChangeListener(this);
        tabLayout = (TabLayout) findViewById(R.id.th_tabs);
    }

    @Override
    protected FragmentProvider getProvider() {
        return adapter;
    }
}
