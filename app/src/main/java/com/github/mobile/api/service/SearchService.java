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
import com.github.mobile.api.model.Repository;
import com.github.mobile.api.model.SearchResult;
import com.github.mobile.api.model.User;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SearchService {
    @GET("search/users")
    Call<SearchResult<User>> searchUsers(
            @Query("q") String q,
            @Query("page") int page);

    @GET("search/repositories")
    Call<SearchResult<Repository>> searchRepositories(
            @Query("q") String q,
            @Query("page") int page);

    @GET("search/issues")
    Call<SearchResult<Issue>> searchIssues(
            @Query("q") String q,
            @Query("page") int page);
}
