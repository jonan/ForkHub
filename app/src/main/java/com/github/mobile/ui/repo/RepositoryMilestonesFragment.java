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

import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import static org.eclipse.egit.github.core.service.IssueService.STATE_OPEN;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.ThrowableLoader;
import com.github.mobile.ui.ItemListFragment;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.util.List;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.MilestoneService;

/**
 * Fragment to display a list of milestones for a specific repository
 */
public class RepositoryMilestonesFragment extends ItemListFragment<Milestone> {

    /**
     * Avatar loader
     */
    @Inject
    protected AvatarLoader avatars;

    /**
     * Milestone service
     */
    @Inject
    protected MilestoneService service;

    private Repository repo;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        repo = getSerializableExtra(EXTRA_REPOSITORY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_milestone);
    }

    @Override
    public Loader<List<Milestone>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<Milestone>>(getActivity(), items) {

            @Override
            public List<Milestone> loadData() throws Exception {
                //todo which state? STATE_OPEN?
                return service.getMilestones(repo, STATE_OPEN);
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Milestone> createAdapter(List<Milestone> items) {
        //todo what to send to MilestoneListAdapter
        return new MilestoneListAdapter(getActivity(),
                items.toArray(new Milestone[items.size()]), avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final Milestone milestone = (Milestone) l.getItemAtPosition(position);
        Toast.makeText(getContext(),milestone.getTitle(),Toast.LENGTH_SHORT).show();
        //todo add open milestone view page
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_milestones_load;
    }
}