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
package com.github.mobile.tests.issue;

import static com.github.mobile.Intents.EXTRA_ISSUE_NUMBERS;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.github.mobile.core.issue.IssueUriMatcher;
import com.github.mobile.ui.UriLauncherActivity;

import org.eclipse.egit.github.core.Repository;

/**
 * Unit tests of {@link IssueUriMatcher}
 */
public class IssueUriMatcherTest extends AndroidTestCase {

    /**
     * Verify empty uri
     */
    public void testEmptyUri() {
        assertNull(IssueUriMatcher.getIssueIntent(Uri.parse("").getPathSegments()));
    }

    /**
     * Verify non-numeric issue number in uri
     */
    public void testNonNumericIssueNumber() {
        assertNull(UriLauncherActivity.getIntentForURI(Uri
                .parse("https://github.com/defunkt/resque/issues/fourty")));
    }

    /**
     * Verify http uri
     */
    public void testHttpUri() {
        Intent intent = UriLauncherActivity.getIntentForURI(Uri
                .parse("https://github.com/defunkt/resque/issues/3"));
        assertNotNull(intent);
        assertEquals(intent.getAction(), "jp.forkhub.mobile.issues.VIEW");
        assertEquals(3, intent.getIntArrayExtra(EXTRA_ISSUE_NUMBERS)[0]);
        Repository repo = (Repository) intent.getSerializableExtra(EXTRA_REPOSITORY);
        assertNotNull(repo);
        assertEquals("resque", repo.getName());
        assertNotNull(repo.getOwner());
        assertEquals("defunkt", repo.getOwner().getLogin());
    }

    /**
     * Verify pull uri
     */
    public void testPullUri() {
        Intent intent = UriLauncherActivity.getIntentForURI(Uri
                .parse("https://github.com/defunkt/resque/pull/3"));
        assertNotNull(intent);
        assertEquals(intent.getAction(), "jp.forkhub.mobile.issues.VIEW");
        assertEquals(3, intent.getIntArrayExtra(EXTRA_ISSUE_NUMBERS)[0]);
        Repository repo = (Repository) intent.getSerializableExtra(EXTRA_REPOSITORY);
        assertNotNull(repo);
        assertEquals("resque", repo.getName());
        assertNotNull(repo.getOwner());
        assertEquals("defunkt", repo.getOwner().getLogin());
    }

    /**
     * Verify https uri
     */
    public void testHttpsUri() {
        Intent intent = UriLauncherActivity.getIntentForURI(Uri
                .parse("http://github.com/defunkt/resque/issues/15"));
        assertNotNull(intent);
        assertEquals(intent.getAction(), "jp.forkhub.mobile.issues.VIEW");
        assertEquals(15, intent.getIntArrayExtra(EXTRA_ISSUE_NUMBERS)[0]);
        Repository repo = (Repository) intent.getSerializableExtra(EXTRA_REPOSITORY);
        assertNotNull(repo);
        assertEquals("resque", repo.getName());
        assertNotNull(repo.getOwner());
        assertEquals("defunkt", repo.getOwner().getLogin());
    }

    /**
     * Verify uri with comment fragment
     */
    public void testCommentUri() {
        Intent intent = UriLauncherActivity.getIntentForURI(Uri
                .parse("https://github.com/defunkt/resque/issues/300#issuecomment-123456"));
        assertNotNull(intent);
        assertEquals(intent.getAction(), "jp.forkhub.mobile.issues.VIEW");
        assertEquals(300, intent.getIntArrayExtra(EXTRA_ISSUE_NUMBERS)[0]);
        Repository repo = (Repository) intent.getSerializableExtra(EXTRA_REPOSITORY);
        assertNotNull(repo);
        assertEquals("resque", repo.getName());
        assertNotNull(repo.getOwner());
        assertEquals("defunkt", repo.getOwner().getLogin());
    }
}
