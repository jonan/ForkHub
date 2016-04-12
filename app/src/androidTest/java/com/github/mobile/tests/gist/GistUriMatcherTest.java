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
package com.github.mobile.tests.gist;

import android.content.Intent;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.github.mobile.core.gist.GistUriMatcher;

import org.eclipse.egit.github.core.Gist;

import static com.github.mobile.Intents.EXTRA_GIST;

/**
 * Unit tests of {@link GistUriMatcher}
 */
public class GistUriMatcherTest extends AndroidTestCase {

    /**
     * Verify empty uri
     */
    public void testEmptyUri() {
        assertNull(GistUriMatcher.getGistIntent(Uri.parse("").getPathSegments()));
    }

    /**
     * Verify invalid Gist ids in URIs
     */
    public void testNonGistId() {
        assertNull(GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/TEST").getPathSegments()));
        assertNull(GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/abc%20").getPathSegments()));
        assertNull(GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/abcdefg").getPathSegments()));
    }

    /**
     * Verify public Gist id
     */
    public void testPublicGist() {
        Intent intent = GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/1234").getPathSegments());
        assertNotNull(intent);
        Gist gist = (Gist) intent.getSerializableExtra(EXTRA_GIST);
        assertEquals("1234", gist.getId());
    }

    /**
     * Verify public Gist id
     */
    public void testPrivateGist() {
        Intent intent = GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/abcd1234abcd1234abcd").getPathSegments());
        assertNotNull(intent);
        Gist gist = (Gist) intent.getSerializableExtra(EXTRA_GIST);
        assertEquals("abcd1234abcd1234abcd", gist.getId());
    }

    /**
     * Verify public Gist id with user
     */
    public void testPrivateGistWithUser() {
        Intent intent = GistUriMatcher.getGistIntent(Uri
                .parse("https://gist.github.com/user/abcd1234abcd1234abcd").getPathSegments());
        assertNotNull(intent);
        Gist gist = (Gist) intent.getSerializableExtra(EXTRA_GIST);
        assertEquals("abcd1234abcd1234abcd", gist.getId());
    }
}
