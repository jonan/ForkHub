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

import java.util.Date;

public class Repository {
    public long id;

    public User owner;

    public String name;

    public String full_name;

    public String description;

    @Json(name = "private")
    public boolean is_private;

    @Json(name = "fork")
    public boolean is_fork;

    public Date created_at;

    public Date updated_at;

    public Date pushed_at;

    public String homepage;

    public int size;

    public int stargazers_count;

    public int watchers_count;

    public String language;

    public boolean has_issues;

    public boolean has_downloads;

    public boolean has_wiki;

    public boolean has_pages;

    public int forks_count;

    public String mirror_url;

    public int open_issues_count;

    public int forks;

    public int open_issues;

    public int watchers;

    public String default_branch;

    public int network_count;

    public int subscribers_count;

    public Permissions permissions;

    public User organization;

    public Repository parent;

    public Repository source;
}
