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

public class Notification {
    public static final String REASON_SUBSCRIBED = "subscribed";
    public static final String REASON_MANUAL = "manual";
    public static final String REASON_AUTHOR = "author";
    public static final String REASON_COMMENT = "comment";
    public static final String REASON_MENTION = "mention";
    public static final String REASON_TEAM_MENTION = "team_mention";
    public static final String REASON_STATE_CHANGE = "state_change";
    public static final String REASON_ASSIGN = "assign";

    public int id;

    public Repository repository;

    public Subject subject;

    public String reason;

    @Json(name = "unread")
    public boolean is_unread;

    public Date updated_at;

    public Date last_read_at;
}
