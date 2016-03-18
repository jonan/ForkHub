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
package com.github.mobile.ui.repo;

import android.app.Activity;
import android.os.Bundle;

import com.github.mobile.ui.user.OrganizationSelectionListener;
import com.github.mobile.ui.user.OrganizationSelectionProvider;

import org.eclipse.egit.github.core.User;

import static com.github.mobile.Intents.EXTRA_USER;

/**
 * Fragment to display the list of starred repositories
 */
public class StarredRepositoryListFragment extends UserStarredRepositoryListFragment
        implements OrganizationSelectionListener {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Activity activity = getActivity();
        User currentOrg = ((OrganizationSelectionProvider) activity).addListener(this);
        if (currentOrg == null && savedInstanceState != null)
            currentOrg = (User) savedInstanceState.getSerializable(EXTRA_USER);
        user = currentOrg;

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onOrganizationSelected(User organization) {
        user = organization;
    }
}
