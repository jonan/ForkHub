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

import com.github.mobile.R;
import com.github.mobile.tests.ActivityTest;
import com.github.mobile.ui.gist.CreateGistActivity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import static android.content.Intent.EXTRA_TEXT;

/**
 * Tests of {@link CreateGistActivity}
 */
public class CreateGistActivityTest extends ActivityTest<CreateGistActivity> {

    /**
     * Create test
     */
    public CreateGistActivityTest() {
        super(CreateGistActivity.class);
    }

    /**
     * Create Gist with initial text
     */
    public void testCreateWithInitialText() {
        setActivityIntent(new Intent().putExtra(EXTRA_TEXT, "gist content"));

        View createMenu = view(R.id.m_apply);
        assertTrue(createMenu.isEnabled());
        EditText content = editText(R.id.et_gist_content);
        assertEquals("gist content", content.getText().toString());
    }

    /**
     * Create Gist with no initial text
     *
     * @throws Throwable
     */
    public void testCreateWithNoInitialText() throws Throwable {
        View createMenu = view(R.id.m_apply);
        assertFalse(createMenu.isEnabled());
        EditText content = editText(R.id.et_gist_content);
        focus(content);
        send("gist content");
        assertTrue(createMenu.isEnabled());
    }
}
