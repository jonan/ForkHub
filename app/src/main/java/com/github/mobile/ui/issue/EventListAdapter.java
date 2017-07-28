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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.api.model.TimelineEvent;
import com.github.mobile.api.model.User;
import com.github.mobile.ui.ReactionsView;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Adapter for a list of {@link TimelineEvent} objects
 */
public class EventListAdapter extends MultiTypeAdapter {
    private static final int VIEW_COMMENT = 0;
    private static final int VIEW_EVENT = 1;
    private static final int VIEW_TOTAL = 2;

    private static final List<String> CLICKABLE_EVENTS = Arrays.asList(
            TimelineEvent.EVENT_CLOSED,
            TimelineEvent.EVENT_CROSS_REFERENCED,
            TimelineEvent.EVENT_MERGED,
            TimelineEvent.EVENT_REFERENCED);

    private final Context context;

    private final Resources resources;

    private final AvatarLoader avatars;

    private final HttpImageGetter imageGetter;

    private final IssueFragment issueFragment;

    private final String user;

    private final boolean isCollaborator;


    /**
     * Create list adapter
     *
     * @param activity
     * @param avatars
     * @param imageGetter
     * @param issueFragment
     */
    public EventListAdapter(Activity activity, AvatarLoader avatars,
                            HttpImageGetter imageGetter, IssueFragment issueFragment,
                            boolean isCollaborator, String loggedUser) {
        super(activity.getLayoutInflater());

        this.context = activity;
        this.resources = activity.getResources();
        this.avatars = avatars;
        this.imageGetter = imageGetter;
        this.issueFragment = issueFragment;
        this.isCollaborator = isCollaborator;
        this.user = loggedUser;
    }

    @Override
    protected void update(int position, Object obj, int type) {
        switch (type) {
        case VIEW_COMMENT:
            updateComment((TimelineEvent) obj);
            break;
        case VIEW_EVENT:
            updateEvent((TimelineEvent) obj);
            break;
        }
    }

