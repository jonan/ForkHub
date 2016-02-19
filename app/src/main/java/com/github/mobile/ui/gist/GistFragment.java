/*
 * Copyright 2012 GitHub Inc.
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
package com.github.mobile.ui.gist;

import com.google.inject.Inject;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.core.OnLoadListener;
import com.github.mobile.core.gist.FullGist;
import com.github.mobile.core.gist.GistStore;
import com.github.mobile.core.gist.RefreshGistTask;
import com.github.mobile.core.gist.StarGistTask;
import com.github.mobile.core.gist.UnstarGistTask;
import com.github.mobile.ui.DialogFragment;
import com.github.mobile.ui.HeaderFooterListAdapter;
import com.github.mobile.ui.StyledText;
import com.github.mobile.ui.comment.CommentListAdapter;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.ShareUtils;
import com.github.mobile.util.ToastUtils;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.User;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.mobile.Intents.EXTRA_COMMENT;
import static com.github.mobile.Intents.EXTRA_GIST_ID;
import static com.github.mobile.RequestCodes.COMMENT_CREATE;

/**
 * Activity to display an existing Gist
 */
public class GistFragment extends DialogFragment implements OnItemClickListener {

    private String gistId;

    private List<Comment> comments;

    private Gist gist;

    private ListView list;

    private ProgressBar progress;

    @Inject
    private GistStore store;

    @Inject
    private HttpImageGetter imageGetter;

    private View headerView;

    private View footerView;

    private TextView created;

    private TextView updated;

    private TextView description;

    private View loadingView;

    private HeaderFooterListAdapter<CommentListAdapter> adapter;

    private boolean starred;

    private boolean loadFinished;

    private MenuItem starItem;

    @Inject
    private AvatarLoader avatars;

    private List<View> fileHeaders = new ArrayList<View>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gistId = getArguments().getString(EXTRA_GIST_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comment_list, null);

        headerView = inflater.inflate(R.layout.gist_header, null);
        created = (TextView) headerView.findViewById(R.id.tv_gist_creation);
        updated = (TextView) headerView.findViewById(R.id.tv_gist_updated);
        description = (TextView) headerView
                .findViewById(R.id.tv_gist_description);

        loadingView = inflater.inflate(R.layout.loading_item, null);
        ((TextView) loadingView.findViewById(R.id.tv_loading))
                .setText(R.string.loading_comments);

        footerView = inflater.inflate(R.layout.footer_separator, null);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = finder.find(android.R.id.list);
        progress = finder.find(R.id.pb_loading);

        Activity activity = getActivity();
        adapter = new HeaderFooterListAdapter<CommentListAdapter>(list, new CommentListAdapter(activity, avatars, imageGetter));
        list.setAdapter(adapter);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        list.setOnItemClickListener(this);
        adapter.addHeader(headerView);
        adapter.addFooter(footerView);

        gist = store.getGist(gistId);

        if (gist != null) {
            updateHeader(gist);
            updateFiles(gist);
        }

        if (gist == null || (gist.getComments() > 0 && comments == null))
            adapter.addHeader(loadingView, null, false);

