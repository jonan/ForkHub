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

import com.github.mobile.api.model.Project;
import com.github.mobile.api.model.ProjectCard;
import com.github.mobile.api.model.ProjectColumn;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProjectService {
    @Headers("Accept: application/vnd.github.inertia-preview+json")
    @GET("repos/{owner}/{repo}/projects")
    Call<List<Project>> getProjects(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Query("page") int page);

    @Headers("Accept: application/vnd.github.inertia-preview+json")
    @GET("projects/{id}/columns")
    Call<List<ProjectColumn>> getColumns(
            @Path("id") long id,
            @Query("page") int page);

    @Headers("Accept: application/vnd.github.inertia-preview+json")
    @GET("projects/columns/{id}/cards")
    Call<List<ProjectCard>> getCards(
            @Path("id") long id,
            @Query("page") int page,
            @Query("per_page") int perPage);
}
