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

import static com.github.mobile.Intents.EXTRA_GIST_FILE;
import static com.github.mobile.Intents.EXTRA_GIST_ID;
import static com.github.mobile.util.PreferenceUtils.WRAP;
import android.accounts.Account;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.github.mobile.R;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.core.gist.GistStore;
import com.github.mobile.ui.DialogFragment;
import com.github.mobile.util.PreferenceUtils;
import com.github.mobile.util.SourceEditor;
import com.github.mobile.util.ToastUtils;
import com.google.inject.Inject;

import java.io.IOException;
import java.util.Map;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;

/**
 * Fragment to display the content of a file in a Gist
 */
public class GistFileFragment extends DialogFragment implements
        OnSharedPreferenceChangeListener {

    private WebView webView;

    private String gistId;

    private GistFile file;

    private Gist gist;

    @Inject
    private GistStore store;

    private SourceEditor editor;

    private SharedPreferences codePrefs;

    private MenuItem wrapItem;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        gistId = getStringExtra(EXTRA_GIST_ID);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        file = (GistFile) getArguments().get(EXTRA_GIST_FILE);
        gist = store.getGist(gistId);
        if (gist == null)
            gist = new Gist().setId(gistId);

        codePrefs = PreferenceUtils.getCodePreferences(getActivity());
        codePrefs.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        codePrefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu optionsMenu, MenuInflater inflater) {
        inflater.inflate(R.menu.code_view, optionsMenu);

        wrapItem = optionsMenu.findItem(R.id.m_wrap);
        updateWrapItem();
    }

    private void updateWrapItem() {
        if (wrapItem != null)
            if (codePrefs.getBoolean(WRAP, false))
                wrapItem.setTitle(R.string.disable_wrapping);
            else
                wrapItem.setTitle(R.string.enable_wrapping);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_wrap:
            if (editor.getWrap()) {
                item.setTitle(R.string.enable_wrapping);
                editor.setWrap(false);
            } else {
                item.setTitle(R.string.disable_wrapping);
                editor.setWrap(true);
            }
            PreferenceUtils.save(codePrefs.edit().putBoolean(WRAP,
                    editor.getWrap()));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    private void loadSource() {
        new AuthenticatedUserTask<GistFile>(getActivity()) {

            @Override
            public GistFile run(Account account) throws Exception {
                gist = store.refreshGist(gistId);
                Map<String, GistFile> files = gist.getFiles();
                if (files == null)
                    throw new IOException();
                GistFile loadedFile = files.get(file.getFilename());
                if (loadedFile == null)
                    throw new IOException();
                return loadedFile;
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                ToastUtils.show(getActivity(), e, R.string.error_gist_file_load);
            }

            @Override
            protected void onSuccess(GistFile loadedFile) throws Exception {
                super.onSuccess(loadedFile);

                if (loadedFile == null)
                    return;

                file = loadedFile;
                getArguments().putSerializable(EXTRA_GIST_FILE, file);
                if (file.getContent() != null)
                    showSource();
            }

        }.execute();
    }

    private void showSource() {
        editor.setSource(file.getFilename(), file.getContent(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gist_file_view, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        webView = finder.find(R.id.wv_code);

        editor = new SourceEditor(webView);
        editor.setWrap(PreferenceUtils.getCodePreferences(getActivity())
                .getBoolean(WRAP, false));

        if (file.getContent() != null)
            showSource();
        else
            loadSource();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
            String key) {
        if (WRAP.equals(key)) {
            updateWrapItem();
            editor.setWrap(sharedPreferences.getBoolean(WRAP, false));
        }
    }
}
