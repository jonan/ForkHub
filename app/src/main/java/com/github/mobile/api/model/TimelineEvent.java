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
package com.github.mobile.api.model;

import org.eclipse.egit.github.core.Comment;

import java.util.Date;
import java.util.List;

public class TimelineEvent {
    public static final String EVENT_ADDED_TO_PROJECT = "added_to_project";
    public static final String EVENT_ASSIGNED = "assigned";
    public static final String EVENT_CLOSED = "closed";
    public static final String EVENT_COMMENTED = "commented";
    public static final String EVENT_COMMITTED = "committed";
    public static final String EVENT_COMMIT_COMMENTED = "commit-commented";
    public static final String EVENT_CROSS_REFERENCED = "cross-referenced";
    public static final String EVENT_DEMILESTONED = "demilestoned";
    public static final String EVENT_HEAD_REF_DELETED = "head_ref_deleted";
    public static final String EVENT_HEAD_REF_RESTORED = "head_ref_restored";
    public static final String EVENT_LABELED = "labeled";
    public static final String EVENT_LINE_COMMENTED = "line-commented";
    public static final String EVENT_LOCKED = "locked";
    public static final String EVENT_MARKED_AS_DUPLICATE = "marked_as_duplicate";
    public static final String EVENT_MENTIONED = "mentioned";
    public static final String EVENT_MERGED = "merged";
    public static final String EVENT_MILESTONED = "milestoned";
    public static final String EVENT_MOVED_COLUMNS_IN_PROJECT = "moved_columns_in_project";
    public static final String EVENT_REFERENCED = "referenced";
    public static final String EVENT_RENAMED = "renamed";
    public static final String EVENT_REMOVED_FROM_PROJECT = "removed_from_project";
    public static final String EVENT_REOPENED = "reopened";
    public static final String EVENT_REVIEW_DISMISSED = "review_dismissed";
    public static final String EVENT_REVIEW_REQUESTED = "review_requested";
    public static final String EVENT_REVIEW_REQUEST_REMOVED = "review_request_removed";
    public static final String EVENT_REVIEWED = "reviewed";
    public static final String EVENT_SUBSCRIBED = "subscribed";
    public static final String EVENT_UNASSIGNED = "unassigned";
    public static final String EVENT_UNLABELED = "unlabeled";
    public static final String EVENT_UNLOCKED = "unlocked";
    public static final String EVENT_UNSUBSCRIBED = "unsubscribed";

    public static final String STATE_PENDING = "pending";
    public static final String STATE_COMMENTED = "commented";
    public static final String STATE_CHANGES_REQUESTED = "changes_requested";
    public static final String STATE_APPROVED = "approved";
    public static final String STATE_DISMISSED = "dismissed";

    public long id;

    public User user;

    public User actor;

    public CommitAuthor author;

    public CommitAuthor committer;

    public List<LineComment> comments;

    public ReferenceSource source;

    public User review_requester;

    public User requested_reviewer;

    public String commit_id;

    public String sha;

    public String message;

    public String event;

    public Date created_at;

    public Date updated_at;

    public Date submitted_at;

    public String state;

    public String body;

    public String body_html;

    public Label label;

    public User assignee;

    public Milestone milestone;

    public Rename rename;

    public ReactionSummary reactions;

    public Comment getOldModel() {
        Comment comment = new Comment();
        comment.setCreatedAt(created_at);
        comment.setUpdatedAt(updated_at);
        comment.setBody(body);
        comment.setBodyHtml(body_html);
        comment.setId(id);
        if (actor != null) {
            comment.setUser(actor.getOldModel());
        }
        return comment;
    }
}
