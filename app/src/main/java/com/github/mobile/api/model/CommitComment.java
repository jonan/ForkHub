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

public class CommitComment extends Comment{
    @Json(name = "path")
    private String path;
    @Json(name = "position")
    private Integer position;
    @Json(name = "line")
    private Integer line;
    @Json(name = "commit_id")
    private String commitId;

    public CommitComment(org.eclipse.egit.github.core.CommitComment comment) {
        this.path = comment.getPath();
        this.position = comment.getPosition();
        this.line = comment.getLine();
        this.commitId = comment.getCommitId();
        super.setId(comment.getId());
        super.setUrl(comment.getUrl());
        super.setBody(comment.getBody());
        super.setBodyHtml(comment.getBodyHtml());
        super.setBodyText(comment.getBodyText());
        super.setUser(new User(comment.getUser()));
        super.setCreatedAt(comment.getCreatedAt());
        super.setUpdatedAt(comment.getUpdatedAt());
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }
}