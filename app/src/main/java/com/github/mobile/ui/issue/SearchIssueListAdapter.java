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

import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.SearchIssue;
import org.eclipse.egit.github.core.User;

/**
 * Adapter for a list of searched for issues
 */
public class SearchIssueListAdapter extends IssueListAdapter<SearchIssue> {

    private int numberPaintFlags;

    /**
     * @param inflater
     * @param elements
     * @param avatars
     */
    public SearchIssueListAdapter(LayoutInflater inflater,
            Resources resources, SearchIssue[] elements, AvatarLoader avatars) {
        super(R.layout.repo_issue_item, inflater, resources, elements, avatars);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getNumber();
    }

    @Override
    protected int getNumber(SearchIssue issue) {
        return issue.getNumber();
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        numberPaintFlags = textView(view, 0).getPaintFlags();

        setText(4, TypefaceUtils.ICON_GIT_PULL_REQUEST);
        setText(5, TypefaceUtils.ICON_COMMENT);
        TypefaceUtils.setOcticons(textView(view, 4), textView(view, 5));

        setGone(4, true);

        for (int i = 0; i < MAX_LABELS; i++)
            ViewUtils.setGone(view.findViewById(R.id.v_label0 + i), true);

        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_issue_number, R.id.tv_issue_title, R.id.iv_avatar,
                R.id.tv_issue_creation, R.id.tv_pull_request_icon,
                R.id.tv_comment_icon, R.id.tv_issue_comments, };
    }

    @Override
    protected void update(int position, SearchIssue issue) {
        updateNumber(issue.getNumber(), issue.getState(), numberPaintFlags, 0);

        String gravatarId = issue.getGravatarId();
        User user;
        if (!TextUtils.isEmpty(gravatarId))
            user = new User().setGravatarId(gravatarId);
        else
            user = null;
        avatars.bind(imageView(2), user);

        setText(1, issue.getTitle());

        updateReporter(issue.getUser(), issue.getCreatedAt(), 3);

        setNumber(6, issue.getComments());

        if (issue.getComments() > 0) {
            textView(5).setTextColor(resources.getColor(R.color.text_icon_highlighted));
            textView(6).setTextColor(resources.getColor(R.color.text_icon_highlighted));
        } else {
            textView(5).setTextColor(resources.getColor(R.color.text_icon_disabled));
            textView(6).setTextColor(resources.getColor(R.color.text_icon_disabled));
        }
    }
}
