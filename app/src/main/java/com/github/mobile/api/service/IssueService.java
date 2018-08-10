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
package com.github.mobile.api.service;

import com.github.mobile.api.model.Issue;
import com.github.mobile.api.model.TimelineEvent;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IssueService {
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    @GET("repos/{owner}/{repo}/issues/{number}")
    Call<Issue> getIssue(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("number") long number);

    @Headers({"Accept: application/vnd.github.v3.full+json",
            "Accept: application/vnd.github.mockingbird-preview",
            "Accept: application/vnd.github.squirrel-girl-preview"})
    @GET("repos/{owner}/{repo}/issues/{issue_number}/timeline")
    Call<List<TimelineEvent>> getTimeline(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("issue_number") long issue_number,
            @Query("page") int page,
            @Query("per_page") int per_page);
}
