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
package com.github.mobile.core.repo;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;

import android.net.Uri;

import java.util.List;

/**
 * Parses a {@link Repository} from a {@link Uri}
 */
public class RepositoryUriMatcher {

    /**
     * Attempt to parse a {@link Repository} from the given {@link Uri}
     *
     * @param uri
     * @return {@link Repository} or null if unparseable
     */
    public static Repository getRepository(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null)
            return null;
        if (segments.size() < 2)
            return null;

        String repoOwner = segments.get(0);
        if (!RepositoryUtils.isValidOwner(repoOwner))
            return null;

        String repoName = segments.get(1);
        if (!RepositoryUtils.isValidRepo(repoName))
            return null;

        Repository repository = new Repository();
        repository.setName(repoName);
        repository.setOwner(new User().setLogin(repoOwner));
        return repository;
    }

    /**
     * Attempt to parse a {@link Repository} with issues or
     * pull requests from the given {@link Uri}
     *
     * @param uri
     * @return {@link Repository} or null if unparseable
     */
    public static Repository getRepositoryIssues(Uri uri) {
        List<String> segments = uri.getPathSegments();
        if (segments == null)
            return null;
        if (segments.size() < 3)
            return null;
        if (!"issues".equals(segments.get(2)) && !"pulls".equals(segments.get(2)))
            return null;

        return RepositoryUriMatcher.getRepository(uri);
    }
}
