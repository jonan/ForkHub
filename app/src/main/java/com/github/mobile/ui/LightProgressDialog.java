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
package com.github.mobile.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;

import com.github.mobile.R;

import static android.R.style.Theme_Holo_Light_Dialog;
import static android.R.style.Theme_Material_Light_Dialog_Alert;

/**
 * Progress dialog in Material Light theme
 */
public class LightProgressDialog extends ProgressDialog {

    /**
     * Create progress dialog
     *
     * @param context
     * @param resId
     * @return dialog
     */
    public static AlertDialog create(Context context, int resId) {
        return create(context, context.getResources().getString(resId));
    }

    /**
     * Create progress dialog
     *
     * @param context
     * @param message
     * @return dialog
     */
    public static AlertDialog create(Context context, CharSequence message) {
        ProgressDialog dialog = new LightProgressDialog(context, message);
        dialog.setMessage(message);
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(STYLE_SPINNER);
        dialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.spinner));
        return dialog;
    }

    private LightProgressDialog(Context context, CharSequence message) {
        super(context, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? Theme_Material_Light_Dialog_Alert : Theme_Holo_Light_Dialog);
    }
}
