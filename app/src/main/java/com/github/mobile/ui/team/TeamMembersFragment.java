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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.ThrowableLoader;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.api.model.Team;
import com.github.mobile.ui.ItemListFragment;
import com.github.mobile.ui.user.UserListAdapter;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.TeamService;

import java.util.List;

import static com.github.mobile.Intents.EXTRA_TEAM;
import static com.github.mobile.Intents.EXTRA_USER;

/**
 * Fragment to display the members of a {@link Team}
 */
public class TeamMembersFragment extends ItemListFragment<User> {

    @Inject
    private AvatarLoader avatars;

    @Inject
    private TeamService service;

    private Team team;

    private User org;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        team = getSerializableExtra(EXTRA_TEAM);
        org = getSerializableExtra(EXTRA_USER);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(R.string.no_members);
    }

    @Override
    public Loader<List<User>> onCreateLoader(int id, Bundle args) {
        return new ThrowableLoader<List<User>>(getActivity(), items) {

            @Override
            public List<User> loadData() throws Exception {
                return service.getMembers((int) team.id);
            }
        };
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        User[] users = items.toArray(new User[items.size()]);
        return new UserListAdapter(getActivity().getLayoutInflater(), users,
                avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        User user = (User) l.getItemAtPosition(position);
        startActivity(UserViewActivity.createIntent(user));
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return R.string.error_members_load;
    }
}
