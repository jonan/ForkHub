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
package com.github.mobile.api.model;

public class CommitComment extends Comment {
    public String path;

    public Integer position;

    public Integer line;

    public String commit_id;

    public CommitComment() {
    }

    public CommitComment(org.eclipse.egit.github.core.CommitComment comment) {
        this.path = comment.getPath();
        this.position = comment.getPosition();
        this.line = comment.getLine();
        this.commit_id = comment.getCommitId();
        super.id = comment.getId();
        super.url = comment.getUrl();
        super.body = comment.getBody();
        super.body_html = comment.getBodyHtml();
        super.body_html = comment.getBodyText();
        super.user = new User(comment.getUser());
        super.created_at = comment.getCreatedAt();
        super.updated_at = comment.getUpdatedAt();
    }
}
