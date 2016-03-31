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
package com.github.mobile.core.gist;

import static android.app.Activity.RESULT_OK;
import android.accounts.Account;
import android.app.Activity;
import android.util.Log;

import com.github.mobile.R;
import com.github.mobile.ui.ProgressDialogTask;
import com.github.mobile.util.ToastUtils;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.service.GistService;

/**
 * Async task to delete a Gist
 */
public class DeleteGistTask extends ProgressDialogTask<Gist> {

    private static final String TAG = "DeleteGistTask";

    private final String id;

    @Inject
    private GistService service;

    /**
     * Create task
     *
     * @param context
     * @param gistId
     */
    public DeleteGistTask(final Activity context, final String gistId) {
        super(context);

        id = gistId;
    }

    /**
     * Execute the task with a progress dialog displaying.
     * <p>
     * This method must be called from the main thread.
     */
    public void start() {
        showIndeterminate(R.string.deleting_gist);

        execute();
    }

    @Override
    public Gist run(Account account) throws Exception {
        service.deleteGist(id);
        return null;
    }

    @Override
    protected void onSuccess(Gist gist) throws Exception {
        super.onSuccess(gist);

        Activity activity = (Activity) getContext();
        activity.setResult(RESULT_OK);
        activity.finish();
    }

    @Override
    protected void onException(Exception e) throws RuntimeException {
        super.onException(e);

        Log.d(TAG, "Exception deleting Gist", e);
        ToastUtils.show((Activity) getContext(), e.getMessage());
    }
}
