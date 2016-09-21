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
package com.github.mobile.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mobile.R;
import com.github.mobile.api.model.ReactionSummary;

public class ReactionsView extends LinearLayout {

    private LinearLayout plusOneView;

    private LinearLayout minusOneView;

    private LinearLayout laughView;

    private LinearLayout confusedView;

    private LinearLayout heartView;

    private LinearLayout hoorayView;

    private TextView plusOneText;

    private TextView minusOneText;

    private TextView laughText;

    private TextView confusedText;

    private TextView heartText;

    private TextView hoorayText;

    public ReactionsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(getContext(), R.layout.reactions_view, this);

        plusOneView = (LinearLayout) findViewById(R.id.ll_plus_one);
        minusOneView = (LinearLayout) findViewById(R.id.ll_minus_one);
        laughView = (LinearLayout) findViewById(R.id.ll_laugh);
        confusedView = (LinearLayout) findViewById(R.id.ll_confused);
        heartView = (LinearLayout) findViewById(R.id.ll_heart);
        hoorayView = (LinearLayout) findViewById(R.id.ll_hooray);

        plusOneText = (TextView) findViewById(R.id.tv_plus_one);
        minusOneText = (TextView) findViewById(R.id.tv_minus_one);
        laughText = (TextView) findViewById(R.id.tv_laugh);
        confusedText = (TextView) findViewById(R.id.tv_confused);
        heartText = (TextView) findViewById(R.id.tv_heart);
        hoorayText = (TextView) findViewById(R.id.tv_hooray);
    }

    public void setReactionSummary(ReactionSummary summary) {
        if (summary == null || summary.total_count == 0) {
            setVisibility(View.GONE);
            return;
        }

        setVisibility(View.VISIBLE);

        updateView(plusOneView, plusOneText, summary.plus_one);
        updateView(minusOneView, minusOneText, summary.minus_one);
        updateView(laughView, laughText, summary.laugh);
        updateView(confusedView, confusedText, summary.confused);
        updateView(heartView, heartText, summary.heart);
        updateView(hoorayView, hoorayText, summary.hooray);
    }

    private void updateView(LinearLayout ll, TextView tv, int value) {
        if (value == 0) {
            ll.setVisibility(View.GONE);
            return;
        }

        ll.setVisibility(View.VISIBLE);
        if (value >= 100) {
            tv.setText("+99");
        } else {
            tv.setText(Integer.toString(value));
        }
    }
}
