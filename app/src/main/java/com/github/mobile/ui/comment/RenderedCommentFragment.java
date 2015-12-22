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
package com.github.mobile.ui.comment;

import com.google.inject.Inject;

import com.github.kevinsawicki.wishlist.Keyboard;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;
import com.github.mobile.ui.DialogFragment;
import com.github.mobile.ui.MarkdownLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.ToastUtils;

import org.eclipse.egit.github.core.IRepositoryIdProvider;

import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.Serializable;

/**
 * Fragment to display rendered comment fragment
 */
public class RenderedCommentFragment extends DialogFragment implements
        LoaderCallbacks<CharSequence> {

    private static final String ARG_TEXT = "text";

    private static final String ARG_REPO = "repo";

    private ProgressBar progress;

    private TextView bodyText;

    @Inject
    private HttpImageGetter imageGetter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress = finder.find(R.id.pb_loading);
        bodyText = finder.find(R.id.tv_comment_body);
    }

    /**
     * Set text to render
     *
     * @param raw
     * @param repo
     */
    public void setText(final String raw, final IRepositoryIdProvider repo) {
        Bundle args = new Bundle();
        args.putCharSequence(ARG_TEXT, raw);
        if (repo instanceof Serializable)
            args.putSerializable(ARG_REPO, (Serializable) repo);
        getLoaderManager().restartLoader(0, args, this);
        Keyboard.hideSoftInput(bodyText);
        showLoading(true);
    }

    private void showLoading(final boolean loading) {
        ViewUtils.setGone(progress, !loading);
        ViewUtils.setGone(bodyText, loading);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_preview, null);
    }

    @Override
    public Loader<CharSequence> onCreateLoader(int loader, Bundle args) {
        final CharSequence raw = args.getCharSequence(ARG_TEXT);
        final IRepositoryIdProvider repo = (IRepositoryIdProvider) args
                .getSerializable(ARG_REPO);
        return new MarkdownLoader(getActivity(), repo, raw.toString(),
                imageGetter, true);
    }

    @Override
    public void onLoadFinished(Loader<CharSequence> loader,
            CharSequence rendered) {
        if (rendered == null)
            ToastUtils.show(getActivity(), R.string.error_rendering_markdown);
        bodyText.setText(rendered);
        showLoading(false);
    }

    @Override
    public void onLoaderReset(Loader<CharSequence> loader) {
    }
}
