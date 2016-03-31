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
package com.github.mobile.core.user;

import android.content.Intent;

import com.github.mobile.core.repo.RepositoryUtils;
import com.github.mobile.ui.user.UserViewActivity;

import java.util.List;

import org.eclipse.egit.github.core.User;

/**
 * Parses a {@link User} from a path
 */
public class UserUriMatcher {

    /**
     * Get the user for the given path
     *
     * @param pathSegments
     * @return {@link User} or null if path is not valid
     */
    public static User getUser(List<String> pathSegments) {
        if (pathSegments.size() < 1)
            return null;

        String login = pathSegments.get(0);
        if (!RepositoryUtils.isValidOwner(login))
            return null;

        return new User().setLogin(login);
    }

    /**
     * Get an intent for an exact {@link User} match
     *
     * @param pathSegments
     * @return {@link Intent} or null if path is not valid
     */
    public static Intent getUserIntent(List<String> pathSegments) {
        if (pathSegments.size() != 1)
            return null;

        User user = getUser(pathSegments);
        if (user == null)
            return null;

        return UserViewActivity.createIntent(user);
    }
}
