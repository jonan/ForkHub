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
package com.github.mobile.ui.comment;


import static com.github.mobile.util.TypefaceUtils.ICON_ISSUE_CLOSE;
import static com.github.mobile.util.TypefaceUtils.ICON_ISSUE_REOPEN;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

import java.util.Collection;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;

/**
 * Adapter for a list of {@link Comment} objects
 */
public class CommentListAdapter extends MultiTypeAdapter {

    private final AvatarLoader avatars;

    private final HttpImageGetter imageGetter;

    private Issue issue;

    /**
     * Create list adapter
     *
     * @param inflater
     * @param elements
     * @param avatars
     * @param imageGetter
     * @param issue
     */
    public CommentListAdapter(LayoutInflater inflater, Comment[] elements,
            AvatarLoader avatars, HttpImageGetter imageGetter, Issue issue) {
        super(inflater);

        this.avatars = avatars;
        this.imageGetter = imageGetter;
        this.issue = issue;
        setItems(elements);
    }

    /**
     * Create list adapter
     *
     * @param inflater
     * @param avatars
     * @param imageGetter
     * @param issue
     */
    public CommentListAdapter(LayoutInflater inflater, AvatarLoader avatars,
            HttpImageGetter imageGetter, Issue issue) {
        this(inflater, null, avatars, imageGetter, issue);
    }

    @Override
    protected void update(int i, Object o, int type) {
        if(type == 0)
            updateComment((Comment)o);
        else
            updateEvent((IssueEvent)o);
    }

    protected void updateEvent(final IssueEvent event) {
        TypefaceUtils.setOcticons(textView(0));
        String message = String.format("<b>%s</b> %s", event.getActor().getLogin(), event.getEvent());
        avatars.bind(imageView(2), event.getActor());

        String eventString = event.getEvent();

        switch (eventString) {
        case "closed":
            message += " this ";
            setText(0, ICON_ISSUE_CLOSE);
            textView(0).setTextColor(Color.rgb(189,44,0));
            break;
        case "reopened":
            message += " this ";
            setText(0, ICON_ISSUE_REOPEN);
            textView(0).setTextColor(Color.rgb(108,198,68));
            break;
        case "merged":
            message += String.format(" commit <b>%s</b> into <tt>%s</tt> from <tt>%s</tt> ", event.getCommitId().substring(0,6), issue.getPullRequest().getBase().getRef(),
                    issue.getPullRequest().getHead().getRef());
            setText(0, "\uf023");
            textView(0).setTextColor(Color.rgb(110,84,148));
            break;
        }

        message += TimeUtils.getRelativeTime(event.getCreatedAt());
        setText(1, Html.fromHtml(message));
    }

    protected void updateComment(final Comment comment) {
        imageGetter.bind(textView(0), comment.getBodyHtml(), comment.getId());
        avatars.bind(imageView(3), comment.getUser());

        setText(1, comment.getUser().getLogin());
        setText(2, TimeUtils.getRelativeTime(comment.getUpdatedAt()));
    }

    public MultiTypeAdapter setItems(Collection<?> items) {
        if (items == null || items.isEmpty())
            return this;
        return setItems(items.toArray());
    }

    public MultiTypeAdapter setItems(final Object[] items) {
        if (items == null || items.length == 0)
            return this;

        this.clear();

        for (Object item : items) {
            if(item instanceof Comment)
                this.addItem(0, item);
            else
                this.addItem(1, item);
        }

        notifyDataSetChanged();
        return this;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    @Override
    protected View initialize(int type, View view) {
        view = super.initialize(type, view);

        textView(view, 0).setMovementMethod(LinkMovementMethod.getInstance());
        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected int getChildLayoutId(int i) {
        if(i == 0)
            return R.layout.comment_item;
        else
            return R.layout.comment_event_item;
    }

    @Override
    protected int[] getChildViewIds(int i) {
        if(i == 0)
            return new int[] { R.id.tv_comment_body, R.id.tv_comment_author,
                    R.id.tv_comment_date, R.id.iv_avatar };
        else
            return new int[]{R.id.tv_event_icon, R.id.tv_event, R.id.iv_avatar};
    }
}
