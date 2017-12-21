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

import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.Intents.EXTRA_REPOSITORY;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.RequestCodes.MILESTONE_VIEW;

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
import com.github.mobile.ui.milestone.MilestoneViewActivity;
import com.google.inject.Inject;

import java.util.List;

import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.RepositoryIssue;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.MilestoneService;

/**
 * Fragment to display a list of milestones for a specific repository
 */
public class RepositoryMilestonesFragment extends ItemListFragment<Milestone> {
    public static final String MILESTONES_STATE_ALL = "all";

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
                return service.getMilestones(repo, MILESTONES_STATE_ALL);
            }
        };
    }

    @Override
    protected SingleTypeAdapter<Milestone> createAdapter(List<Milestone> items) {
        return new MilestoneListAdapter(getActivity(),
                items.toArray(new Milestone[items.size()]));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        startActivityForResult(MilestoneViewActivity.createIntent(repo, (Milestone) l.getItemAtPosition(position), position), MILESTONE_VIEW);
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_milestones_load;
    }
}