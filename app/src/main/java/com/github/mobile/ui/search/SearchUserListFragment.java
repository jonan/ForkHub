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
package com.github.mobile.ui.search;

import static android.app.SearchManager.QUERY;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.api.model.User;
import com.github.mobile.api.service.PaginationService;
import com.github.mobile.api.service.SearchService;
import com.github.mobile.core.ResourcePager;
import com.github.mobile.core.user.RefreshUserTask;
import com.github.mobile.ui.NewPagedItemFragment;
import com.github.mobile.ui.PagedItemFragment;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Fragment to display a list of {@link User} instances
 */
public class SearchUserListFragment extends NewPagedItemFragment<User> {

    @Inject
    private SearchService service;

    @Inject
    private AvatarLoader avatars;

    private String query;

    public SearchUserListFragment() {
        super(R.string.no_people, R.string.loading_people, R.string.error_users_search);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        query = getStringExtra(QUERY);
    }

    @Override
    public void refresh() {
        query = getStringExtra(QUERY);

        super.refresh();
    }

    @Override
    public void refreshWithProgress() {
        query = getStringExtra(QUERY);

        super.refreshWithProgress();
    }

    @Override
    protected Object getResourceId(User resource) {
        return resource.id;
    }

    @Override
    protected Collection<User> getPage(int page, int itemsPerPage) throws IOException {
        return service.searchUsers(query, page).execute().body().items;
    }

    @Override
    protected SingleTypeAdapter<User> createAdapter(List<User> items) {
        return new SearchUserListAdapter(getActivity(),
                items.toArray(new User[items.size()]), avatars);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        final User result = (User) l.getItemAtPosition(position);
        new RefreshUserTask(getActivity(), result.login) {

            @Override
            protected void onSuccess(org.eclipse.egit.github.core.User user) throws Exception {
                super.onSuccess(user);

                startActivity(UserViewActivity.createIntent(user));
            }
        }.execute();
    }
}
