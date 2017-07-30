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

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {
    public static final String TYPE_USER = "User";
    public static final String TYPE_ORGANIZATION = "Organization";

    public long id;

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

    public Date created_at;

    public Date updated_at;

    public int total_private_repos;

    public int owned_private_repos;

    public int private_gists;

    public int disk_usage;

    public int collaborators;

    public User() {
    }

    public User(org.eclipse.egit.github.core.User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.avatar_url = user.getAvatarUrl();
        this.type = user.getType();
        this.name = user.getName();
        this.company = user.getCompany();
        this.blog = user.getBlog();
        this.location = user.getLocation();
        this.email = user.getEmail();
        this.is_hireable = user.isHireable();
        this.bio = user.getBio();
        this.public_repos = user.getPublicRepos();
        this.public_gists = user.getPublicGists();
        this.followers = user.getFollowers();
        this.following = user.getFollowing();
        this.created_at = user.getCreatedAt();
        this.total_private_repos = user.getTotalPrivateRepos();
        this.owned_private_repos = user.getOwnedPrivateRepos();
        this.private_gists = user.getPrivateGists();
        this.disk_usage = user.getDiskUsage();
        this.collaborators = user.getCollaborators();
    }

    public org.eclipse.egit.github.core.User getOldModel() {
        org.eclipse.egit.github.core.User user = new org.eclipse.egit.github.core.User();
        user.setId((int) id);
        user.setLogin(login);
        user.setAvatarUrl(avatar_url);
        user.setType(type);
        user.setName(name);
        user.setCompany(company);
        user.setBlog(blog);
        user.setLocation(location);
        user.setEmail(email);
        user.setHireable(is_hireable);
        user.setBio(bio);
        user.setPublicRepos(public_repos);
        user.setPublicGists(public_gists);
        user.setFollowers(followers);
        user.setFollowing(following);
        user.setCreatedAt(created_at);
        user.setTotalPrivateRepos(total_private_repos);
        user.setOwnedPrivateRepos(owned_private_repos);
        user.setPrivateGists(private_gists);
        user.setDiskUsage(disk_usage);
        user.setCollaborators(collaborators);
        return user;
    }
}
