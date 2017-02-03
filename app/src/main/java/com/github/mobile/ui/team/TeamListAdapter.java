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
package com.github.mobile.ui.team;

import android.view.LayoutInflater;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Team;

/**
 * List adapter for a list of teams
 */
public class TeamListAdapter extends SingleTypeAdapter<Team> {
    private final static String SEPARATOR = " · ";

    private final String members;
    private final String repositories;

    /**
     * Create team list adapter
     *
     * @param inflater
     * @param elements
     */
    public TeamListAdapter(final LayoutInflater inflater, final Team[] elements) {
        super(inflater, R.layout.team_item);

        members = " " + inflater.getContext().getString(R.string.members);
        repositories = " " + inflater.getContext().getString(R.string.repositories);

        setItems(elements);
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).id;
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.tv_name, R.id.tv_info };
    }

    @Override
    protected void update(final int position, final Team team) {
        setText(0, team.name);

        String infoText = "";
        if (team.members_count > 0) {
            infoText += (team.members_count + members);
            if (team.repos_count > 0) {
                infoText += SEPARATOR;
            }
        }
        if (team.repos_count > 0) {
            infoText += (team.repos_count + repositories);
        }

        setText(1, infoText);
    }
}
