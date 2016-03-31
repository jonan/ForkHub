/*
 * Copyright 2012 GitHub Inc.
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
package com.github.mobile.core.issue;

import android.content.Intent;
import android.text.TextUtils;

import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.ui.issue.IssuesViewActivity;

import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryIssue;

/**
 * Parses a {@link RepositoryIssue} from a path
 */
public class IssueUriMatcher {

    /**
     * Get an intent for an exact {@link RepositoryIssue} match
     *
     * @param pathSegments
     * @return {@link Intent} or null if path is not valid
     */
    public static Intent getIssueIntent(List<String> pathSegments) {
        if (pathSegments.size() != 4)
            return null;

        Repository repo = RepositoryUriMatcher.getRepository(pathSegments);
        if (repo == null)
            return null;

        if (!"issues".equals(pathSegments.get(2)) && !"pull".equals(pathSegments.get(2)))
            return null;

        String number = pathSegments.get(3);
        if (TextUtils.isEmpty(number))
            return null;

        int issueNumber;
        try {
            issueNumber = Integer.parseInt(number);
        } catch (NumberFormatException nfe) {
            return null;
        }
        if (issueNumber < 1)
            return null;

        RepositoryIssue issue = new RepositoryIssue();
        issue.setRepository(repo);
        issue.setNumber(issueNumber);

        return IssuesViewActivity.createIntent(issue, repo);
    }
}
