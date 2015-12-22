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

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Issue model with comments
 */
public class FullIssue extends ArrayList<Comment> implements Serializable {

    private static final long serialVersionUID = 4586476132467323827L;

    private final Issue issue;

    private Collection<IssueEvent> events;

    /**
     * Create wrapper for issue, comments and events
     *
     * @param issue
     * @param comments
     * @param events
     */
    public FullIssue(final Issue issue, final Collection<Comment> comments, final Collection<IssueEvent> events) {
        super(comments);

        this.events = events;
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
     * @return events
     */
    public Collection<IssueEvent> getEvents() {
        return events;
    }
}
