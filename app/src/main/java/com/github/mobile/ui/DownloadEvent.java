package com.github.mobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.event.DownloadPayload;
import org.eclipse.egit.github.core.event.Event;

import static android.content.Intent.ACTION_VIEW;
import static android.content.Intent.CATEGORY_BROWSABLE;

/**
 * Created by mapang on 3/13/17.
 */

public class DownloadEvent implements OpenEvent  {

    @Override
    public void openEvent(Event event){
        Download download = ((DownloadPayload) event.getPayload())
                .getDownload();
        if (download == null)
            return;

        String url = download.getHtmlUrl();
        if (TextUtils.isEmpty(url))
            return;

        Intent intent = new Intent(ACTION_VIEW, Uri.parse(url));
        intent.addCategory(CATEGORY_BROWSABLE);
        Fragment f = new Fragment();
        f.startActivity(intent);
    }
}