        if (gist != null && comments != null)
            updateList(gist, comments);
        else
            refreshGist();
    }

    private boolean isOwner() {
        if (gist == null)
            return false;
        User owner = gist.getOwner();
        if (owner == null)
            return false;
        String login = AccountUtils.getLogin(getActivity());
        return login != null && login.equals(owner.getLogin());
    }

    private void updateHeader(Gist gist) {
        Date createdAt = gist.getCreatedAt();
        if (createdAt != null) {
            StyledText text = new StyledText();
            text.append(getString(R.string.prefix_created));
            text.append(createdAt);
            created.setText(text);
            created.setVisibility(VISIBLE);
        } else
            created.setVisibility(GONE);

        Date updatedAt = gist.getUpdatedAt();
        if (updatedAt != null && !updatedAt.equals(createdAt)) {
            StyledText text = new StyledText();
            text.append(getString(R.string.prefix_updated));
            text.append(updatedAt);
            updated.setText(text);
            updated.setVisibility(VISIBLE);
        } else
            updated.setVisibility(GONE);

        String desc = gist.getDescription();
        if (!TextUtils.isEmpty(desc))
            description.setText(desc);
        else
            description.setText(R.string.no_description_given);

        ViewUtils.setGone(progress, true);
        ViewUtils.setGone(list, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu options, MenuInflater inflater) {
        inflater.inflate(R.menu.gist_view, options);

        boolean owner = isOwner();
        if (!owner) {
            options.removeItem(R.id.m_delete);
        }

        starItem = options.findItem(R.id.m_star);
        refreshStarOptionsMenu();
    }

    private void refreshStarOptionsMenu() {
        if (starItem != null) {
            starItem.setEnabled(loadFinished);
            starItem.setTitle(starred ? R.string.unstar : R.string.star);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (gist == null)
            return super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
        case R.id.m_comment:
            startActivityForResult(CreateCommentActivity.createIntent(gist),
                    COMMENT_CREATE);
            return true;
        case R.id.m_star:
            if (starred)
                unstarGist();
            else
                starGist();
            return true;
        case R.id.m_refresh:
            refreshGist();
            return true;
        case R.id.m_share:
            shareGist();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void starGist() {
        ToastUtils.show(getActivity(), R.string.starring_gist);

        new StarGistTask(getActivity(), gistId) {

            @Override
            protected void onSuccess(Gist gist) throws Exception {
                super.onSuccess(gist);

                starred = true;
                refreshStarOptionsMenu();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                ToastUtils.show((Activity) getContext(), e.getMessage());
            }

        }.execute();
    }

    private void shareGist() {
        StringBuilder subject = new StringBuilder("Gist ");
        String id = gist.getId();
        subject.append(id);
        User owner = gist.getOwner();
        if (owner != null && !TextUtils.isEmpty(owner.getLogin()))
            subject.append(" by ").append(owner.getLogin());
        startActivity(ShareUtils.create(subject, "https://gist.github.com/"
                + id));
    }

    private void unstarGist() {
        ToastUtils.show(getActivity(), R.string.unstarring_gist);

        new UnstarGistTask(getActivity(), gistId) {

            @Override
            protected void onSuccess(Gist gist) throws Exception {
                super.onSuccess(gist);

                starred = false;
                refreshStarOptionsMenu();
            }

            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                ToastUtils.show((Activity) getContext(), e.getMessage());
            }

        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK == resultCode && COMMENT_CREATE == requestCode
                && data != null) {
            Comment comment = (Comment) data
                    .getSerializableExtra(EXTRA_COMMENT);
            if (comments != null) {
                comments.add(comment);
                gist.setComments(gist.getComments() + 1);
                updateList(gist, comments);
            } else
                refreshGist();
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateFiles(Gist gist) {
        final Activity activity = getActivity();
        if (activity == null)
            return;

        for (View header : fileHeaders)
            adapter.removeHeader(header);
        fileHeaders.clear();

        Map<String, GistFile> files = gist.getFiles();
        if (files == null || files.isEmpty())
            return;

        final LayoutInflater inflater = activity.getLayoutInflater();
        final Typeface octicons = TypefaceUtils.getOcticons(activity);
        for (GistFile file : files.values()) {
            View fileView = inflater.inflate(R.layout.gist_file_item, null);
            ((TextView) fileView.findViewById(R.id.tv_file)).setText(file.getFilename());
            TextView fileIcon = (TextView) fileView.findViewById(R.id.tv_file_icon);
            fileIcon.setText(TypefaceUtils.ICON_FILE_TEXT);
            fileIcon.setTypeface(octicons);
            adapter.addHeader(fileView, file, true);
            fileHeaders.add(fileView);
        }
    }

    private void updateList(Gist gist, List<Comment> comments) {
        adapter.getWrappedAdapter().setItems(
                comments.toArray(new Comment[comments.size()]));
        adapter.removeHeader(loadingView);

        headerView.setVisibility(VISIBLE);
        updateHeader(gist);

        updateFiles(gist);
    }

    private void refreshGist() {
        new RefreshGistTask(getActivity(), gistId, imageGetter) {

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                ToastUtils.show(getActivity(), e, R.string.error_gist_load);
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void onSuccess(FullGist fullGist) throws Exception {
                super.onSuccess(fullGist);

                if (!isUsable())
                    return;

                FragmentActivity activity = getActivity();
                if (activity instanceof OnLoadListener)
                    ((OnLoadListener<Gist>) activity)
                            .loaded(fullGist.getGist());

                starred = fullGist.isStarred();
                loadFinished = true;
                gist = fullGist.getGist();
                comments = fullGist;
                updateList(fullGist.getGist(), fullGist);
                refreshStarOptionsMenu();
            }

        }.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id) {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof GistFile)
            startActivity(GistFilesViewActivity
                    .createIntent(gist, position - 1));
    }
}
