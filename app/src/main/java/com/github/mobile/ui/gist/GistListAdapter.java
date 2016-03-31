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

import android.app.Activity;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.ui.StyledText;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.TypefaceUtils;

import java.util.Collection;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.User;

/**
 * Adapter to display a list of {@link Gist} objects
 */
public class GistListAdapter extends SingleTypeAdapter<Gist> {

    private final AvatarLoader avatars;

    private final Resources resources;

    private String anonymous;

    /**
     * @param avatars
     * @param activity
     * @param elements
     */
    public GistListAdapter(AvatarLoader avatars, Resources resources,
            Activity activity, Collection<Gist> elements) {
        super(activity, R.layout.gist_item);

        this.avatars = avatars;
        this.resources = resources;
        setItems(elements);
    }

    @Override
    public long getItemId(final int position) {
        final String id = getItem(position).getId();
        return !TextUtils.isEmpty(id) ? id.hashCode() : super
                .getItemId(position);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_gist_id, R.id.tv_gist_title, R.id.tv_gist_author,
                R.id.tv_gist_files, R.id.tv_comment_icon, R.id.tv_gist_comments, R.id.iv_avatar };
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        setText(4, TypefaceUtils.ICON_COMMENT);
        TextView fileIcon = (TextView) view .findViewById(R.id.tv_file_icon);
        fileIcon.setText(TypefaceUtils.ICON_FILE_TEXT);
        TypefaceUtils.setOcticons(textView(view, 4), fileIcon);

        anonymous = view.getResources().getString(R.string.anonymous);
        return view;
    }

    @Override
    protected void update(int position, Gist gist) {
        setText(0, gist.getId());

        String description = gist.getDescription();
        if (!TextUtils.isEmpty(description))
            setText(1, description);
        else
            setText(1, R.string.no_description_given);

        User owner = gist.getOwner();
        avatars.bind(imageView(6), owner);

        StyledText authorText = new StyledText();
        if (owner != null)
            authorText.bold(owner.getLogin());
        else
            authorText.bold(anonymous);
        authorText.append(' ');
        authorText.append(gist.getCreatedAt());
        setText(2, authorText);

        setNumber(3, gist.getFiles().size());
        setNumber(5, gist.getComments());

        if (gist.getComments() > 0) {
            textView(4).setTextColor(resources.getColor(R.color.text_icon_highlighted));
            textView(5).setTextColor(resources.getColor(R.color.text_icon_highlighted));
        } else {
            textView(4).setTextColor(resources.getColor(R.color.text_icon_disabled));
            textView(5).setTextColor(resources.getColor(R.color.text_icon_disabled));
        }
    }
}
