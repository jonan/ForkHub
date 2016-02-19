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

import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.accounts.LoginActivity;
import com.github.mobile.tests.ActivityTest;

import android.accounts.AccountManager;
import android.view.View;
import android.widget.EditText;

/**
 * Tests of {@link LoginActivity}
 */
public class LoginActivityTest extends ActivityTest<LoginActivity> {

    /**
     * Create test for {@link LoginActivity}
     */
    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    /**
     * Verify authenticator is registered
     */
    public void testHasAuthenticator() {
        assertTrue(AccountUtils.hasAuthenticator(AccountManager
                .get(getActivity())));
    }

    /**
     * Verify activity was created successfully
     *
     * @throws Throwable
     */
    public void testSignInIsDisabled() throws Throwable {
        View loginMenu = view(R.id.m_login);
        assertFalse(loginMenu.isEnabled());
        final EditText login = editText(R.id.et_login);
        final EditText password = editText(R.id.et_password);
        focus(login);
        send("loginname");
        assertEquals("loginname", login.getText().toString());
        assertFalse(loginMenu.isEnabled());
        focus(password);
        send("password");
        assertEquals("password", password.getText().toString());
        assertTrue(loginMenu.isEnabled());
    }
}
