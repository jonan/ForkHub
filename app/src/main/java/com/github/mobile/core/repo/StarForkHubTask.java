/*
 * Copyright 2016 Jon Ander Peñalba
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

import android.accounts.Account;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.github.mobile.R;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.StargazerService;

/**
 * Task to check repository starring status
 */
public class StarForkHubTask extends AuthenticatedUserTask<Boolean> implements DialogInterface.OnClickListener {

    private static final String TAG = "StarForkHubTask";

    private static final String PREF_START_APP_COUNT = "startAppCount";

    private static final int NUMBER_STARTS_NEEDED = 5;

    @Inject
    private StargazerService service;

    @Inject
    private SharedPreferences sharedPreferences;

    private Repository repository;

    /**
     * Create task for context and id provider
     *
     * @param context
     */
    public StarForkHubTask(Context context) {
        super(context);
        repository = new Repository();
        repository.setName("ForkHub");
        repository.setOwner(new User().setLogin("jonan"));
    }

    @Override
    protected Boolean run(Account account) throws Exception {
        int numStarts = sharedPreferences.getInt(PREF_START_APP_COUNT, -1);
        sharedPreferences.edit()
                .putInt(PREF_START_APP_COUNT, Math.min(numStarts, NUMBER_STARTS_NEEDED) + 1)
                .apply();
        return numStarts == NUMBER_STARTS_NEEDED && !service.isStarring(repository);
    }

    @Override
    protected void onSuccess(Boolean result) {
        if (result) {
            new AlertDialog.Builder(context)
                    .setMessage(context.getResources().getString(R.string.star_forkhub_dialog_text))
                    .setPositiveButton(context.getResources().getString(R.string.star), this)
                    .setNegativeButton(context.getResources().getString(android.R.string.no), this)
                    .setCancelable(true)
                    .show();
        }
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception checking ForkHub starring status", e);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            new StarRepositoryTask(context, repository).start();
        }
    }
}
