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

public class Subject {
    public static final String TYPE_ISSUE = "Issue";
    public static final String TYPE_PULL_REQUEST = "PullRequest";
    public static final String TYPE_COMMIT = "Commit";
    public static final String TYPE_RELEASE = "Release";

    public String title;

    public String url;

    public String latest_comment_url;

    public String type;
}
