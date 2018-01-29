/*
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

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.github.mobile.R;
import com.github.mobile.ui.DialogFragment;
import org.eclipse.egit.github.core.Milestone;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static com.github.mobile.Intents.EXTRA_MILESTONE;

/**
 * Fragment to display a milestone.
 */
public class MilestoneFragment extends DialogFragment {
    private Milestone milestone;

    private TextView milestoneTitle;
    private TextView milestoneDueTo;
    private TextView milestoneDescription;
    private ProgressBar milestoneProgress;
    private TextView milestoneProgressPercentage;
    private TextView milestoneTime;
    
    private final static int MS_TIME_PAST_DAYS = -100;
    private final static int MS_TIME_OK_DAYS = 100;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        milestone = ((com.github.mobile.api.model.Milestone)getSerializableExtra(EXTRA_MILESTONE)).getOldModel();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.repo_milestone_item, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if(bundle != null && bundle.getSerializable(EXTRA_MILESTONE) != null)
            milestone = ((com.github.mobile.api.model.Milestone)bundle.getSerializable(EXTRA_MILESTONE)).getOldModel();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        milestoneTitle = (TextView) finder.find(R.id.tv_milestone_name);
        milestoneDueTo = (TextView) finder.find(R.id.tv_milestone_due_to);
        milestoneDescription = (TextView) finder.find(R.id.tv_milestone_description);
        milestoneProgress = (ProgressBar) finder.find(R.id.pB_milestone_completion_progress);
        milestoneProgressPercentage = (TextView) finder.find(R.id.tv_milestone_progress_percentage);
        milestoneTime = (TextView) finder.find(R.id.tv_milestone_time);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (milestone != null){
            updateMilestone(milestone);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu optionsMenu, MenuInflater inflater) {
        inflater.inflate(R.menu.milestone_view, optionsMenu);
    }

    private void updateMilestone(final Milestone milestone){
        if (!isUsable()){
            return;
        }

        milestoneTitle.setText(milestone.getTitle());
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        if(milestone.getDueOn() != null) {
            milestoneDueTo.setText(sdf.format(milestone.getDueOn()));
        }
        milestoneDescription.setText(milestone.getDescription());
        int totalIssues = milestone.getClosedIssues() + milestone.getOpenIssues();
        int progress = totalIssues == 0 ? 0 : milestone.getClosedIssues() * 100 / totalIssues;
        milestoneProgress.setProgress(progress);
        milestoneProgressPercentage.setText(String.valueOf(progress));

        Date dueOn = milestone.getDueOn();
        Date current = Calendar.getInstance().getTime();
        String state = milestone.getState();
        boolean open = state.equals("open");
        long days = MS_TIME_OK_DAYS;
        if(dueOn != null) {
            long diff = dueOn.getTime() - current.getTime();
            days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        }
        GradientDrawable back = (GradientDrawable) milestoneTime.getBackground();
        if (!open){
            milestoneTime.setText(R.string.status_closed);
            back.setColor(getResources().getColor(R.color.milestone_badge_default));
        }
        else if (MS_TIME_PAST_DAYS <= days && days < 0 && open){
            milestoneTime.setText(getString(R.string.ms_time_past) + " " +(-days) + " " + getString(R.string.ms_days));
            back.setColor(getResources().getColor(R.color.milestone_badge_red));
        }
        else if (0 <= days && days < MS_TIME_OK_DAYS && open){
            milestoneTime.setText(days + " " + getString(R.string.ms_days));
            back.setColor(getResources().getColor(R.color.milestone_badge_default));
        }
        else {
            milestoneTime.setText("");
            back.setColor(getResources().getColor(R.color.background));
        }
    }
}
