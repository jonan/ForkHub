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

import com.github.mobile.core.ItemStore;
import com.github.mobile.util.HtmlUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;

/**
 * Store of loaded issues
 */
public class IssueStore extends ItemStore {

    private final Map<String, ItemReferences<RepositoryIssue>> repos = new HashMap<String, ItemReferences<RepositoryIssue>>();

    private final IssueService issueService;

    private final PullRequestService pullService;

    /**
     * Create issue store
     *
     * @param issueService
     * @param pullService
     */
    public IssueStore(final IssueService issueService,
            final PullRequestService pullService) {
        this.issueService = issueService;
        this.pullService = pullService;
    }

    /**
     * Get issue
     *
     * @param repository
     * @param number
     * @return issue or null if not in store
     */
    public RepositoryIssue getIssue(IRepositoryIdProvider repository, int number) {
        ItemReferences<RepositoryIssue> repoIssues = repos.get(repository.generateId());
        return repoIssues != null ? repoIssues.get(number) : null;
    }

    /**
     * Add issue to store
     *
     * @param issue
     * @return issue
     */
    public RepositoryIssue addIssue(Issue issue) {
        IRepositoryIdProvider repo = null;
        if (issue instanceof RepositoryIssue)
            repo = ((RepositoryIssue) issue).getRepository();
        if (repo == null)
            repo = RepositoryId.createFromUrl(issue.getHtmlUrl());
        return addIssue(repo, issue);
    }

    /**
     * Add issue to store
     *
     * @param repository
     * @param issue
     * @return issue
     */
    public RepositoryIssue addIssue(IRepositoryIdProvider repository, Issue issue) {
        issue.setBodyHtml(HtmlUtils.format(issue.getBodyHtml()).toString());
        RepositoryIssue current = getIssue(repository, issue.getNumber());
        if (current != null) {
            return copyIssue(current, issue);
        } else {
            String repoId = repository.generateId();
            ItemReferences<RepositoryIssue> repoIssues = repos.get(repoId);
            if (repoIssues == null) {
                repoIssues = new ItemReferences<RepositoryIssue>();
                repos.put(repoId, repoIssues);
            }
            RepositoryIssue repoIssue = createRepositoryIssue(issue);
            repoIssues.put(issue.getNumber(), repoIssue);
            return repoIssue;
        }
    }

    /**
     * Refresh issue
     *
     * @param repository
     * @param number
     * @return refreshed issue
     * @throws IOException
     */
    public RepositoryIssue refreshIssue(IRepositoryIdProvider repository, int number)
            throws IOException {
        Issue issue;
        try {
            issue = issueService.getIssue(repository, number);
            if (IssueUtils.isPullRequest(issue))
                issue.setPullRequest(pullService.getPullRequest(repository, number));
        } catch (IOException e) {
            if (e instanceof RequestException && 410 == ((RequestException) e).getStatus())
                try {
                    issue = IssueUtils.toIssue(pullService.getPullRequest(repository, number));
                } catch (IOException e2) {
                    throw e;
                }
            else
                throw e;
        }
        return addIssue(repository, issue);
    }

    /**
     * Edit issue
     *
     * @param repository
     * @param issue
     * @return edited issue
     * @throws IOException
     */
    public RepositoryIssue editIssue(IRepositoryIdProvider repository, Issue issue)
            throws IOException {
        return addIssue(repository, issueService.editIssue(repository, issue));
    }

    private RepositoryIssue createRepositoryIssue(Issue issue) {
        if (issue instanceof RepositoryIssue)
            return (RepositoryIssue) issue;

        return copyIssue(new RepositoryIssue(), issue);
    }

    private RepositoryIssue copyIssue(RepositoryIssue to, Issue from) {
        to.setId(from.getId());
        to.setUser(from.getUser());
        to.setAssignee(from.getAssignee());
        to.setBody(from.getBody());
        to.setBodyHtml(from.getBodyHtml());
        to.setBodyText(from.getBodyText());
        to.setHtmlUrl(from.getHtmlUrl());
        to.setClosedBy(from.getClosedBy());
        to.setClosedAt(from.getClosedAt());
        to.setCreatedAt(from.getCreatedAt());
        to.setUpdatedAt(from.getUpdatedAt());
        to.setComments(from.getComments());
        to.setNumber(from.getNumber());
        to.setLabels(from.getLabels());
        to.setMilestone(from.getMilestone());
        to.setPullRequest(from.getPullRequest());
        to.setState(from.getState());
        to.setTitle(from.getTitle());
        to.setUrl(from.getUrl());

        if (from instanceof RepositoryIssue)
            to.setRepository(((RepositoryIssue) from).getRepository());

        return to;
    }
}
