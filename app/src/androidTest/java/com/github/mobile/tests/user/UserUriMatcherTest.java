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
package com.github.mobile.tests.user;

import static com.github.mobile.Intents.EXTRA_POSITION;
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.ui.user.UserViewActivity.TAB_FOLLOWEES;
import static com.github.mobile.ui.user.UserViewActivity.TAB_FOLLOWERS;
import static com.github.mobile.ui.user.UserViewActivity.TAB_MEMBERS;
import static com.github.mobile.ui.user.UserViewActivity.TAB_STARS;
import static com.github.mobile.ui.user.UserViewActivity.TAB_TEAMS;

import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.github.mobile.core.user.UserUriMatcher;

import org.eclipse.egit.github.core.User;

/**
 * Unit tests of {@link UserUriMatcher}
 */
public class UserUriMatcherTest extends AndroidTestCase {

    /**
     * Verify empty URI
     */
    public void testEmptyUri() {
        assertNull(UserUriMatcher.getUserIntent(Uri.parse("").getPathSegments()));
    }

    /**
     * Verify no name
     */
    public void testUriWithNoName() {
        assertNull(UserUriMatcher.getUserIntent(Uri.parse("http://github.com").getPathSegments()));
        assertNull(UserUriMatcher.getUserIntent(Uri.parse("https://github.com").getPathSegments()));
        assertNull(UserUriMatcher.getUserIntent(Uri.parse("http://github.com/").getPathSegments()));
        assertNull(UserUriMatcher.getUserIntent(Uri.parse("http://github.com//").getPathSegments()));
    }

    /**
     * Verify URI with name
     */
    public void testHttpUriWithName() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("http://github.com/defunkt").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("defunkt", user.getLogin());
        assertEquals(-1, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with name
     */
    public void testHttpsUriWithName() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/mojombo").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(-1, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with name
     */
    public void testUriWithTrailingSlash() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("http://github.com/defunkt/").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("defunkt", user.getLogin());
        assertEquals(-1, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with name
     */
    public void testUriWithTrailingSlashes() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("http://github.com/defunkt//").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("defunkt", user.getLogin());
        assertEquals(-1, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with stars
     */
    public void testUriWithStars() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/stars/mojombo").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(TAB_STARS, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with followers
     */
    public void testUriWithFollowers() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/mojombo/followers").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(TAB_FOLLOWERS, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with following
     */
    public void testUriWithFollowing() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/mojombo/following").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(TAB_FOLLOWEES, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI with org
     */
    public void testUriOrg() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/orgs/mojombo").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(-1, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI for org with people
     */
    public void testUriOrgWithPeople() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/orgs/mojombo/people").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(TAB_MEMBERS, intent.getIntExtra(EXTRA_POSITION, -1));
    }

    /**
     * Verify URI for org with teams
     */
    public void testUriOrgWithTeams() {
        Intent intent = UserUriMatcher.getUserIntent(Uri
                .parse("https://github.com/orgs/mojombo/teams").getPathSegments());
        assertNotNull(intent);
        User user = (User) intent.getSerializableExtra(EXTRA_USER);
        assertNotNull(user);
        assertEquals("mojombo", user.getLogin());
        assertEquals(TAB_TEAMS, intent.getIntExtra(EXTRA_POSITION, -1));
    }
}
