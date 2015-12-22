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
package com.github.mobile.ui.ref;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.mobile.R;
import com.github.mobile.core.ref.RefUtils;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.SingleChoiceDialogFragment;
import com.github.mobile.util.TypefaceUtils;

import org.eclipse.egit.github.core.Reference;

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

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;
import static android.content.DialogInterface.BUTTON_NEGATIVE;

/**
 * Dialog fragment to select a branch or tag
 */
public class RefDialogFragment extends SingleChoiceDialogFragment {

    private static class RefListAdapter extends SingleTypeAdapter<Reference> {

        private final int selected;

        public RefListAdapter(LayoutInflater inflater, Reference[] refs,
                int selected) {
            super(inflater, R.layout.ref_item);

            this.selected = selected;
            setItems(refs);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getRef().hashCode();
        }

        @Override
        protected int[] getChildViewIds() {
            return new int[] { R.id.tv_ref_icon, R.id.tv_ref, R.id.rb_selected };
        }

        @Override
        protected View initialize(View view) {
            view = super.initialize(view);

            TypefaceUtils.setOcticons(textView(0));
            return view;
        }

        @Override
        protected void update(int position, Reference item) {
            if (RefUtils.isTag(item))
                setText(0, TypefaceUtils.ICON_TAG);
            else
                setText(0, TypefaceUtils.ICON_GIT_BRANCH);
            setText(1, RefUtils.getName(item));
            setChecked(2, selected == position);
        }
    }

    /**
     * Get selected reference from results bundle
     *
     * @param arguments
     * @return user
     */
    public static Reference getSelected(Bundle arguments) {
        return (Reference) arguments.getSerializable(ARG_SELECTED);
    }

    /**
     * Confirm message and deliver callback to given activity
     *
     * @param activity
     * @param requestCode
     * @param title
     * @param message
     * @param choices
     * @param selectedChoice
     */
    public static void show(final DialogFragmentActivity activity,
            final int requestCode, final String title, final String message,
            ArrayList<Reference> choices, final int selectedChoice) {
        show(activity, requestCode, title, message, choices, selectedChoice,
                new RefDialogFragment());
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Activity activity = getActivity();
        Bundle arguments = getArguments();

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

        ArrayList<Reference> choices = getChoices();
        int selected = arguments.getInt(ARG_SELECTED_CHOICE);
        RefListAdapter adapter = new RefListAdapter(inflater,
                choices.toArray(new Reference[choices.size()]), selected);
        view.setAdapter(adapter);
        if (selected >= 0)
            view.setSelection(selected);
        dialog.setView(view);

        return dialog;
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Reference> getChoices() {
        return (ArrayList<Reference>) getArguments().getSerializable(
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
