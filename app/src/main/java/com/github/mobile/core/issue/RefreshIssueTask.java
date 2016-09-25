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

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.api.model.TimelineEvent;
import com.github.mobile.api.service.PaginationService;
import com.github.mobile.api.model.ReactionSummary;
import com.github.mobile.util.HtmlUtils;
import com.github.mobile.util.HttpImageGetter;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;

/**
 * Task to load and store an {@link Issue}
 */
public class RefreshIssueTask extends AuthenticatedUserTask<FullIssue> {

    private static final String TAG = "RefreshIssueTask";

    @Inject
    private IssueService issueService;

    @Inject
    private PullRequestService pullService;

    @Inject
    private IssueStore store;

    @Inject
    private com.github.mobile.api.service.IssueService newIssueService;

    private final IRepositoryIdProvider repositoryId;

    private final int issueNumber;

    private final HttpImageGetter bodyImageGetter;

    private final HttpImageGetter commentImageGetter;

    /**
     * Create task to refresh given issue
     *
     * @param context
     * @param repositoryId
     * @param issueNumber
     * @param bodyImageGetter
     * @param commentImageGetter
     */
    public RefreshIssueTask(Context context,
            IRepositoryIdProvider repositoryId, int issueNumber,
            HttpImageGetter bodyImageGetter, HttpImageGetter commentImageGetter) {
        super(context);

        this.repositoryId = repositoryId;
        this.issueNumber = issueNumber;
        this.bodyImageGetter = bodyImageGetter;
        this.commentImageGetter = commentImageGetter;
    }

    @Override
    public FullIssue run(Account account) throws Exception {
        Issue issue = store.refreshIssue(repositoryId, issueNumber);
        bodyImageGetter.encode(issue.getId(), issue.getBodyHtml());
        List<Comment> comments;
        if (issue.getComments() > 0)
            comments = issueService.getComments(repositoryId, issueNumber);
        else
            comments = Collections.emptyList();

        List<CommitComment> reviews;
        if (IssueUtils.isPullRequest(issue))
            reviews = pullService.getComments(repositoryId, issueNumber);
        else
            reviews = Collections.emptyList();

        for (Comment comment : comments) {
            String formatted = HtmlUtils.format(comment.getBodyHtml())
                    .toString();
            comment.setBodyHtml(formatted);
            commentImageGetter.encode(comment.getId(), formatted);
        }

        final String[] repo = repositoryId.generateId().split("/");
        PaginationService<TimelineEvent> paginationService = new PaginationService<TimelineEvent>(1, PaginationService.ITEMS_PER_PAGE_MAX) {
            @Override
            public Collection<TimelineEvent> getSinglePage(int page, int itemsPerPage) throws IOException {
                return newIssueService.getTimeline(repo[0], repo[1], issueNumber, page, itemsPerPage).execute().body();
            }
        };
        Collection<TimelineEvent> timelineEvents = paginationService.getAll();

        ReactionSummary reactions = new ReactionSummary();
        try {
            reactions = newIssueService.getIssue(repo[0], repo[1], issueNumber).execute().body().reactions;
        } catch (Exception e) {
            // Reactions are in a preview state, API can change, so make sure we don't crash if it does.
        }

        return new FullIssue(issue, reactions, sortAllComments(comments, reviews), timelineEvents);
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception loading issue", e);
    }

    private List<Comment> sortAllComments(List<Comment> comments, List<CommitComment> reviews) {
        List<Comment> allComments = new ArrayList<>(comments.size() + reviews.size());

        int numReviews = reviews.size();

        int start = 0;
        for (Comment comment : comments) {
            for (int i = start; i < numReviews; i++) {
                CommitComment review = reviews.get(i);
                if (comment.getCreatedAt().after(review.getCreatedAt())) {
                    allComments.add(review);
                    start++;
                } else {
                    i = numReviews;
                }
            }
            allComments.add(comment);
        }

        // Add the remaining reviews
        for (int i = start; i < numReviews; i++) {
            allComments.add(reviews.get(i));
        }

        return allComments;
    }
}
