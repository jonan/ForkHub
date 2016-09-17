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
package com.github.mobile.tests.commit;

import static com.github.mobile.Intents.EXTRA_BASES;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.github.mobile.core.commit.CommitUriMatcher;

import org.eclipse.egit.github.core.Repository;

/**
 * Tests of {@link CommitUriMatcher}
 */
public class CommitUriMatcherTest extends AndroidTestCase {

    /**
     * Verity empty uri
     */
    public void testEmptyUri() {
        assertNull(CommitUriMatcher.getCommitIntent(Uri.parse("").getPathSegments()));
    }

    /**
     * Verify http uri
     */
    public void testHttpUri() {
        Intent intent = CommitUriMatcher.getCommitIntent(Uri
                .parse("https://github.com/defunkt/resque/commit/abcd").getPathSegments());
        assertNotNull(intent);
        assertEquals("abcd", intent.getCharSequenceArrayExtra(EXTRA_BASES)[0].toString());
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
        Intent intent = CommitUriMatcher.getCommitIntent(Uri
                .parse("https://github.com/defunkt/resque/commit/1234").getPathSegments());
        assertNotNull(intent);
        assertEquals("1234", intent.getCharSequenceArrayExtra(EXTRA_BASES)[0].toString());
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
        Intent intent = CommitUriMatcher.getCommitIntent(Uri
                .parse("https://github.com/defunkt/resque/commit/a1b2#commitcomment-1605701").getPathSegments());
        assertNotNull(intent);
        assertEquals("a1b2", intent.getCharSequenceArrayExtra(EXTRA_BASES)[0].toString());
        Repository repo = (Repository) intent.getSerializableExtra(EXTRA_REPOSITORY);
        assertNotNull(repo);
        assertEquals("resque", repo.getName());
        assertNotNull(repo.getOwner());
        assertEquals("defunkt", repo.getOwner().getLogin());
    }
}
