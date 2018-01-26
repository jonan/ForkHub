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
package com.github.mobile.ui.milestone;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.api.model.Issue;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.SingleChoiceDialogFragment;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Dialog fragment to add an issue to milestone
 */
public class IssueDialogFragment extends SingleChoiceDialogFragment {

    private static class IssueListAdapter extends
            SingleTypeAdapter<Issue> {


        public IssueListAdapter(LayoutInflater inflater,
                Issue[] issues) {
            super(inflater, R.layout.milestone_item);

            setItems(issues);
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[] { R.id.rb_selected, R.id.tv_milestone_title,
                    R.id.tv_milestone_description };
        }

        @Override
        protected void update(int position, Issue item) {
            setText(1, item.title);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).number;
        }
    }

    /**
     * Get selected milestone from results bundle
     *
     * @param arguments
     * @return milestone
     */
    public static Issue getSelected(Bundle arguments) {
        return (Issue) arguments.getSerializable(ARG_SELECTED);
    }

    /**
     * Confirm message and deliver callback to given activity
     *
     * @param activity
     * @param requestCode
     * @param title
     * @param message
     * @param choices
     */
    public static void show(final DialogFragmentActivity activity,
            final int requestCode, final String title, final String message,
            ArrayList<Issue> choices) {
        show(activity, requestCode, title, message, choices, -1,
                new IssueDialogFragment());
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Activity activity = getActivity();

        final AlertDialog dialog = createDialog();
        dialog.setButton(BUTTON_NEGATIVE, activity.getString(R.string.cancel),
                this);

        LayoutInflater inflater = activity.getLayoutInflater();

        ListView view = (ListView) inflater.inflate(R.layout.dialog_list_view,
                null);
        view.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                onClick(dialog, position);
            }
        });

        ArrayList<Issue> choices = getChoices();
        IssueListAdapter adapter = new IssueListAdapter(inflater,
                choices.toArray(new Issue[choices.size()]));
        view.setAdapter(adapter);
        dialog.setView(view);

        return dialog;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Issue> getChoices() {
        return (ArrayList<Issue>) getArguments().getSerializable(
                ARG_CHOICES);
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        super.onClick(dialog, which);

        switch (which) {
        case BUTTON_NEGATIVE:
            break;
        default:
            getArguments().putSerializable(ARG_SELECTED,
                    getChoices().get(which));
            onResult(RESULT_OK);
        }
    }
}
