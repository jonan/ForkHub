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

import com.github.mobile.R;
import com.github.mobile.ui.DialogFragment;
import com.github.mobile.ui.TextWatcherAdapter;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Fragment to display raw comment text
 */
public class RawCommentFragment extends DialogFragment {

    private EditText commentText;

    /**
     * Text to populate comment window.
     */
    private String initComment;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        commentText = finder.find(R.id.et_comment);
        commentText.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                Activity activity = getActivity();
                if (activity != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                    activity.invalidateOptionsMenu();
            }
        });
        commentText.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                commentText.requestFocusFromTouch();
                return false;
            }
        });

        setText(initComment);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_create, null);
    }

    /**
     * Get comment text
     *
     * @return text
     */
    public String getText() {
        return commentText.getText().toString();
    }

    /**
     * Set comment text
     */
    public void setText(String comment) {
        if (commentText != null) {
            commentText.setText(comment);
            commentText.selectAll();
        } else {
            initComment = comment;
        }
    }
}
