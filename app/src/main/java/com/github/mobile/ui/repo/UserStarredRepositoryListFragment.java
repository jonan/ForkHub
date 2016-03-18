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

import com.github.mobile.core.ResourcePager;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.StargazerService;

import java.io.IOException;

/**
 * Fragment to display the list of starred repositories for a {@link User}
 */
public class UserStarredRepositoryListFragment extends UserRepositoryListFragment {

    @Inject
    private StargazerService service;

    @Override
    protected ResourcePager<Repository> createPager() {
        return new ResourcePager<Repository>() {

            @Override
            protected Object getId(Repository resource) {
                return resource.getId();
            }

            @Override
            public PageIterator<Repository> createIterator(int page, int size) {
                try {
                    return service.pageStarred(user.getLogin(), page, size);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
