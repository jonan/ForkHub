/*
 * Copyright 2013 GitHub Inc.
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
package com.github.mobile.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.github.mobile.R;
import com.github.mobile.api.model.Repository;
import com.github.mobile.ui.StyledText;
import com.github.mobile.ui.repo.RepositoryListAdapter;
import com.github.mobile.util.TypefaceUtils;

/**
 * Adapter for a list of searched for repositories
 */
public class SearchRepositoryListAdapter extends
        RepositoryListAdapter<Repository> {

    /**
     * Create list adapter for searched for repositories
     *
     * @param inflater
     * @param elements
     */
    public SearchRepositoryListAdapter(LayoutInflater inflater,
            Repository[] elements) {
        super(R.layout.user_repo_item, inflater, elements);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).id;
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        TextView forksIcon = (TextView) view.findViewById(R.id.tv_forks_icon);
        forksIcon.setText(TypefaceUtils.ICON_REPO_FORKED);
        TextView watchersIcon = (TextView) view.findViewById(R.id.tv_watchers_icon);
        watchersIcon.setText(TypefaceUtils.ICON_STAR);

        TypefaceUtils.setOcticons(textView(view, 0), forksIcon, watchersIcon);
        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_repo_icon, R.id.tv_repo_description,
                R.id.tv_language, R.id.tv_watchers, R.id.tv_forks, R.id.tv_repo_name };
    }

    @Override
    protected void update(int position, Repository repository) {
        StyledText name = new StyledText();
        name.append(repository.owner.login).append('/');
        name.bold(repository.name);
        setText(5, name);

        updateDetails(repository.description, repository.language,
                repository.stargazers_count, repository.forks_count,
                repository.is_private, repository.is_fork, repository.mirror_url);
    }
}
