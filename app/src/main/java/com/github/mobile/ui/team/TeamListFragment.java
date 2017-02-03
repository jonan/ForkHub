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

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Team;
import com.github.mobile.api.service.TeamService;
import com.github.mobile.ui.NewPagedItemFragment;
import com.github.mobile.ui.user.OrganizationSelectionListener;
import com.github.mobile.ui.user.OrganizationSelectionProvider;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.github.mobile.Intents.EXTRA_USER;

/**
 * Fragment to display the teams of an organization
 */
public class TeamListFragment extends NewPagedItemFragment<Team> implements
        OrganizationSelectionListener {

    // We need an extra request for each team, so we load them in small batches
    private static final int ITEMS_PER_PAGE = 5;

    private User org;

    @Inject
    private TeamService service;

    public TeamListFragment() {
        super(R.string.no_teams, R.string.loading_teams, R.string.error_teams_load);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (org != null)
            outState.putSerializable(EXTRA_USER, org);
    }

    @Override
    public void onDetach() {
        OrganizationSelectionProvider selectionProvider = (OrganizationSelectionProvider) getActivity();
        if (selectionProvider != null)
            selectionProvider.removeListener(this);

        super.onDetach();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        org = ((OrganizationSelectionProvider) getActivity()).addListener(this);
        if (org == null && savedInstanceState != null)
            org = (User) savedInstanceState.getSerializable(EXTRA_USER);

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected Object getResourceId(Team resource) {
        return resource.id;
    }

    @Override
    protected Collection<Team> getPage(int page, int itemsPerPage) throws IOException {
        Collection<Team> teams = service.getTeams(org.getLogin(), page, ITEMS_PER_PAGE).execute().body();
        // We need more information about each team
        List<Team> fullTeams = new ArrayList<Team>(teams.size());
        for (Team t : teams) {
            fullTeams.add(service.getTeam(t.id).execute().body());
        }
        return fullTeams;
    }

    @Override
    protected SingleTypeAdapter<Team> createAdapter(List<Team> items) {
        Team[] teams = items.toArray(new Team[items.size()]);
        return new TeamListAdapter(getActivity().getLayoutInflater(), teams);
    }

    @Override
    public void onOrganizationSelected(User organization) {
        int previousOrgId = org != null ? org.getId() : -1;
        org = organization;
        // Only hard refresh if view already created and org is changing
        if (previousOrgId != org.getId())
            refreshWithProgress();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Team team = (Team) l.getItemAtPosition(position);
        startActivity(TeamViewActivity.createIntent(team, org));
    }
}
