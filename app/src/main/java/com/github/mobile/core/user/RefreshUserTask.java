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

import com.google.inject.Inject;

import com.github.mobile.accounts.AuthenticatedUserTask;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.UserService;

import android.accounts.Account;
import android.content.Context;
import android.util.Log;

/**
 * Task to refresh a user
 */
public class RefreshUserTask extends AuthenticatedUserTask<User> {

    private static final String TAG = "RefreshUserTask";

    @Inject
    private UserService service;

    private final String login;

    /**
     * Create task for context and login
     *
     * @param context
     * @param login
     */
    public RefreshUserTask(Context context, String login) {
        super(context);

        this.login = login;
    }

    @Override
    protected User run(Account account) throws Exception {
        return service.getUser(login);
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception loading user", e);
    }
}