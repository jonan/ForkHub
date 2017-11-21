/*
 * Copyright 2013 GitHub Inc.
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
package com.github.mobile.ui.repo;

import android.content.Context;
import android.view.LayoutInflater;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.util.AvatarLoader;

import org.eclipse.egit.github.core.Milestone;

/**
 * List adapter for a list of milestones
 */
public class MilestoneListAdapter extends SingleTypeAdapter<Milestone> {

    /**
     * Create milestone list adapter
     *
     * @param context
     * @param elements
     * @param avatars
     */
    public MilestoneListAdapter(final Context context,
                                final Milestone[] elements, final AvatarLoader avatars) {
        super(LayoutInflater.from(context), R.layout.contributor_item);

        /*this.context = context.getApplicationContext();
        this.avatars = avatars;
        setItems(elements);*/
    }

    @Override
    public long getItemId(final int position) {
        //todo
        //return getItem(position).getId();
        return 0;
    }

    @Override
    protected int[] getChildViewIds() {
        //todo
        //return new int[] { R.id.iv_avatar, R.id.tv_login, R.id.tv_contributions };
        return new int[]{};
    }

    @Override
    protected void update(int position, Milestone milestone) {
        //todo
        /*avatars.bind(imageView(0), contributor);
        setText(1, contributor.getLogin());
        setText(2, context.getString(R.string.contributions, contributor.getContributions()));*/
    }
}