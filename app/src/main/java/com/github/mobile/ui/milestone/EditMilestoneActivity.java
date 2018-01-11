package com.github.mobile.ui.milestone;


import android.accounts.Account;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.github.mobile.Intents;
import com.github.mobile.R;

import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.api.model.Milestone;
import com.github.mobile.core.milestone.EditMilestoneTask;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.TextWatcherAdapter;
import com.github.mobile.ui.issue.MilestoneDialog;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.MilestoneService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.github.mobile.Intents.EXTRA_MILESTONE;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;

/*
 * Activity to edit or create a milestone
 */
public class EditMilestoneActivity extends DialogFragmentActivity {


    /**
     * Create intent to create milestone
     *
     * @param repository
     * @return intent
     */
    public static Intent createIntent(Repository repository) {
        return createIntent(null, repository.getOwner().getLogin(),
                repository.getName());
    }

    /**
     * Create intent to edit milestone
     *
     * @param milestone
     * @param repositoryOwner
     * @param repositoryName
     * @return intent
     */
    public static Intent createIntent(final Milestone milestone,
                                      final String repositoryOwner, final String repositoryName) {
        Intents.Builder builder = new Intents.Builder("repo.milestones.edit.VIEW");
        builder.add(EXTRA_REPOSITORY_NAME, repositoryName);
        builder.add(EXTRA_REPOSITORY_OWNER, repositoryOwner);
        if (milestone != null)
            builder.milestone(milestone);
        return builder.toIntent();
    }

    private EditText titleText;

    private EditText descriptionText;

    private TextView dateText;

    private MilestoneDialog milestoneDialog;

    @Inject
    private MilestoneService milestoneService;

    @Inject
    private CollaboratorService collaboratorService;

    private Milestone milestone;

    private RepositoryId repository;

    private MenuItem saveItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.milestone_edit);

        titleText = finder.find(R.id.et_milestone_title);
        descriptionText = finder.find(R.id.et_milestone_description);
        dateText = finder.find(R.id.tv_milestone_date);
        Button twoWeeksButton = finder.find(R.id.b_two_weeks);
        Button monthButton = finder.find(R.id.b_month);
        Button chooseDateButton = finder.find(R.id.b_choose_date);
        Button clear = finder.find(R.id.b_clear);

        chooseDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar dateAndTime = Calendar.getInstance();

                new DatePickerDialog(EditMilestoneActivity.this, R.style.Theme_AppCompat_DayNight_Dialog, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateAndTime.set(Calendar.YEAR, year);
                        dateAndTime.set(Calendar.MONTH, monthOfYear);
                        dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                        final Date startDate = dateAndTime.getTime();
                        String fdate = sd.format(startDate);
                        dateText.setText(fdate);
                    }
                }, dateAndTime.get(Calendar.YEAR), dateAndTime.get(Calendar.MONTH), dateAndTime.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        twoWeeksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar dateAndTime = Calendar.getInstance();
                int noOfDays = 14; //two weeks
                Date dateOfOrder = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateOfOrder);
                dateAndTime.add(Calendar.DAY_OF_YEAR, noOfDays);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = dateAndTime.getTime();
                String fdate = sd.format(startDate);
                dateText.setText(fdate);
            }
        });

        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar dateAndTime = Calendar.getInstance();
                dateAndTime.add(Calendar.MONTH, 1);
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                final Date startDate = dateAndTime.getTime();
                String fdate = sd.format(startDate);
                dateText.setText(fdate);
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dateText.setText("");
            }
        });

        checkCollaboratorStatus();

        Intent intent = getIntent();

        if (savedInstanceState != null)
            milestone = (Milestone) savedInstanceState.getSerializable(EXTRA_MILESTONE);
        if (milestone == null)
            milestone = (Milestone) intent.getSerializableExtra(EXTRA_MILESTONE);
        if (milestone == null)
            milestone = new Milestone();

        repository = RepositoryId.create(
                intent.getStringExtra(EXTRA_REPOSITORY_OWNER),
                intent.getStringExtra(EXTRA_REPOSITORY_NAME));

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (milestone.number > 0)
            actionBar.setTitle(milestone.title);
        else
            actionBar.setTitle(R.string.new_milestone);
        actionBar.setSubtitle(repository.generateId());

        titleText.addTextChangedListener(new TextWatcherAdapter() {

            @Override
            public void afterTextChanged(Editable s) {
                updateSaveMenu(s);
            }
        });

        updateSaveMenu();
        titleText.setText(milestone.title);
        descriptionText.setText(milestone.description);
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        if (RESULT_OK != resultCode)
            return;
        switch (requestCode) {
            //todo think about cases
        }
    }

    private void showMainContent() {
        finder.find(R.id.sv_milestone_content).setVisibility(View.VISIBLE);
        finder.find(R.id.pb_loading).setVisibility(View.GONE);
    }

    private void showCollaboratorOptions() {
        updateMilestone();
    }

    private void updateMilestone() {
        if (milestone != null) {
            titleText.setText(milestone.title);
            descriptionText.setText(milestone.description);
            Date dueOn = milestone.due_on;
            if (dueOn != null) {
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date = sd.parse(dueOn.toString());
                    dateText.setText(date.toString());
                }
                catch (ParseException e){
                    Calendar cal = new GregorianCalendar();
                    cal.setTime(dueOn);
                    dateText.setText(sd.format(cal.getTime()));
                    //dateText.setText(dueOn.toString());
                }
            } else {
                dateText.setText("");
            }
        } else {
            titleText.setText(R.string.none);
            descriptionText.setText(R.string.none);
            dateText.setText("");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXTRA_MILESTONE, milestone);
    }

    private void updateSaveMenu() {
        if (titleText != null)
            updateSaveMenu(titleText.getText());
    }

    private void updateSaveMenu(final CharSequence text) {
        if (saveItem != null)
            saveItem.setEnabled(!TextUtils.isEmpty(text));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu options) {
        getMenuInflater().inflate(R.menu.milestone_edit, options);
        saveItem = options.findItem(R.id.m_apply);
        updateSaveMenu();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.m_apply:
                milestone.title = titleText.getText().toString();
                milestone.description = descriptionText.getText().toString();
                SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy");
                try {
                    Date date = sd.parse(dateText.getText().toString());
                    milestone.due_on = date;
                }
                catch (ParseException e){
                    e.printStackTrace();
                }

                new EditMilestoneTask(this, repository.getOwner(), repository.getName(), milestone) {

                    @Override
                    protected void onSuccess(Milestone editedMilestone)
                            throws Exception {
                        super.onSuccess(editedMilestone);

                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_MILESTONE, editedMilestone);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }.edit();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkCollaboratorStatus() {
        new AuthenticatedUserTask<Boolean>(this) {

            @Override
            public Boolean run(Account account) throws Exception {
                return collaboratorService.isCollaborator(
                        repository, AccountUtils.getLogin(EditMilestoneActivity.this));
            }

            @Override
            protected void onSuccess(Boolean isCollaborator) throws Exception {
                super.onSuccess(isCollaborator);

                showMainContent();
                if (isCollaborator)
                    showCollaboratorOptions();
            }

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);

                showMainContent();
            }
        }.execute();
    }
}
