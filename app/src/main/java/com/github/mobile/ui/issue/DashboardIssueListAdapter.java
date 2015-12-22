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

import com.github.mobile.R;
import com.github.mobile.core.issue.IssueUtils;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.RepositoryIssue;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Adapter to display a list of dashboard issues
 */
public class DashboardIssueListAdapter extends
        IssueListAdapter<RepositoryIssue> {

    private int numberPaintFlags;

    /**
     * Create adapter
     *
     * @param avatars
     * @param inflater
     * @param elements
     */
    public DashboardIssueListAdapter(AvatarLoader avatars,
            LayoutInflater inflater, RepositoryIssue[] elements) {
        super(R.layout.dashboard_issue_item, inflater, elements, avatars);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).getId();
    }

    @Override
    protected int getNumber(final RepositoryIssue issue) {
        return issue.getNumber();
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        numberPaintFlags = textView(view, 1).getPaintFlags();
        setText(6, TypefaceUtils.ICON_GIT_PULL_REQUEST);
        TextView commentIcon = (TextView) view .findViewById(R.id.tv_comment_icon);
        commentIcon.setText(TypefaceUtils.ICON_COMMENT);
        TypefaceUtils.setOcticons(textView(view, 6), commentIcon);
        return view;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_issue_repo_name, R.id.tv_issue_number,
                R.id.tv_issue_title, R.id.iv_avatar, R.id.tv_issue_creation,
                R.id.tv_issue_comments, R.id.tv_pull_request_icon, R.id.v_label0,
                R.id.v_label1, R.id.v_label2, R.id.v_label3, R.id.v_label4,
                R.id.v_label5, R.id.v_label6, R.id.v_label7 };
    }

    @Override
    protected void update(int position, RepositoryIssue issue) {
        updateNumber(issue.getNumber(), issue.getState(), numberPaintFlags, 1);

        avatars.bind(imageView(3), issue.getUser());

        String[] segments = issue.getUrl().split("/");
        int length = segments.length;
        if (length >= 4)
            setText(0, segments[length - 4] + '/' + segments[length - 3]);
        else
            setText(0, null);

        setGone(6, !IssueUtils.isPullRequest(issue));

        setText(2, issue.getTitle());

        updateReporter(issue.getUser().getLogin(), issue.getCreatedAt(), 4);
        setNumber(5, issue.getComments());
        updateLabels(issue.getLabels(), 7);
    }
}
