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

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Notification;
import com.github.mobile.api.model.Subject;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

/**
 * Adapter to display a list of {@link Notification} objects
 */
public class NotificationsListAdapter extends SingleTypeAdapter<Notification> {

    private final int colorIconRead;
    private final int colorIconUnread;

    private final int colorTextRead;
    private final int colorTextUnread;

    /**
     * Create {@link Notification} list adapter
     *
     * @param activity
     * @param elements
     */
    public NotificationsListAdapter(Activity activity, Notification[] elements) {
        super(activity.getLayoutInflater(), R.layout.notification_item);

        Resources resources = activity.getResources();

        colorIconRead = resources.getColor(R.color.text_light);
        colorIconUnread = resources.getColor(R.color.notification_unread);

        colorTextRead = resources.getColor(R.color.text_light);
        colorTextUnread = resources.getColor(R.color.text);

        setItems(elements);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_type_icon, R.id.tv_issue_repo_name,
                R.id.tv_issue_title, R.id.tv_event_date };
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        TypefaceUtils.setOcticons(textView(view, 0));
        return view;
    }

    @Override
    protected void update(int position, Notification notification) {
        if (notification.is_unread) {
            textView(0).setTextColor(colorIconUnread);
            textView(2).setTextColor(colorTextUnread);
        } else {
            textView(0).setTextColor(colorIconRead);
            textView(2).setTextColor(colorTextRead);
        }

        switch (notification.subject.type) {
        case Subject.TYPE_ISSUE:
            setText(0, TypefaceUtils.ICON_ISSUE_OPENED);
            break;
        case Subject.TYPE_PULL_REQUEST:
            setText(0, TypefaceUtils.ICON_GIT_PULL_REQUEST);
            break;
        case Subject.TYPE_COMMIT:
            setText(0, TypefaceUtils.ICON_GIT_COMMIT);
            break;
        case Subject.TYPE_RELEASE:
            setText(0, TypefaceUtils.ICON_TAG);
            break;
        default:
            setText(0, "");
            break;
        }

        setText(1, notification.repository.full_name);
        setText(2, notification.subject.title);
        setText(3, TimeUtils.getRelativeTime(notification.updated_at));
    }
}
