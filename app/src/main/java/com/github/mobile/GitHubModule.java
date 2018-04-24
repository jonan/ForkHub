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

import android.content.Context;

import com.github.mobile.accounts.AccountClient;
import com.github.mobile.accounts.AccountScope;
import com.github.mobile.accounts.GitHubAccount;
import com.github.mobile.api.DateAdapter;
import com.github.mobile.api.RequestConfiguration;
import com.github.mobile.api.model.Milestone;
import com.github.mobile.core.commit.CommitStore;
import com.github.mobile.core.gist.GistStore;
import com.github.mobile.core.issue.IssueStore;
import com.github.mobile.persistence.OrganizationRepositories;
import com.github.mobile.sync.SyncCampaign;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.google.inject.name.Named;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.lang.ref.WeakReference;

import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

/**
 * Main module provide services and clients
 */
public class GitHubModule extends AbstractModule {

    private WeakReference<IssueStore> issues;

    private WeakReference<GistStore> gists;

    private WeakReference<CommitStore> commits;

    @Override
    protected void configure() {
        install(new ServicesModule());
        install(new FactoryModuleBuilder().build(SyncCampaign.Factory.class));
        install(new FactoryModuleBuilder()
                .build(OrganizationRepositories.Factory.class));
        install(AccountScope.module());
    }

    @Provides
    GitHubClient client(Provider<GitHubAccount> accountProvider) {
        return new AccountClient(accountProvider);
    }

    @Provides
    @Singleton
    Retrofit retrofit(Provider<GitHubAccount> accountProvider) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new RequestConfiguration(accountProvider))
                .build();

        JsonAdapter<Milestone> adapter =
                new Moshi.Builder().add(new DateAdapter()).build().adapter(Milestone.class).serializeNulls();
        Moshi converter = new Moshi.Builder()
                .add(new DateAdapter())
                .add(Milestone.class, adapter)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(converter))
                .build();
    }

    @Provides
    @Named("cacheDir")
    File cacheDir(Context context) {
        return new File(context.getFilesDir(), "cache");
    }

    @Provides
    IssueStore issueStore(IssueService issueService,
            PullRequestService pullService) {
        IssueStore store = issues != null ? issues.get() : null;
        if (store == null) {
            store = new IssueStore(issueService, pullService);
            issues = new WeakReference<IssueStore>(store);
        }
        return store;
    }

    @Provides
    GistStore gistStore(GistService service) {
        GistStore store = gists != null ? gists.get() : null;
        if (store == null) {
            store = new GistStore(service);
            gists = new WeakReference<GistStore>(store);
        }
        return store;
    }

    @Provides
    CommitStore commitStore(CommitService service) {
        CommitStore store = commits != null ? commits.get() : null;
        if (store == null) {
            store = new CommitStore(service);
            commits = new WeakReference<CommitStore>(store);
        }
        return store;
    }
}
