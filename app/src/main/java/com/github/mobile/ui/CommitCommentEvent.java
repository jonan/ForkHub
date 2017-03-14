package com.github.mobile.ui;

import android.app.Fragment;
import android.text.TextUtils;

import com.github.mobile.core.repo.RepositoryEventMatcher;
import com.github.mobile.ui.commit.CommitViewActivity;

import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.CommitCommentPayload;
import org.eclipse.egit.github.core.event.Event;

/**
 * Created by mapang on 3/13/17.
 */

public class CommitCommentEvent implements OpenEvent{

    public void openEvent(Event event){
        Repository repo = RepositoryEventMatcher.getRepository(event.getRepo(),
                event.getActor(), event.getOrg());
        Fragment f = new Fragment();
        if (repo == null)
            return;

        CommitCommentPayload payload = (CommitCommentPayload) event
                .getPayload();
        CommitComment comment = payload.getComment();
        if (comment == null)
            return;

        String sha = comment.getCommitId();
        if (!TextUtils.isEmpty(sha))
            f.startActivity(CommitViewActivity.createIntent(repo, sha));
    }
}
