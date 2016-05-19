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
package com.github.mobile.api.model;

import com.squareup.moshi.Json;

public class User {
    public static final String TYPE_USER = "User";
    public static final String TYPE_ORGANIZATION = "Organization";

    public int id;

    public String login;

    public String avatar_url;

    public String type;

    @Json(name = "site_admin")
    public boolean is_site_admin;

    public String name;

    public String company;

    public String blog;

    public String location;

    public String email;

    @Json(name = "hireable")
    public boolean is_hireable;

    public String bio;

    public int public_repos;

    public int public_gists;

    public int followers;

    public int following;

    public String created_at;

    public String updated_at;

    public int total_private_repos;

    public int owned_private_repos;

    public int private_gists;

    public int disk_usage;

    public int collaborators;

    public Plan plan;
}
