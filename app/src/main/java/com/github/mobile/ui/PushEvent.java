package com.github.mobile.ui;

import android.app.Fragment;
import android.text.TextUtils;

import com.github.mobile.core.repo.RepositoryEventMatcher;
import com.github.mobile.ui.commit.CommitCompareViewActivity;
import com.github.mobile.ui.commit.CommitViewActivity;

import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.PushPayload;

import java.util.List;

/**
 * Created by mapang on 3/13/17.
 */

public class PushEvent implements OpenEvent {

    public void openEvent(Event event){

        Repository repo = RepositoryEventMatcher.getRepository(event.getRepo(),
                event.getActor(), event.getOrg());
        Fragment f = new Fragment();
        if (repo == null)
            return;

        PushPayload payload = (PushPayload) event.getPayload();
        List<Commit> commits = payload.getCommits();
        if (commits == null || commits.isEmpty())
            return;

        if (commits.size() > 1) {
            String base = payload.getBefore();
            String head = payload.getHead();
            if (!TextUtils.isEmpty(base) && !TextUtils.isEmpty(head))

                f.startActivity(CommitCompareViewActivity.createIntent(repo,
                        base, head));
        } else {
            Commit commit = commits.get(0);
            String sha = commit != null ? commit.getSha() : null;
            if (!TextUtils.isEmpty(sha))
                f.startActivity(CommitViewActivity.createIntent(repo, sha));
        }

    }
}
