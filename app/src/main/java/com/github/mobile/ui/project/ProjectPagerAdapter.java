/*
 * Copyright 2017 Jon Ander Peñalba
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
package com.github.mobile.ui.project;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.github.mobile.api.model.ProjectColumn;
import com.github.mobile.ui.FragmentPagerAdapter;

import java.util.List;

import static com.github.mobile.Intents.EXTRA_PROJECT_COLUMN;

/**
 * Pager adapter for a project's different columns
 */
public class ProjectPagerAdapter extends FragmentPagerAdapter {

    private final List<ProjectColumn> columns;

    /**
     * @param activity
     */
    public ProjectPagerAdapter(final AppCompatActivity activity, final List<ProjectColumn> columns) {
        super(activity);

        this.columns = columns;
    }

    @Override
    public Fragment getItem(final int position) {
        ProjectColumnFragment fragment = new ProjectColumnFragment();
        Bundle args = new Bundle();
        args.putLong(EXTRA_PROJECT_COLUMN, columns.get(position).id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return columns.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return columns.get(position).name;
    }
}
