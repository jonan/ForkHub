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

import java.text.SimpleDateFormat;
/**
 * List adapter for a list of milestones
 */
public class MilestoneListAdapter extends SingleTypeAdapter<Milestone> {

    private final Context context;
    /**
     * Create milestone list adapter
     *
     * @param context
     * @param elements
     * @param avatars
     */
    public MilestoneListAdapter(final Context context,
                                final Milestone[] elements, final AvatarLoader avatars) {
        super(LayoutInflater.from(context), R.layout.milestone_item);
        this.context = context.getApplicationContext();
        setItems(elements);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[]{R.id.tv_milestone_title,
                R.id.tv_milestone_due_to,
                R.id.tv_milestone_opened_iss_number,
                R.id.tv_milestone_closed_iss_number};
    }

    @Override
    protected void update(int position, Milestone milestone) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

        setText(0, milestone.getTitle());
        setText(1, context.getString(R.string.ms_due_by) + sdf.format(milestone.getDueOn()));
        setText(2, context.getString(R.string.ms_opened_issues) + String.valueOf(milestone.getOpenIssues()));
        setText(3, context.getString(R.string.ms_closed_issues) + String.valueOf(milestone.getClosedIssues()));
    }
}