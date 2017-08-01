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
package com.github.mobile;

import com.github.mobile.api.service.NotificationService;
import com.github.mobile.api.service.ProjectService;
import com.github.mobile.api.service.SearchService;
import com.github.mobile.api.service.TeamService;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import java.io.IOException;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.ContentsService;
import org.eclipse.egit.github.core.service.DataService;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MarkdownService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.OrganizationService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.StargazerService;
import org.eclipse.egit.github.core.service.UserService;

import retrofit2.Retrofit;

/**
 * Provide GitHub-API related services
 */
public class ServicesModule extends AbstractModule {

    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    NotificationService notificationService(Retrofit retrofit) {
        return retrofit.create(NotificationService.class);
    }

    @Provides
    @Singleton
    SearchService searchService(Retrofit retrofit) {
        return retrofit.create(SearchService.class);
    }

    @Provides
    @Singleton
    TeamService teamService(Retrofit retrofit) {
        return retrofit.create(TeamService.class);
    }

    @Provides
    @Singleton
    ProjectService projectService(Retrofit retrofit) {
        return retrofit.create(ProjectService.class);
    }

    @Provides
    @Singleton
    com.github.mobile.api.service.IssueService issueService(Retrofit retrofit) {
        return retrofit.create(com.github.mobile.api.service.IssueService.class);
    }

    @Provides
    IssueService issueService(GitHubClient client) {
        return new IssueService(client);
    }

    @Provides
    PullRequestService pullRequestService(GitHubClient client) {
        return new PullRequestService(client);
    }

    @Provides
    UserService userService(GitHubClient client) {
        return new UserService(client);
    }

    @Provides
    GistService gistService(GitHubClient client) {
        return new GistService(client);
    }

    @Provides
    OrganizationService orgService(GitHubClient client) {
        return new OrganizationService(client);
    }

    @Provides
    org.eclipse.egit.github.core.service.TeamService teamService(GitHubClient client) {
        return new org.eclipse.egit.github.core.service.TeamService(client);
    }

    @Provides
    RepositoryService repoService(GitHubClient client) {
        return new RepositoryService(client);
    }

    @Provides
    User currentUser(UserService userService) throws IOException {
        return userService.getUser();
    }

    @Provides
    CollaboratorService collaboratorService(GitHubClient client) {
        return new CollaboratorService(client);
    }

    @Provides
    MilestoneService milestoneService(GitHubClient client) {
        return new MilestoneService(client);
    }

    @Provides
    LabelService labelService(GitHubClient client) {
        return new LabelService(client);
    }

    @Provides
    EventService eventService(GitHubClient client) {
        return new EventService(client);
    }

    @Provides
    StargazerService stargazerService(GitHubClient client) {
        return new StargazerService(client);
    }

    @Provides
    CommitService commitService(GitHubClient client) {
        return new CommitService(client);
    }

    @Provides
    @Singleton
    com.github.mobile.api.service.CommitService commitService(Retrofit retrofit) {
        return retrofit.create(com.github.mobile.api.service.CommitService.class);
    }

    @Provides
    DataService dataService(GitHubClient client) {
        return new DataService(client);
    }

    @Provides
    MarkdownService markdownService(GitHubClient client) {
        return new MarkdownService(client);
    }

    @Provides
    ContentsService contentsService(GitHubClient client) {
        return new ContentsService(client);
    }
}
