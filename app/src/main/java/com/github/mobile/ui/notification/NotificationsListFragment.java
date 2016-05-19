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
package com.github.mobile.ui.notification;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.ThrowableLoader;
import com.github.mobile.api.model.Notification;
import com.github.mobile.api.model.Subject;
import com.github.mobile.api.service.NotificationService;
import com.github.mobile.ui.ItemListFragment;
import com.github.mobile.ui.UriLauncherActivity;
import com.google.inject.Inject;

import java.util.List;

/**
 * Fragment to display a list of {@link Notification} objects
 */
public class NotificationsListFragment extends ItemListFragment<Notification>  {

    @Inject
    private NotificationService service;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_notifications);
    }

    @Override
    public Loader<List<Notification>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<Notification>>(getActivity(), items) {

            @Override
            public List<Notification> loadData() throws Exception {
                return service.listNotifications(true).execute().body();
            }
        };
    }

    @Override
    public void onResume() {
        super.onResume();

        refresh();
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_notifications_load;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Notification notification = (Notification) l.getItemAtPosition(position);
        String url = notification.subject.url.replace("://api.github.com/repos/", "://github.com/");
        switch (notification.subject.type) {
        case Subject.TYPE_ISSUE:
            UriLauncherActivity.launchUri(getContext(), Uri.parse(url));
            break;
        case Subject.TYPE_PULL_REQUEST:
            url = url.replace("/pulls/", "/pull/");
            UriLauncherActivity.launchUri(getContext(), Uri.parse(url));
            break;
        case Subject.TYPE_COMMIT:
            url = url.replace("/commits/", "/commit/");
            UriLauncherActivity.launchUri(getContext(), Uri.parse(url));
            break;
        default:
            break;
        }
    }

    @Override
    protected SingleTypeAdapter<Notification> createAdapter(List<Notification> items) {
        return new NotificationsListAdapter(getActivity().getLayoutInflater(),
                items.toArray(new Notification[items.size()]));
    }
}
