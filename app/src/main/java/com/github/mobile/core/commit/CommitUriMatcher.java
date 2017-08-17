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
package com.github.mobile.core.commit;

import android.content.Intent;
import android.text.TextUtils;

import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.ui.commit.CommitCompareViewActivity;
import com.github.mobile.ui.commit.CommitViewActivity;

import java.util.List;

import org.eclipse.egit.github.core.Repository;

/**
 * Parses commits from a path
 */
public class CommitUriMatcher {

    /**
     * Get an intent for an exact commit match
     *
     * @param pathSegments
     * @return {@link Intent} or null if path is not valid
     */
    public static Intent getCommitIntent(List<String> pathSegments) {
        if (pathSegments.size() < 4)
            return null;

        Repository repo = RepositoryUriMatcher.getRepository(pathSegments);
        if (repo == null)
            return null;

        switch (pathSegments.get(2)) {
            case "commit":
                return getSingleCommitIntent(repo, pathSegments);
            case "pull":
                return getSingleCommitIntent(repo, pathSegments);
            case "compare":
                return getCommitCompareIntent(repo, pathSegments);
            default:
                return null;
        }
    }

    private static Intent getSingleCommitIntent(Repository repo, List<String> pathSegments) {
        String ref = pathSegments.get(3);
        if (TextUtils.isEmpty(ref))
            return null;

        if (TextUtils.isDigitsOnly(ref))
            ref = pathSegments.get(pathSegments.size() - 1);

        return CommitViewActivity.createIntent(repo, ref);
    }

    private static Intent getCommitCompareIntent(Repository repo, List<String> pathSegments) {
        String path = pathSegments.get(3);
        if (TextUtils.isEmpty(path))
            return null;

        String[] refs = path.split("\\.\\.\\.");

        switch (refs.length) {
            case 1:
                return CommitCompareViewActivity.createIntent(repo, refs[0]);
            case 2:
                return CommitCompareViewActivity.createIntent(repo, refs[0], refs[1]);
        }

        return null;
    }
}
