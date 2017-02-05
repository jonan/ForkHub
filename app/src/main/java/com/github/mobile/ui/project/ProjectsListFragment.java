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
package com.github.mobile.ui.project;

import android.content.Context;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Project;
import com.github.mobile.api.service.ProjectService;
import com.github.mobile.ui.NewPagedItemFragment;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.github.mobile.Intents.EXTRA_REPOSITORY;

/**
 * Fragment to display a list of {@link Project} objects
 */
public class ProjectsListFragment extends NewPagedItemFragment<Project> {

    private Repository repository;

    @Inject
    private ProjectService service;

    public ProjectsListFragment() {
        super(R.string.no_projects, R.string.loading_projects, R.string.error_projects_load);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        repository = getSerializableExtra(EXTRA_REPOSITORY);
    }

    @Override
    protected Object getResourceId(Project resource) {
        return resource.id;
    }

    @Override
    protected Collection<Project> getPage(int page, int itemsPerPage) throws IOException {
        return service.getProjects(repository.getOwner().getLogin(), repository.getName(), page).execute().body();
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        Project project = items.get(position);
        startActivity(ProjectViewActivity.createIntent(repository, project));
    }

    @Override
    protected SingleTypeAdapter<Project> createAdapter(List<Project> items) {
        return new ProjectsListAdapter(getActivity(),
                items.toArray(new Project[items.size()]));
    }
}
