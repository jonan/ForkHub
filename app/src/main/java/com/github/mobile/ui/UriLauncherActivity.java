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
package com.github.mobile.ui;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_GISTS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.PROTOCOL_HTTPS;
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

import com.github.mobile.R;
import com.github.mobile.core.commit.CommitUriMatcher;
import com.github.mobile.core.gist.GistUriMatcher;
import com.github.mobile.core.issue.IssueUriMatcher;
import com.github.mobile.core.repo.RepositoryUriMatcher;
import com.github.mobile.core.user.UserUriMatcher;

import java.net.URI;
import java.text.MessageFormat;
import java.util.List;

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

    static public boolean launchUriInBrowser(Context context, Uri data) {
        Intent intent = getBrowserIntentForURI(context, data);
        if (intent == null) {
            return false;
        }

        context.startActivity(intent);
        return true;
    }

    static public Intent getIntentForURI(Uri data) {
        List<String> segments = data.getPathSegments();
        if (segments == null)
            return null;

        Intent intent;
        if (HOST_GISTS.equals(data.getHost())) {
            intent = GistUriMatcher.getGistIntent(segments);
            if (intent != null) {
                return intent;
            }
        } else if (HOST_DEFAULT.equals(data.getHost())) {
            intent = CommitUriMatcher.getCommitIntent(segments);
            if (intent != null) {
                return intent;
            }

            intent = IssueUriMatcher.getIssueIntent(segments);
            if (intent != null) {
                return intent;
            }

            intent = RepositoryUriMatcher.getRepositoryIntent(segments);
            if (intent != null) {
                return intent;
            }

            intent = UserUriMatcher.getUserIntent(segments);
            if (intent != null) {
                return intent;
            }
        }

        return null;
    }

    static public Intent getBrowserIntentForURI(Context context, Uri data) {
        final Intent dummyIntent =
                new Intent(Intent.ACTION_VIEW, Uri.parse("https://dummyintent.github.com/"))
                        .addCategory(CATEGORY_BROWSABLE);

        List<ResolveInfo> resolvers = context.getPackageManager().queryIntentActivities(dummyIntent, 0);
        if (resolvers.isEmpty()) {
            return null;
        }

        return new Intent(Intent.ACTION_VIEW, data)
                .addCategory(CATEGORY_BROWSABLE)
                .setPackage(resolvers.get(0).activityInfo.packageName);
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

        // If we can't open the link try to open it in a browser
        final Intent externalIntent = getBrowserIntentForURI(this, data);
        if (externalIntent != null) {
            startActivity(externalIntent);
            finish();
            return;
        }

        showParseError(data.toString());
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
