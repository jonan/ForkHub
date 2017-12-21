package com.github.mobile.ui.milestone;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import static com.github.mobile.Intents.EXTRA_MILESTONE;

/**
 * Created by Александр on 20.12.2017.
 */

public class MilestoneFragment extends DialogFragment {
    private Milestone milestone;

    private TextView milestoneTitle;
    private TextView milestoneDueTo;
    private TextView milestoneDescription;
    private ProgressBar milestoneProgress;
    private TextView milestoneProgressPercentage;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        milestone = (Milestone) getSerializableExtra(EXTRA_MILESTONE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.repo_milestone_item, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        milestoneTitle = (TextView) finder.find(R.id.tv_milestone_name);
        milestoneDueTo = (TextView) finder.find(R.id.tv_milestone_due_to);
        milestoneDescription = (TextView) finder.find(R.id.tv_milestone_description);
        milestoneProgress = (ProgressBar) finder.find(R.id.pB_milestone_completion_progress);
        milestoneProgressPercentage = (TextView) finder.find(R.id.tv_milestone_progress_percentage);
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
//            case R.id.m_edit:
//                if (issue != null) {
//                    Intent intent = EditIssueActivity.createIntent(issue,
//                            repositoryId.getOwner(), repositoryId.getName(), user);
//                    startActivityForResult(intent, ISSUE_EDIT);
//                }
//                return true;
        }
        return true;
    }

    private void updateMilestone(final Milestone milestone){
        if (!isUsable()){
            return;
        }

        milestoneTitle.setText(milestone.getTitle());
        DateFormat sdf = SimpleDateFormat.getDateInstance();
        milestoneDueTo.setText(sdf.format(milestone.getDueOn()));
        milestoneDescription.setText(milestone.getDescription());
        int totalIssues = milestone.getClosedIssues() + milestone.getOpenIssues();
        int progress = totalIssues == 0 ? 0 : milestone.getClosedIssues() * 100 / totalIssues;
        milestoneProgress.setProgress(progress);
        milestoneProgressPercentage.setText(String.valueOf(progress));
    }
}
