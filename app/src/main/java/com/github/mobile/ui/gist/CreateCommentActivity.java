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
package com.github.mobile.ui.gist;

import static com.github.mobile.Intents.EXTRA_GIST;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.core.gist.CreateCommentTask;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.User;

/**
 * Activity to create a comment on a {@link Gist}
 */
public class CreateCommentActivity extends
        com.github.mobile.ui.comment.CreateCommentActivity {

    /**
     * Create intent to create a comment
     *
     * @param gist
     * @return intent
     */
    public static Intent createIntent(Gist gist) {
        Builder builder = new Builder("gist.comment.create.VIEW");
        builder.gist(gist);
        return builder.toIntent();
    }

    private Gist gist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gist = getSerializableExtra(EXTRA_GIST);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.gist_title) + gist.getId());
        User owner = gist.getOwner();
        if (owner != null)
            actionBar.setSubtitle(owner.getLogin());
        avatars.bind(actionBar, owner);
    }

    @Override
    protected void createComment(String comment) {
        new CreateCommentTask(this, gist.getId(), comment) {

            @Override
            protected void onSuccess(Comment comment) throws Exception {
                super.onSuccess(comment);

                finish(comment);
            }

        }.start();
    }
}
