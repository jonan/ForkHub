package com.github.mobile.api.model;

/*
 * Copyright 2017 Kavalchuk Viktar
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

import com.squareup.moshi.Json;

import org.eclipse.egit.github.core.util.DateUtils;

import java.util.Date;

public class Comment {
    @Json(name = "id")
    private Long id;
    @Json(name = "html_url")
    private String htmlUrl;
    @Json(name = "url")
    private String url;
    @Json(name = "body")
    private String body;
    @Json(name = "body_html")
    private String bodyHtml;
    @Json(name = "body_text")
    private String bodyText;
    @Json(name = "user")
    private User user;
    @Json(name = "created_at")
    private Date createdAt;
    @Json(name = "updated_at")
    private Date updatedAt;
    @Json(name = "reactions")
    private ReactionSummary reactions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getBodyHtml() {
        return bodyHtml;
    }

    public void setBodyHtml(String bodyHtml) {
        this.bodyHtml = bodyHtml;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreatedAt() {
        return DateUtils.clone(this.createdAt);
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = DateUtils.clone(createdAt);
    }

    public Date getUpdatedAt() {
        return DateUtils.clone(this.updatedAt);
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = DateUtils.clone(updatedAt);
    }

    public ReactionSummary getReactions() {
        return reactions;
    }

    public void setReactions(ReactionSummary reactions) {
        this.reactions = reactions;
    }
}
