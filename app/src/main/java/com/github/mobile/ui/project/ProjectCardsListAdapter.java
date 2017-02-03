/*
 * Copyright 2017 Jon Ander Peñalba
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
package com.github.mobile.ui.project;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.ProjectCard;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

/**
 * Adapter to display a list of {@link ProjectCard} objects
 */
public class ProjectCardsListAdapter extends SingleTypeAdapter<ProjectCard> {

    private final String updateText;

    private final int colorIconClosed;
    private final int colorIconOpen;
    private final int colorIconDefault;

    /**
     * Create {@link ProjectCard} list adapter
     *
     * @param activity
     * @param elements
     */
    public ProjectCardsListAdapter(Activity activity, ProjectCard[] elements) {
        super(activity.getLayoutInflater(), R.layout.project_card_item);

        updateText = activity.getString(R.string.updated);

        Resources resources = activity.getResources();

        colorIconClosed = resources.getColor(R.color.issue_event_red);
        colorIconOpen = resources.getColor(R.color.issue_event_green);
        colorIconDefault = resources.getColor(R.color.text_icon);

        setItems(elements);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_type_icon, R.id.tv_title, R.id.tv_update_date };
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        TypefaceUtils.setOcticons(textView(view, 0));
        return view;
    }

    @Override
    protected void update(int position, ProjectCard card) {
        if (card.issue != null) {
            if (card.issue.closed_at == null) {
                textView(0).setTextColor(colorIconOpen);
                setText(0, TypefaceUtils.ICON_ISSUE_OPENED);
            } else {
                textView(0).setTextColor(colorIconClosed);
                setText(0, TypefaceUtils.ICON_ISSUE_CLOSED);
            }
            setText(1, card.issue.title);
        } else {
            textView(0).setTextColor(colorIconDefault);
            setText(0, TypefaceUtils.ICON_BOOK);
            setText(1, card.note);
        }
        setText(2, String.format(updateText, TimeUtils.getRelativeTime(card.updated_at)));
    }
}
