/*
 * Copyright 2017 Jon Ander Peñalba
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

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.api.model.ProjectCard;
import com.github.mobile.api.service.IssueService;
import com.github.mobile.api.service.ProjectService;
import com.github.mobile.ui.NewPagedItemFragment;
import com.github.mobile.ui.issue.IssuesViewActivity;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.github.mobile.Intents.EXTRA_PROJECT_COLUMN;

/**
 * Fragment to display the list of {@link ProjectCard} for a column
 */
public class ProjectColumnFragment extends NewPagedItemFragment<ProjectCard> {

    // We need an extra request for each team, so we load them in small batches
    private static final int ITEMS_PER_PAGE = 5;

    private long columnId;

    @Inject
    private ProjectService projectservice;

    @Inject
    private IssueService issueService;

    public ProjectColumnFragment() {
        super(R.string.no_cards, R.string.loading_cards, R.string.error_cards_load);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        columnId = args.getLong(EXTRA_PROJECT_COLUMN);
    }

    @Override
    protected Object getResourceId(ProjectCard resource) {
        return resource.id;
    }

    @Override
    protected Collection<ProjectCard> getPage(int page, int itemsPerPage) throws IOException {
        List<ProjectCard> cards = projectservice.getCards(columnId, page, ITEMS_PER_PAGE).execute().body();
        for (ProjectCard c : cards) {
            c.issue = getIssue(c);
        }
        return cards;
    }

    @Override
    public void onListItemClick(ListView list, View v, int position, long id) {
        ProjectCard card = items.get(position);
        if (card.issue != null) {
            startActivity(IssuesViewActivity.createIntent(card.issue.getOldModel()));
        }
    }

    @Override
    protected SingleTypeAdapter<ProjectCard> createAdapter(List<ProjectCard> items) {
        return new ProjectCardsListAdapter(getActivity(),
                items.toArray(new ProjectCard[items.size()]));
    }

    private Issue getIssue(ProjectCard card) throws IOException {
        if (TextUtils.isEmpty(card.content_url)) {
            return null;
        }

        Uri data = Uri.parse(card.content_url);
        List<String> segments = data.getPathSegments();
        if (segments == null || segments.size() != 5)
            return null;

        String owner = segments.get(1);
        String repo = segments.get(2);
        long issue = Long.decode(segments.get(4));

        return issueService.getIssue(owner, repo, issue).execute().body();
    }
}
