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
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
public class StarForkHubTask extends AuthenticatedUserTask<Integer> implements DialogInterface.OnClickListener {

    private static final String TAG = "StarForkHubTask";

    private static final String PREF_START_APP_COUNT = "startAppCount";

    private static final int NUMBER_EXECUTIONS_NEEDED_STAR = 5;

    private static final int NUMBER_EXECUTIONS_NEEDED_PLAY_STORE = 15;

    @Inject
    private StargazerService service;

    @Inject
    private SharedPreferences sharedPreferences;

    private int numStarts;

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
    protected Integer run(Account account) throws Exception {
        int numStarts = sharedPreferences.getInt(PREF_START_APP_COUNT, -1);

        if (numStarts > NUMBER_EXECUTIONS_NEEDED_PLAY_STORE) {
            return 0;
        }

        sharedPreferences.edit()
                .putInt(PREF_START_APP_COUNT, numStarts + 1)
                .apply();

        if (numStarts == NUMBER_EXECUTIONS_NEEDED_STAR && !service.isStarring(repository)) {
            return numStarts;
        }

        if (numStarts == NUMBER_EXECUTIONS_NEEDED_PLAY_STORE) {
            return numStarts;
        }

        return 0;
    }

    @Override
    protected void onSuccess(Integer result) {
        numStarts = result;

        switch (result) {
        case NUMBER_EXECUTIONS_NEEDED_STAR:
            // Star dialog
            new AlertDialog.Builder(context, R.style.AlertDialog)
                    .setMessage(context.getResources().getString(R.string.star_forkhub_dialog_text))
                    .setPositiveButton(context.getResources().getString(R.string.star), this)
                    .setNegativeButton(context.getResources().getString(android.R.string.cancel), this)
                    .setCancelable(true)
                    .show();
            break;
        case NUMBER_EXECUTIONS_NEEDED_PLAY_STORE:
            // Rate dialog
            new AlertDialog.Builder(context, R.style.AlertDialog)
                    .setMessage(context.getResources().getString(R.string.rate_forkhub_dialog_text))
                    .setPositiveButton(context.getResources().getString(R.string.rate), this)
                    .setNegativeButton(context.getResources().getString(android.R.string.cancel), this)
                    .setCancelable(true)
                    .show();
            break;
        }
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception checking ForkHub starring status", e);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which != DialogInterface.BUTTON_POSITIVE) {
            return;
        }

        switch (numStarts) {
        case NUMBER_EXECUTIONS_NEEDED_STAR:
            new StarRepositoryTask(context, repository).start();
            break;
        case NUMBER_EXECUTIONS_NEEDED_PLAY_STORE:
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=jp.forkhub")));
            } catch (ActivityNotFoundException e) {
                Log.d(TAG, "PlayStore not installed, using other browser", e);
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=jp.forkhub")));
            }
            break;
        }
    }
}
