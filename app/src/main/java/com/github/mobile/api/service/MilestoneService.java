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
import com.github.mobile.api.model.Milestone;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MilestoneService {
    @GET("repos/{owner}/{repo}/milestones/{number}")
    Call<Milestone> getMilestone(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("number") long number);

    @GET("repos/{owner}/{repo}/milestones")
    Call<List<Milestone>> getMilestones(
            @Path("owner") String owner,
            @Path("repo") String repo);

    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    @GET("repos/{owner}/{repo}/issues")
    Call<List<Issue>> getIssues(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("milestone") long milestone);

    @POST("repos/{owner}/{repo}/milestones")
    Call<Milestone> createMilestone (
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Body Milestone milestone);
    
    @PATCH("repos/{owner}/{repo}/milestones/{number}")
    Call<Milestone> editMilestone (
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("number") long number,
            @Body Milestone milestone);

    @DELETE("repos/{owner}/{repo}/milestones/{number}")
    Call<Milestone> deleteMilestone(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("number") long number);
}
