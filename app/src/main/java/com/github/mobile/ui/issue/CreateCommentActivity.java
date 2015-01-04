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
package com.github.mobile.ui.issue;

import static com.github.mobile.Intents.EXTRA_COMMENT;
import static com.github.mobile.Intents.EXTRA_ISSUE_NUMBER;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;
import static com.github.mobile.Intents.EXTRA_USER;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.github.mobile.Intents.Builder;
import com.github.mobile.R;
import com.github.mobile.ui.comment.CommentPreviewPagerAdapter;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;

/**
 * Activity to create a comment on an {@link Issue}
 */
public class CreateCommentActivity extends
        com.github.mobile.ui.comment.CreateCommentActivity {

    /**
     * Create intent to create a comment
     *
     * @param repoId
     * @param issueNumber
     * @param user
     * @return intent
     */
    public static Intent createIntent(RepositoryId repoId, int issueNumber, User user) {
        return createIntent(repoId, issueNumber, user, null);
    }

    /**
     * Create intent to create a comment
     *
     * @param repoId
     * @param issueNumber
     * @param user
     * @return intent
     */
    public static Intent createIntent(RepositoryId repoId, int issueNumber, User user, Comment comment) {
        Builder builder = new Builder("issue.comment.create.VIEW");
        builder.repo(repoId);
        builder.add(EXTRA_ISSUE_NUMBER, issueNumber);
        builder.add(EXTRA_USER, user);
        builder.add(EXTRA_COMMENT, comment);
        return builder.toIntent();
    }

    private RepositoryId repositoryId;

    private int issueNumber;

    /**
     * Comment to edit.
     */
    private Comment comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        issueNumber = getIntExtra(EXTRA_ISSUE_NUMBER);
        comment = getSerializableExtra(EXTRA_COMMENT);
        repositoryId = new RepositoryId(
                getStringExtra(EXTRA_REPOSITORY_OWNER),
                getStringExtra(EXTRA_REPOSITORY_NAME));

        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.issue_title) + issueNumber);
        actionBar.setSubtitle(repositoryId.generateId());
        avatars.bind(actionBar, (User) getSerializableExtra(EXTRA_USER));
    }

    @Override
    protected void createComment(String commentText) {
        if (comment != null) {
            comment.setBody(commentText);

            new EditCommentTask(this, repositoryId, comment) {
                @Override
                protected void onSuccess(Comment comment) throws Exception {
                    super.onSuccess(comment);

                    finish(comment);
                }
            }.start();
        } else {
            new CreateCommentTask(this, repositoryId, issueNumber, commentText) {

                @Override
                protected void onSuccess(Comment comment) throws Exception {
                    super.onSuccess(comment);

                    finish(comment);
                }
            }.start();
        }
    }

    @Override
    protected CommentPreviewPagerAdapter createAdapter() {
        CommentPreviewPagerAdapter commentPreviewPagerAdapter = new CommentPreviewPagerAdapter(this, repositoryId);
        commentPreviewPagerAdapter.setCommentText(comment != null ? comment.getBody() : null);
        return commentPreviewPagerAdapter;
    }
}
