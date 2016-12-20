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

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;

import com.github.kevinsawicki.wishlist.MultiTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.TimelineEvent;
import com.github.mobile.api.model.User;
import com.github.mobile.ui.issue.IssueFragment;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

import java.util.Collection;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;

/**
 * Adapter for a list of {@link Comment} objects
 */
public class CommentListAdapter extends MultiTypeAdapter {

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
     */
    public CommentListAdapter(Activity activity, AvatarLoader avatars, HttpImageGetter imageGetter) {
        this(activity, avatars, imageGetter, null, false, "");
    }

    /**
     * Create list adapter
     *
     * @param activity
     * @param avatars
     * @param imageGetter
     * @param issueFragment
     */
    public CommentListAdapter(Activity activity, AvatarLoader avatars,
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
        if (type == 0) {
            if (obj instanceof Comment) {
                updateComment((Comment) obj);
            } else {
                updateComment((TimelineEvent) obj);
            }
        } else {
            updateEvent((TimelineEvent) obj);
        }
    }

    protected void updateEvent(final TimelineEvent event) {
        String eventString = event.event;

        User actor;
        if (eventString.equals(TimelineEvent.EVENT_ASSIGNED) || eventString.equals(TimelineEvent.EVENT_UNASSIGNED)) {
            actor = event.assignee;
        } else {
            actor = event.actor;
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

        if (event.created_at != null) {
            message += " " + TimeUtils.getRelativeTime(event.created_at);
        }
        setText(1, Html.fromHtml(message));
    }

    protected void updateComment(final Comment comment) {
        imageGetter.bind(textView(0), comment.getBodyHtml(), comment.getId());
        avatars.bind(imageView(4), comment.getUser());
        imageView(4).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(UserViewActivity.createIntent(comment.getUser()));
            }
        });

        setText(1, comment.getUser() == null ? "ghost" : comment.getUser().getLogin());
        setText(2, TimeUtils.getRelativeTime(comment.getCreatedAt()));
        setGone(3, !comment.getUpdatedAt().after(comment.getCreatedAt()));

        boolean canEdit = isCollaborator ||
                (comment.getUser() != null && comment.getUser().getLogin().equals(user));

        if (issueFragment != null && canEdit) {
            // Edit button
            setGone(5, false);
            view(5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.editComment(comment);
                }
            });
            // Delete button
            setGone(6, false);
            view(6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.deleteComment(comment);
                }
            });
        } else {
            setGone(5, true);
            setGone(6, true);
        }
    }

    protected void updateComment(final TimelineEvent comment) {
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

        if (issueFragment != null && canEdit) {
            // Edit button
            setGone(5, false);
            view(5).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.editComment(comment.getOldCommentModel());
                }
            });
            // Delete button
            setGone(6, false);
            view(6).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    issueFragment.deleteComment(comment.getOldCommentModel());
                }
            });
        } else {
            setGone(5, true);
            setGone(6, true);
        }
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
            if (item instanceof CommitComment) {
                this.addItem(1, item);
            } else if (item instanceof Comment) {
                this.addItem(0, item);
            } else if (item instanceof TimelineEvent) {
                if (TimelineEvent.EVENT_COMMENTED.equals(((TimelineEvent) item).event)) {
                    this.addItem(0, item);
                } else {
                    this.addItem(1, item);
                }
            }
        }

        notifyDataSetChanged();
        return this;
    }

    @Override
    protected View initialize(int type, View view) {
        view = super.initialize(type, view);

        if (type == 0) {
            textView(view, 0).setMovementMethod(LinkMovementMethod.getInstance());
            TypefaceUtils.setOcticons(textView(view, 5), textView(view, 6));
            setText(view, 5, TypefaceUtils.ICON_PENCIL);
            setText(view, 6, TypefaceUtils.ICON_X);
        } else {
            TypefaceUtils.setOcticons(textView(view, 0));
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    protected int getChildLayoutId(int type) {
        if (type == 0)
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
        return false;
    }

    @Override
    protected int[] getChildViewIds(int type) {
        if(type == 0)
            return new int[] { R.id.tv_comment_body, R.id.tv_comment_author,
                    R.id.tv_comment_date, R.id.tv_comment_edited, R.id.iv_avatar,
                    R.id.iv_comment_edit, R.id.iv_comment_delete };
        else
            return new int[]{R.id.tv_event_icon, R.id.tv_event, R.id.iv_avatar};
    }
}
