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

import com.github.mobile.api.model.CommitComment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

public interface CommitService {
    @Headers("Accept: application/vnd.github.squirrel-girl-preview")
    @GET("repos/{owner}/{repo}/commits/{ref}/comments")
    Call<List<CommitComment>> getCommitComments(
            @Path("owner") String owner,
            @Path("repo") String repo,
            @Path("ref") String number);
}
