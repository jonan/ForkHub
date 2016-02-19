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
package com.github.mobile.ui.user;

import com.github.mobile.R;
import com.github.mobile.core.commit.CommitMatch;
import com.github.mobile.core.commit.CommitUriMatcher;
import com.github.mobile.core.gist.GistUriMatcher;
import com.github.mobile.core.issue.IssueUriMatcher;
import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.core.user.UserUriMatcher;
import com.github.mobile.ui.LightAlertDialog;
import com.github.mobile.ui.commit.CommitViewActivity;
import com.github.mobile.ui.gist.GistsViewActivity;
import com.github.mobile.ui.issue.IssuesViewActivity;
import com.github.mobile.ui.repo.RepositoryViewActivity;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.User;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_GISTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PROTOCOL_HTTPS;

/**
 * Activity to launch other activities based on the intent's data {@link URI}
 */
public class UriLauncherActivity extends Activity {

    static public void launchUri(Context context, Uri data) {
        Intent intent = getIntentForURI(data);
        if (intent != null) {
            context.startActivity(intent);
        } else {
            intent = new Intent(ACTION_VIEW, data).addCategory(CATEGORY_BROWSABLE);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(new Intent(ACTION_VIEW, data).addCategory(CATEGORY_BROWSABLE));
            }
        }
    }

    /**
     * Convert global view intent one into one that can be possibly opened
     * inside the current application.
     *
     * @param intent
     * @return converted intent or null if non-application specific
     */
    static public Intent convert(final Intent intent) {
        if (intent == null)
            return null;

        if (!ACTION_VIEW.equals(intent.getAction()))
            return null;

        Uri data = intent.getData();
        if (data == null)
            return null;

        if (TextUtils.isEmpty(data.getHost()) || TextUtils.isEmpty(data.getScheme())) {
            String host = data.getHost();
            if (TextUtils.isEmpty(host))
                host = HOST_DEFAULT;
            String scheme = data.getScheme();
            if (TextUtils.isEmpty(scheme))
                scheme = PROTOCOL_HTTPS;
            String prefix = scheme + "://" + host;

            String path = data.getPath();
            if (!TextUtils.isEmpty(path))
                if (path.charAt(0) == '/')
                    data = Uri.parse(prefix + path);
                else
                    data = Uri.parse(prefix + '/' + path);
            else
                data = Uri.parse(prefix);
            intent.setData(data);
        }

        return getIntentForURI(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Intent intent = getIntent();
        final Uri data = intent.getData();

        final Intent newIntent = getIntentForURI(data);
        if (newIntent != null) {
            startActivity(newIntent);
            finish();
            return;
        }

        if (!intent.hasCategory(CATEGORY_BROWSABLE)) {
            startActivity(new Intent(ACTION_VIEW, data).addCategory(CATEGORY_BROWSABLE));
            finish();
        } else {
            // If we can't open the link look for another app that can (e.g. a browser)
            Intent externalIntent = new Intent(Intent.ACTION_VIEW, data);
            List<ResolveInfo> resolvers = getPackageManager().queryIntentActivities(externalIntent, 0);
            for (ResolveInfo r : resolvers) {
                if (!"jp.forkhub".equals(r.activityInfo.packageName)) {
                    externalIntent.setPackage(r.activityInfo.packageName);
                    startActivity(externalIntent);
                    finish();
                    return;
                }
            }

            showParseError(data.toString());
        }
    }

    static private Intent getIntentForURI(Uri data) {
        if (HOST_GISTS.equals(data.getHost())) {
            Gist gist = GistUriMatcher.getGist(data);
            if (gist != null) {
                return GistsViewActivity.createIntent(gist);
            }
        } else if (HOST_DEFAULT.equals(data.getHost())) {
            CommitMatch commit = CommitUriMatcher.getCommit(data);
            if (commit != null) {
                return CommitViewActivity.createIntent(commit.repository, commit.commit);
            }

            RepositoryIssue issue = IssueUriMatcher.getIssue(data);
            if (issue != null) {
                return IssuesViewActivity.createIntent(issue, issue.getRepository());
            }

            Repository repository = RepositoryUriMatcher.getRepositoryIssues(data);
            if (repository != null) {
                return RepositoryViewActivity.createIntentForIssues(repository);
            }

            repository = RepositoryUriMatcher.getRepository(data);
            if (repository != null) {
                return RepositoryViewActivity.createIntent(repository);
            }

            User user = UserUriMatcher.getUser(data);
            if (user != null) {
                return UserViewActivity.createIntent(user);
            }
        }

        return null;
    }

    private void showParseError(String url) {
        AlertDialog dialog = LightAlertDialog.create(this);
        dialog.setTitle(R.string.title_invalid_github_url);
        dialog.setMessage(MessageFormat.format(getString(R.string.message_invalid_github_url), url));
        dialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        dialog.setButton(BUTTON_POSITIVE, getString(android.R.string.ok),
                new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        dialog.show();
    }
}
