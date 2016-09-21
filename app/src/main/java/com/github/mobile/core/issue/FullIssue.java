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
package com.github.mobile.core.issue;

import com.github.mobile.api.model.TimelineEvent;
import com.github.mobile.api.model.ReactionSummary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;

/**
 * Issue model with comments
 */
public class FullIssue extends ArrayList<Comment> implements Serializable {

    private static final long serialVersionUID = 4586476132467323827L;

    private final Issue issue;

    private ReactionSummary reactions;

    private Collection<TimelineEvent> events;

    /**
     * Create wrapper for issue, reactions, comments and events
     *
     * @param issue
     * @param reactions
     * @param comments
     * @param events
     */
    public FullIssue(final Issue issue, final ReactionSummary reactions,
            final Collection<Comment> comments, final Collection<TimelineEvent> events) {
        super(comments);

        this.events = events;
        this.reactions = reactions;
        this.issue = issue;
    }

    /**
     * Create empty wrapper
     */
    public FullIssue() {
        this.issue = null;
    }

    /**
     * @return issue
     */
    public Issue getIssue() {
        return issue;
    }

    /**
     * @return reactions
     */
    public ReactionSummary getReactions() {
        return reactions;
    }

    /**
     * @return events
     */
    public Collection<TimelineEvent> getEvents() {
        return events;
    }
}
