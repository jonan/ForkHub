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
import android.content.Context;

import static android.R.style.Theme_Holo_Light_Dialog;
import static android.R.style.Theme_Material_Light_Dialog_Alert;

/**
 * Alert dialog using the Material Light theme
 */
public class LightAlertDialog extends AlertDialog {

    /**
     * Create alert dialog
     *
     * @param context
     * @return dialog
     */
    public static AlertDialog create(final Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            return new LightAlertDialog(context, Theme_Material_Light_Dialog_Alert);
        else
            return new LightAlertDialog(context, Theme_Holo_Light_Dialog);
    }

    private LightAlertDialog(final Context context, final int theme) {
        super(context, theme);
    }

    private LightAlertDialog(final Context context) {
        super(context);
    }

    /**
     * Alert dialog builder using the Material Light theme
     */
    public static class Builder extends AlertDialog.Builder {

        /**
         * Create alert dialog builder
         *
         * @param context
         * @return dialog builder
         */
        public static LightAlertDialog.Builder create(final Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                return new LightAlertDialog.Builder(context, Theme_Material_Light_Dialog_Alert);
            else
                return new LightAlertDialog.Builder(context, Theme_Holo_Light_Dialog);
        }

        private Builder(Context context) {
            super(context);
        }

        private Builder(Context context, int theme) {
           super(context, theme);
        }
    }
}
