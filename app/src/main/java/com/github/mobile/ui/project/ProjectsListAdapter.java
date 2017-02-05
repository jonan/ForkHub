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
package com.github.mobile.ui.project;

import android.app.Activity;
import android.text.TextUtils;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Project;
import com.github.mobile.util.TimeUtils;

/**
 * Adapter to display a list of {@link Project} objects
 */
public class ProjectsListAdapter extends SingleTypeAdapter<Project> {

    private final String updateText;

    /**
     * Create {@link Project} list adapter
     *
     * @param activity
     * @param elements
     */
    public ProjectsListAdapter(Activity activity, Project[] elements) {
        super(activity.getLayoutInflater(), R.layout.project_item);

        updateText = activity.getString(R.string.updated);

        setItems(elements);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_project_name, R.id.tv_description, R.id.tv_update_date };
    }

    @Override
    protected void update(int position, Project project) {
        setText(0, project.name);
        setText(1, project.body);
        setGone(1, TextUtils.isEmpty(project.body));
        setText(2, String.format(updateText, TimeUtils.getRelativeTime(project.updated_at)));
    }
}