    private void updateEvent(final TimelineEvent event) {
        String eventString = event.event;

        User actor;
        switch (eventString) {
        case TimelineEvent.EVENT_ASSIGNED:
        case TimelineEvent.EVENT_UNASSIGNED:
            actor = event.assignee;
            break;
        case TimelineEvent.EVENT_REVIEWED:
            actor = event.user;
            break;
        default:
            actor = event.actor;
            break;
        }

        String message = String.format("<b>%s</b> ", actor == null ? "ghost" : actor.login);
        if (actor != null) {
            setGone(2, false);
            avatars.bind(imageView(2), actor);
        } else {
            setGone(2, true);
        }

        switch (eventString) {
        case TimelineEvent.EVENT_ASSIGNED:
            int assignedTextResource = R.string.issue_event_label_assigned;
            if (event.actor.id == event.assignee.id) {
                assignedTextResource = R.string.issue_event_label_self_assigned;
            }
            message += String.format(resources.getString(assignedTextResource), "<b>" + event.actor.login + "</b>");
            setText(0, TypefaceUtils.ICON_PERSON);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_UNASSIGNED:
            int unassignedTextResource = R.string.issue_event_label_unassigned;
            if (event.actor.id == event.assignee.id) {
                unassignedTextResource = R.string.issue_event_label_self_unassigned;
            }
            message += String.format(resources.getString(unassignedTextResource), "<b>" + event.actor.login + "</b>");
            setText(0, TypefaceUtils.ICON_PERSON);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_LABELED:
            message += String.format(resources.getString(R.string.issue_event_label_added), "<b>" + event.label.name + "</b>");
            setText(0, TypefaceUtils.ICON_TAG);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_UNLABELED:
            message += String.format(resources.getString(R.string.issue_event_label_removed), "<b>" + event.label.name + "</b>");
            setText(0, TypefaceUtils.ICON_TAG);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_REFERENCED:
            message += String.format(resources.getString(R.string.issue_event_referenced), "<b>" + event.commit_id.substring(0,7) + "</b>");
            setText(0, TypefaceUtils.ICON_BOOKMARK);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_CROSS_REFERENCED:
            Issue issue = event.source.issue;
            String crossRef = issue.repository.full_name + "#" + issue.number;
            message += String.format(resources.getString(R.string.issue_event_cross_referenced), "<b>" + crossRef + "</b>");
            setText(0, TypefaceUtils.ICON_BOOKMARK);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_REVIEW_REQUESTED:
            message += String.format(resources.getString(R.string.issue_event_review_requested), "<b>" + event.requested_reviewer.login + "</b>");
            setText(0, TypefaceUtils.ICON_EYE);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_REVIEW_REQUEST_REMOVED:
            message += String.format(resources.getString(R.string.issue_event_review_request_removed), "<b>" + event.requested_reviewer.login + "</b>");
            setText(0, TypefaceUtils.ICON_X);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_REVIEWED:
            switch (event.state) {
            case TimelineEvent.STATE_PENDING:
                message += resources.getString(R.string.issue_event_review_pending);
                setText(0, TypefaceUtils.ICON_EYE);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
                break;
            case TimelineEvent.STATE_COMMENTED:
                message += resources.getString(R.string.issue_event_reviewed);
                setText(0, TypefaceUtils.ICON_EYE);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
                break;
            case TimelineEvent.STATE_CHANGES_REQUESTED:
                message += resources.getString(R.string.issue_event_reviewed);
                setText(0, TypefaceUtils.ICON_X);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_red));
                break;
            case TimelineEvent.STATE_APPROVED:
                message += resources.getString(R.string.issue_event_reviewed);
                setText(0, TypefaceUtils.ICON_CHECK);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_green));
                break;
            case TimelineEvent.STATE_DISMISSED:
                message += resources.getString(R.string.issue_event_reviewed);
                setText(0, TypefaceUtils.ICON_EYE);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
                break;
            default:
                message += resources.getString(R.string.issue_event_reviewed);
                setText(0, TypefaceUtils.ICON_EYE);
                textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
                break;
            }
            break;
        case TimelineEvent.EVENT_REVIEW_DISMISSED:
            message += resources.getString(R.string.issue_event_review_dismissed);
            setText(0, TypefaceUtils.ICON_X);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_MILESTONED:
            message += String.format(resources.getString(R.string.issue_event_milestone_added), "<b>" + event.milestone.title + "</b>");
            setText(0, TypefaceUtils.ICON_MILESTONE);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_DEMILESTONED:
            message += String.format(resources.getString(R.string.issue_event_milestone_removed), "<b>" + event.milestone.title + "</b>");
            setText(0, TypefaceUtils.ICON_MILESTONE);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_CLOSED:
            if (event.commit_id == null) {
                message += resources.getString(R.string.issue_event_closed);
            } else {
                message += String.format(resources.getString(R.string.issue_event_closed_from_commit), "<b>" + event.commit_id.substring(0,7) + "</b>");
            }
            setText(0, TypefaceUtils.ICON_CIRCLE_SLASH);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_red));
            break;
        case TimelineEvent.EVENT_REOPENED:
            message += resources.getString(R.string.issue_event_reopened);
            setText(0, TypefaceUtils.ICON_PRIMITIVE_DOT);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_green));
            break;
        case TimelineEvent.EVENT_RENAMED:
            message += String.format(resources.getString(R.string.issue_event_rename),
                    "<b>" + event.rename.from + "</b>",
                    "<b>" + event.rename.to + "</b>");
            setText(0, TypefaceUtils.ICON_PENCIL);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_MERGED:
            message += String.format(resources.getString(R.string.issue_event_merged), "<b>" + event.commit_id.substring(0,7) + "</b>");
            setText(0, TypefaceUtils.ICON_GIT_MERGE);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_purple));
            break;
        case TimelineEvent.EVENT_COMMITTED:
            setGone(2, true);
            message = String.format("<b>%s</b> ", event.author.name) + event.message;
            setText(0, TypefaceUtils.ICON_GIT_COMMIT);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_normal));
            break;
        case TimelineEvent.EVENT_COMMIT_COMMENTED:
        case TimelineEvent.EVENT_LINE_COMMENTED:
            message += resources.getString(R.string.issue_event_comment_diff);
            setText(0, TypefaceUtils.ICON_CODE);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_light));
            break;
        case TimelineEvent.EVENT_LOCKED:
            message += resources.getString(R.string.issue_event_lock);
            setText(0, TypefaceUtils.ICON_LOCK);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_dark));
            break;
        case TimelineEvent.EVENT_UNLOCKED:
            message += resources.getString(R.string.issue_event_unlock);
            setText(0, TypefaceUtils.ICON_KEY);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_dark));
            break;
        case TimelineEvent.EVENT_HEAD_REF_DELETED:
            message += resources.getString(R.string.issue_event_head_ref_deleted);
            setText(0, TypefaceUtils.ICON_GIT_BRANCH);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_light));
            break;
        case TimelineEvent.EVENT_HEAD_REF_RESTORED:
            message += resources.getString(R.string.issue_event_head_ref_restored);
            setText(0, TypefaceUtils.ICON_GIT_BRANCH);
            textView(0).setTextColor(resources.getColor(R.color.issue_event_light));
            break;
        }

        Date date;
        switch (eventString) {
        case TimelineEvent.EVENT_REVIEWED:
            date = event.submitted_at;
            break;
        default:
            date = event.created_at;
            break;
        }

        if (date != null) {
            message += " " + TimeUtils.getRelativeTime(date);
        }
        setText(1, Html.fromHtml(message));
    }

    private void updateComment(final TimelineEvent comment) {
        imageGetter.bind(textView(0), comment.body_html, comment.id);
        avatars.bind(imageView(4), comment.actor);
        imageView(4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(UserViewActivity.createIntent(comment.actor));
            }
        });

        setText(1, comment.actor == null ? "ghost" : comment.actor.login);
        setText(2, TimeUtils.getRelativeTime(comment.created_at));
        setGone(3, !comment.updated_at.after(comment.created_at));

        boolean canEdit = isCollaborator ||
                (comment.actor != null && comment.actor.login.equals(user));

        if (canEdit) {
            // Edit button
            setGone(5, false);
            view(5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.editComment(comment.getOldModel());
                }
            });
            // Delete button
            setGone(6, false);
            view(6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.deleteComment(comment.getOldModel());
                }
            });
        } else {
            setGone(5, true);
            setGone(6, true);
        }
        ((ReactionsView)view(7)).setReactionSummary(comment.reactions);
    }

    public MultiTypeAdapter setItems(Collection<TimelineEvent> items) {
        if (items == null || items.isEmpty())
            return this;

        this.clear();

        for (TimelineEvent item : items) {
            if (TimelineEvent.EVENT_COMMENTED.equals(item.event)) {
                this.addItem(VIEW_COMMENT, item);
            } else {
                this.addItem(VIEW_EVENT, item);
            }
        }

        notifyDataSetChanged();
        return this;
    }

    @Override
    protected View initialize(int type, View view) {
        view = super.initialize(type, view);

        switch (type) {
        case VIEW_COMMENT:
            textView(view, 0).setMovementMethod(LinkMovementMethod.getInstance());
            TypefaceUtils.setOcticons(textView(view, 5), textView(view, 6));
            setText(view, 5, TypefaceUtils.ICON_PENCIL);
            setText(view, 6, TypefaceUtils.ICON_X);
            break;
        case VIEW_EVENT:
            TypefaceUtils.setOcticons(textView(view, 0));
            break;
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TOTAL;
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == VIEW_COMMENT)
            return R.layout.comment_item;
        else
            return R.layout.comment_event_item;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        TimelineEvent event = (TimelineEvent) getItem(position);

        if (TimelineEvent.EVENT_CLOSED.equals(event.event)) {
            return event.commit_id != null;
        }

        return CLICKABLE_EVENTS.contains(event.event);
    }

    @Override
    protected int[] getChildViewIds(int type) {
        if(type == VIEW_COMMENT)
            return new int[] { R.id.tv_comment_body, R.id.tv_comment_author,
                    R.id.tv_comment_date, R.id.tv_comment_edited, R.id.iv_avatar,
                    R.id.iv_comment_edit, R.id.iv_comment_delete, R.id.rv_comment_reaction };
        else
            return new int[]{R.id.tv_event_icon, R.id.tv_event, R.id.iv_avatar};
    }
}
