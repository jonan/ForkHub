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

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.core.user.UserComparator;
import com.github.mobile.persistence.AccountDataManager;
import com.github.mobile.ui.TabPagerActivity;
import com.github.mobile.ui.gist.GistsActivity;
import com.github.mobile.ui.issue.FiltersViewActivity;
import com.github.mobile.ui.issue.IssueDashboardActivity;
import com.github.mobile.ui.repo.OrganizationLoader;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.PreferenceUtils;
import com.github.mobile.util.TypefaceUtils;
import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.github.core.User;

/**
 * Home screen activity
 */
public class HomeActivity extends TabPagerActivity<HomePagerAdapter> implements
        NavigationView.OnNavigationItemSelectedListener,
        OrganizationSelectionProvider,
        LoaderCallbacks<List<User>> {

    private static final String TAG = "HomeActivity";

    private static final String PREF_ORG_ID = "orgId";

    @Inject
    private AccountDataManager accountDataManager;

    @Inject
    private Provider<UserComparator> userComparatorProvider;

    private boolean isDefaultUser;

    private List<User> orgs = Collections.emptyList();

    private DrawerLayout navigationDrawer;

    private NavigationView navigationView;

    private ColorStateList navigationIconTint;

    private boolean isUserNavVisible = false;

    private Set<OrganizationSelectionListener> orgSelectionListeners = new LinkedHashSet<OrganizationSelectionListener>();

    private User org;

    @Inject
    private AvatarLoader avatars;

    @Inject
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        navigationDrawer = (DrawerLayout) findViewById(R.id.navigation_drawer);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, navigationDrawer, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (isUserNavVisible) {
                    swapNavigationMenu();
                }

            }
        };

        navigationDrawer.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

        navigationIconTint = navigationView.getItemIconTintList();
        findViewById(R.id.nav_header).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapNavigationMenu();
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void reloadOrgs() {
        getSupportLoaderManager().restartLoader(0, null,
                new LoaderCallbacks<List<User>>() {

                    @Override
                    public Loader<List<User>> onCreateLoader(int id, Bundle bundle) {
                        return HomeActivity.this.onCreateLoader(id, bundle);
                    }

                    @Override
                    public void onLoadFinished(Loader<List<User>> loader, final List<User> users) {
                        HomeActivity.this.onLoadFinished(loader, users);
                        if (users.isEmpty())
                            return;

                        Window window = getWindow();
                        if (window == null)
                            return;
                        View view = window.getDecorView();
                        if (view == null)
                            return;

                        view.post(new Runnable() {

                            @Override
                            public void run() {
                                isDefaultUser = false;
                                setOrg(users.get(0));
                            }
                        });
                    }

                    @Override
                    public void onLoaderReset(Loader<List<User>> loader) {
                        HomeActivity.this.onLoaderReset(loader);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Restart loader if default account doesn't match currently loaded
        // account
        List<User> currentOrgs = orgs;
        if (currentOrgs != null && !currentOrgs.isEmpty()
                && !AccountUtils.isUser(this, currentOrgs.get(0)))
            reloadOrgs();
    }

    private void setOrg(User org) {
        Log.d(TAG, "setOrg : " + org.getLogin());

        PreferenceUtils.save(sharedPreferences.edit().putInt(PREF_ORG_ID,
                org.getId()));

        // Don't notify listeners or change pager if org hasn't changed
        if (this.org != null && this.org.getId() == org.getId())
            return;

        this.org = org;

        avatars.bind((ImageView) findViewById(R.id.avatar), org);
        ((TextView) findViewById(R.id.user_name)).setText(org.getLogin());

        boolean isDefaultUser = AccountUtils.isUser(this, org);
        boolean changed = this.isDefaultUser != isDefaultUser;
        this.isDefaultUser = isDefaultUser;
        if (adapter == null)
            configureTabPager();
        else if (changed) {
            int item = pager.getCurrentItem();
            adapter.clearAdapter(isDefaultUser);
            adapter.notifyDataSetChanged();
            createTabs();
            if (item >= adapter.getCount())
                item = adapter.getCount() - 1;
            pager.setItem(item);
        }

        for (OrganizationSelectionListener listener : orgSelectionListeners)
            listener.onOrganizationSelected(org);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu optionMenu) {
        getMenuInflater().inflate(R.menu.home, optionMenu);

        return super.onCreateOptionsMenu(optionMenu);
    }

    @Override
    public void onBackPressed() {
        if (navigationDrawer.isDrawerOpen(GravityCompat.START)) {
            navigationDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_search:
            onSearchRequested();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() < orgs.size()) {
            setOrg(orgs.get(menuItem.getItemId()));
            navigationDrawer.closeDrawer(GravityCompat.START);
            return false;
        }

        switch (menuItem.getItemId()) {
            case R.id.navigation_gists:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, GistsActivity.class));
                break;
            case R.id.navigation_dashboard:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this, IssueDashboardActivity.class));
                break;
            case R.id.navigation_bookmarks:
                navigationDrawer.closeDrawer(GravityCompat.START);
                startActivity(FiltersViewActivity.createIntent());
                break;
            case R.id.navigation_report_issue:
                navigationDrawer.closeDrawer(GravityCompat.START);
                UriLauncherActivity.launchUri(this,
                        Uri.parse("https://github.com/jonan/ForkHub/issues"));
                break;
        }

        return false;
    }

    @Override
    public Loader<List<User>> onCreateLoader(int i, Bundle bundle) {
        return new OrganizationLoader(this, accountDataManager,
                userComparatorProvider);
    }

    @Override
    public void onLoadFinished(Loader<List<User>> listLoader, List<User> orgs) {
        this.orgs = orgs;

        int sharedPreferencesOrgId = sharedPreferences.getInt(PREF_ORG_ID, -1);
        int targetOrgId = org == null ? sharedPreferencesOrgId : org.getId();

        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.user_select);
        for (int i = 0; i < orgs.size(); ++i) {
            final MenuItem item = menu.add(R.id.user_select, i, Menu.NONE, orgs.get(i).getLogin());
            avatars.bind(item, orgs.get(i));
            if (orgs.get(i).getId() == targetOrgId) {
                setOrg(orgs.get(i));
            }
        }

        // If the target org is invalid (e.g. first login), select the first one
        if (targetOrgId == -1 && orgs.size() > 0) {
            setOrg(orgs.get(0));
        }

        menu.setGroupVisible(R.id.user_select, false);
    }

    @Override
    public void onLoaderReset(Loader<List<User>> listLoader) {
    }

    @Override
    public User addListener(OrganizationSelectionListener listener) {
        if (listener != null)
            orgSelectionListeners.add(listener);
        return org;
    }

    @Override
    public OrganizationSelectionProvider removeListener(
            OrganizationSelectionListener listener) {
        if (listener != null)
            orgSelectionListeners.remove(listener);
        return this;
    }

    @Override
    protected int getContentView() {
        return R.layout.home;
    }

    @Override
    protected HomePagerAdapter createAdapter() {
        return new HomePagerAdapter(this, isDefaultUser);
    }

    @Override
    protected String getIcon(int position) {
        switch (position) {
        case 0:
            return TypefaceUtils.ICON_RSS;
        case 1:
            return TypefaceUtils.ICON_REPO;
        case 2:
            return isDefaultUser ? TypefaceUtils.ICON_EYE : TypefaceUtils.ICON_ORGANIZATION;
        case 3:
            return TypefaceUtils.ICON_BROADCAST;
        default:
            return super.getIcon(position);
        }
    }

    private void swapNavigationMenu() {
        Menu menu = navigationView.getMenu();
        if (isUserNavVisible) {
            menu.setGroupVisible(R.id.user_select, false);
            menu.setGroupVisible(R.id.navigation_menu, true);
            menu.setGroupVisible(R.id.navigation_extra, true);
            navigationView.setItemIconTintList(navigationIconTint);
        } else {
            menu.setGroupVisible(R.id.user_select, true);
            menu.setGroupVisible(R.id.navigation_menu, false);
            menu.setGroupVisible(R.id.navigation_extra, false);
            navigationView.setItemIconTintList(null);
        }
        isUserNavVisible = !isUserNavVisible;
    }
}
