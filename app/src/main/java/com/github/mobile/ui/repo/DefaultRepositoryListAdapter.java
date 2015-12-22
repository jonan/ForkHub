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
package com.github.mobile.ui.repo;

import com.github.mobile.R;
import com.github.mobile.ui.StyledText;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Adapter for the default account's repositories
 */
public class DefaultRepositoryListAdapter extends
        RepositoryListAdapter<Repository> {

    private int descriptionColor;

    private final AtomicReference<User> account;

    private final Map<Long, String> headers = new HashMap<Long, String>();

    private final Set<Long> noSeparators = new HashSet<Long>();

    /**
     * Create list adapter for repositories
     *
     * @param inflater
     * @param elements
     * @param account
     */
    public DefaultRepositoryListAdapter(LayoutInflater inflater,
            Repository[] elements, AtomicReference<User> account) {
        super(R.layout.repo_item, inflater, elements);

        this.account = account;
    }

    /**
     * Clear registered header values
     *
     * @return this adapter
     */
    public DefaultRepositoryListAdapter clearHeaders() {
        headers.clear();
        noSeparators.clear();
        return this;
    }

    /**
     * Register section header
     *
     * @param repository
     * @param text
     * @return this adapter
     */
    public DefaultRepositoryListAdapter registerHeader(Repository repository,
            String text) {
        headers.put(repository.getId(), text);
        return this;
    }

    /**
     * Register repository to have no bottom separator
     *
     * @param repository
     * @return this adapter
     */
    public DefaultRepositoryListAdapter registerNoSeparator(
            Repository repository) {
        noSeparators.add(repository.getId());
        return this;
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        TextView forksIcon = (TextView) view.findViewById(R.id.tv_forks_icon);
        forksIcon.setText(TypefaceUtils.ICON_REPO_FORKED);
        TextView watchersIcon = (TextView) view.findViewById(R.id.tv_watchers_icon);
        watchersIcon.setText(TypefaceUtils.ICON_STAR);

        TypefaceUtils.setOcticons(textView(view, 0), forksIcon, watchersIcon);
        descriptionColor = view.getResources().getColor(R.color.text_description);
        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_repo_icon, R.id.tv_repo_description,
                R.id.tv_language, R.id.tv_watchers, R.id.tv_forks, R.id.ll_header,
                R.id.tv_header, R.id.v_separator, R.id.tv_repo_name };
    }

    @Override
    protected void update(int position, Repository repository) {
        String headerValue = headers.get(repository.getId());
        if (headerValue != null) {
            setGone(5, false);
            setText(6, headerValue);
        } else
            setGone(5, true);

        setGone(7, noSeparators.contains(repository.getId()));

        StyledText name = new StyledText();
        if (!account.get().getLogin().equals(repository.getOwner().getLogin()))
            name.foreground(repository.getOwner().getLogin(), descriptionColor)
                    .foreground('/', descriptionColor);
        name.bold(repository.getName());
        setText(8, name);

        updateDetails(repository.getDescription(), repository.getLanguage(),
                repository.getWatchers(), repository.getForks(),
                repository.isPrivate(), repository.isFork(),
                repository.getMirrorUrl());
    }
}
