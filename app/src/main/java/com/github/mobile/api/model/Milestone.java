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

import java.io.Serializable;
import java.util.Date;

public class Milestone implements Serializable {
    public long id;

    public int number;

    public String state;

    public String title;

    public String description;

    public User creator;

    public int open_issues;

    public int closed_issues;

    public Date created_at;

    public Date updated_at;

    public Date closed_at;

    public Date due_on;

    private String url;

    public Milestone() {
    }

    public Milestone(org.eclipse.egit.github.core.Milestone milestone) {
        //todo this.id=?
        //there are two variants of id usage
        //1)id is like an id in Issues, but it is in old Issues class too - the remove field
        //2)id is like an serialVersionUID - then generate it using "serialver -classpath . Milestone" in terminal
        this.number = milestone.getNumber();
        this.state = milestone.getState();
        this.title = milestone.getTitle();
        this.description = milestone.getDescription();
        this.creator = new User(milestone.getCreator());
        this.open_issues = milestone.getOpenIssues();
        this.closed_issues = milestone.getClosedIssues();
        this.created_at = milestone.getCreatedAt();
        this.url = milestone.getUrl();
        //todo this.updated_at=???
        this.due_on = milestone.getDueOn();
    }

    public org.eclipse.egit.github.core.Milestone getOldModel() {
        org.eclipse.egit.github.core.Milestone milestone = new org.eclipse.egit.github.core.Milestone();
        milestone.setCreatedAt(created_at);
        milestone.setDueOn(due_on);
        milestone.setClosedIssues(closed_issues);
        milestone.setNumber(number);
        milestone.setOpenIssues(open_issues);
        milestone.setDescription(description);
        milestone.setState(state);
        milestone.setTitle(title);
        milestone.setUrl(url);
        milestone.setCreator(creator.getOldModel());

        return milestone;
    }
}
