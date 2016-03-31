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
package com.github.mobile.ui.issue;

import static android.app.Activity.RESULT_OK;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.github.mobile.Intents.EXTRA_COMMENT;
import static com.github.mobile.Intents.EXTRA_ISSUE;
import static com.github.mobile.Intents.EXTRA_ISSUE_NUMBER;
import static com.github.mobile.Intents.EXTRA_IS_COLLABORATOR;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_NAME;
import static com.github.mobile.Intents.EXTRA_REPOSITORY_OWNER;
import static com.github.mobile.Intents.EXTRA_USER;
import static com.github.mobile.RequestCodes.COMMENT_CREATE;
import static com.github.mobile.RequestCodes.COMMENT_EDIT;
import static com.github.mobile.RequestCodes.COMMENT_DELETE;
import static com.github.mobile.RequestCodes.ISSUE_ASSIGNEE_UPDATE;
import static com.github.mobile.RequestCodes.ISSUE_CLOSE;
import static com.github.mobile.RequestCodes.ISSUE_EDIT;
import static com.github.mobile.RequestCodes.ISSUE_LABELS_UPDATE;
import static com.github.mobile.RequestCodes.ISSUE_MILESTONE_UPDATE;
import static com.github.mobile.RequestCodes.ISSUE_REOPEN;
import static org.eclipse.egit.github.core.service.IssueService.STATE_OPEN;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;
import com.github.mobile.accounts.AccountUtils;
import com.github.mobile.core.issue.DeleteCommentTask;
import com.github.mobile.core.issue.EditAssigneeTask;
import com.github.mobile.core.issue.EditLabelsTask;
import com.github.mobile.core.issue.EditMilestoneTask;
import com.github.mobile.core.issue.EditStateTask;
import com.github.mobile.core.issue.FullIssue;
import com.github.mobile.core.issue.IssueStore;
import com.github.mobile.core.issue.IssueUtils;
import com.github.mobile.core.issue.RefreshIssueTask;
import com.github.mobile.ui.ConfirmDialogFragment;
import com.github.mobile.ui.DialogFragment;
import com.github.mobile.ui.DialogFragmentActivity;
import com.github.mobile.ui.HeaderFooterListAdapter;
import com.github.mobile.ui.StyledText;
import com.github.mobile.ui.comment.CommentListAdapter;
import com.github.mobile.ui.commit.CommitCompareViewActivity;
import com.github.mobile.ui.user.UserViewActivity;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.HttpImageGetter;
import com.github.mobile.util.ShareUtils;
import com.github.mobile.util.ToastUtils;
import com.github.mobile.util.TypefaceUtils;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.IssueEvent;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.User;

/**
 * Fragment to display an issue
 */
public class IssueFragment extends DialogFragment {

    private int issueNumber;

    private List<Object> items;

    private RepositoryId repositoryId;

    private Issue issue;

    private User user;

    private String loggedUser;

    private boolean isCollaborator;

    @Inject
    private AvatarLoader avatars;

    @Inject
    private IssueStore store;

    private ListView list;

    private ProgressBar progress;

    private View headerView;

    private View loadingView;

    private View footerView;

    private HeaderFooterListAdapter<CommentListAdapter> adapter;

    private EditMilestoneTask milestoneTask;

    private EditAssigneeTask assigneeTask;

    private EditLabelsTask labelsTask;

    private EditStateTask stateTask;

    private TextView stateText;

    private TextView titleText;

    private TextView bodyText;

    private TextView authorText;

    private TextView createdDateText;

    private ImageView creatorAvatar;

    private ViewGroup commitsView;

    private TextView assigneeText;

    private ImageView assigneeAvatar;

    private TextView labelsArea;

    private View milestoneArea;

    private View milestoneProgressArea;

    private TextView milestoneText;

    private MenuItem stateItem;

    @Inject
    private HttpImageGetter bodyImageGetter;

    @Inject
    private HttpImageGetter commentImageGetter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        repositoryId = RepositoryId.create(
                args.getString(EXTRA_REPOSITORY_OWNER),
                args.getString(EXTRA_REPOSITORY_NAME));
        issueNumber = args.getInt(EXTRA_ISSUE_NUMBER);
        user = (User) args.getSerializable(EXTRA_USER);
        isCollaborator = args.getBoolean(EXTRA_IS_COLLABORATOR, false);

        DialogFragmentActivity dialogActivity = (DialogFragmentActivity) getActivity();

        milestoneTask = new EditMilestoneTask(dialogActivity, repositoryId,
                issueNumber) {

            @Override
            protected void onSuccess(Issue editedIssue) throws Exception {
                super.onSuccess(editedIssue);

                updateHeader(editedIssue);
            }
        };

        assigneeTask = new EditAssigneeTask(dialogActivity, repositoryId,
                issueNumber) {

            @Override
            protected void onSuccess(Issue editedIssue) throws Exception {
                super.onSuccess(editedIssue);

                updateHeader(editedIssue);
            }
        };

        labelsTask = new EditLabelsTask(dialogActivity, repositoryId,
                issueNumber) {

            @Override
            protected void onSuccess(Issue editedIssue) throws Exception {
                super.onSuccess(editedIssue);

                updateHeader(editedIssue);
            }
        };

        stateTask = new EditStateTask(dialogActivity, repositoryId, issueNumber) {

            @Override
            protected void onSuccess(Issue editedIssue) throws Exception {
                super.onSuccess(editedIssue);

                updateHeader(editedIssue);
            }
        };
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter.addHeader(headerView);
        adapter.addFooter(footerView);

        issue = store.getIssue(repositoryId, issueNumber);

        TextView loadingText = (TextView) loadingView
                .findViewById(R.id.tv_loading);
        loadingText.setText(R.string.loading_comments);

        if (issue == null || (issue.getComments() > 0 && items == null))
            adapter.addHeader(loadingView);

        if (issue != null && items != null)
            updateList(issue, items);
        else {
            if (issue != null)
                updateHeader(issue);
            refreshIssue();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.comment_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        list = finder.find(android.R.id.list);
        progress = finder.find(R.id.pb_loading);

        LayoutInflater inflater = getLayoutInflater(savedInstanceState);

        headerView = inflater.inflate(R.layout.issue_header, null);

        stateText = (TextView) headerView.findViewById(R.id.tv_state);
        titleText = (TextView) headerView.findViewById(R.id.tv_issue_title);
        authorText = (TextView) headerView.findViewById(R.id.tv_issue_author);
        createdDateText = (TextView) headerView
                .findViewById(R.id.tv_issue_creation_date);
        creatorAvatar = (ImageView) headerView.findViewById(R.id.iv_avatar);
        commitsView = (ViewGroup) headerView.findViewById(R.id.ll_issue_commits);
        assigneeText = (TextView) headerView.findViewById(R.id.tv_assignee_name);
        assigneeAvatar = (ImageView) headerView
                .findViewById(R.id.iv_assignee_avatar);
        labelsArea = (TextView) headerView.findViewById(R.id.tv_labels);
        milestoneArea = headerView.findViewById(R.id.ll_milestone);
        milestoneText = (TextView) headerView.findViewById(R.id.tv_milestone);
        milestoneProgressArea = headerView.findViewById(R.id.v_closed);
        bodyText = (TextView) headerView.findViewById(R.id.tv_issue_body);
        bodyText.setMovementMethod(LinkMovementMethod.getInstance());

        loadingView = inflater.inflate(R.layout.loading_item, null);

        footerView = inflater.inflate(R.layout.footer_separator, null);

        commitsView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (IssueUtils.isPullRequest(issue))
                    openPullRequestCommits();
            }
        });

        stateText.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (issue != null)
                    stateTask.confirm(STATE_OPEN.equals(issue.getState()));
            }
        });

        milestoneArea.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (issue != null && isCollaborator)
                    milestoneTask.prompt(issue.getMilestone());
            }
        });

        headerView.findViewById(R.id.ll_assignee).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (issue != null && isCollaborator)
                            assigneeTask.prompt(issue.getAssignee());
                    }
                });

        labelsArea.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (issue != null && isCollaborator)
                    labelsTask.prompt(issue.getLabels());
            }
        });

        Activity activity = getActivity();
        loggedUser = AccountUtils.getLogin(activity);
        adapter = new HeaderFooterListAdapter<CommentListAdapter>(list,
                new CommentListAdapter(activity, avatars, commentImageGetter, this, isCollaborator, loggedUser));
        list.setAdapter(adapter);
    }

    private void updateHeader(final Issue issue) {
        if (!isUsable())
            return;

        boolean isPullRequest = IssueUtils.isPullRequest(issue);
        titleText.setText(issue.getTitle());

        String body = issue.getBodyHtml();
        if (!TextUtils.isEmpty(body))
            bodyImageGetter.bind(bodyText, body, issue.getId());
        else
            bodyText.setText(R.string.no_description_given);

        authorText.setText(issue.getUser().getLogin());
        createdDateText.setText(new StyledText().append(
                getString(R.string.prefix_opened)).append(issue.getCreatedAt()));
        avatars.bind(creatorAvatar, issue.getUser());
        creatorAvatar.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(UserViewActivity.createIntent(issue.getUser()));
            }
        });

        boolean open = STATE_OPEN.equals(issue.getState());
        if (!open) {
            StyledText text = new StyledText();
            if (isPullRequest && issue.getPullRequest().isMerged()) {
                text.bold(getString(R.string.merged));
                stateText.setBackgroundResource(R.color.state_background_merged);
            } else {
                text.bold(getString(R.string.closed));
                stateText.setBackgroundResource(R.color.state_background_closed);
            }
            Date closedAt = issue.getClosedAt();
            if (closedAt != null)
                text.append(' ').append(closedAt);
            stateText.setText(text);
        }
        ViewUtils.setGone(stateText, open);

        if (isPullRequest && issue.getPullRequest().getCommits() > 0) {
            ViewUtils.setGone(commitsView, false);

            TextView icon = (TextView) headerView.findViewById(R.id.tv_commit_icon);
            TypefaceUtils.setOcticons(icon);
            icon.setText(TypefaceUtils.ICON_GIT_COMMIT);

            String commits = getString(R.string.pull_request_commits,
                    issue.getPullRequest().getCommits());
            ((TextView) headerView.findViewById(R.id.tv_pull_request_commits)).setText(commits);

            ViewUtils.setGone(headerView.findViewById(R.id.ll_mergeable), !open);

            TextView mergeableIcon = (TextView) headerView.findViewById(R.id.tv_mergeable_icon);
            TypefaceUtils.setOcticons(mergeableIcon);
            TextView mergeableText = (TextView) headerView.findViewById(R.id.tv_mergeable_text);

            if (issue.getPullRequest().isMergeable()) {
                mergeableIcon.setText(TypefaceUtils.ICON_CHECK);
                mergeableIcon.setTextColor(getResources().getColor(R.color.issue_event_green));
                mergeableText.setText(R.string.pull_request_mergeable_text);
            } else {
                mergeableIcon.setText(TypefaceUtils.ICON_ALERT);
                mergeableIcon.setTextColor(getResources().getColor(R.color.text_icon));
                mergeableText.setText(R.string.pull_request_not_mergeable_text);
            }
        } else {
            ViewUtils.setGone(commitsView, true);
        }

        final User assignee = issue.getAssignee();
        if (assignee != null) {
            StyledText name = new StyledText();
            name.bold(assignee.getLogin());
            name.append(' ').append(getString(R.string.assigned));
            assigneeText.setText(name);
            assigneeAvatar.setVisibility(VISIBLE);
            avatars.bind(assigneeAvatar, assignee);
        } else {
            assigneeAvatar.setVisibility(GONE);
            assigneeText.setText(R.string.unassigned);
        }

        List<Label> labels = issue.getLabels();
        if (labels != null && !labels.isEmpty()) {
            LabelDrawableSpan.setText(labelsArea, labels);
            labelsArea.setVisibility(VISIBLE);
        } else
            labelsArea.setVisibility(GONE);

        if (issue.getMilestone() != null) {
            Milestone milestone = issue.getMilestone();
            StyledText milestoneLabel = new StyledText();
            milestoneLabel.append(getString(R.string.milestone_prefix));
            milestoneLabel.append(' ');
            milestoneLabel.bold(milestone.getTitle());
            milestoneText.setText(milestoneLabel);
            float closed = milestone.getClosedIssues();
            float total = closed + milestone.getOpenIssues();
            if (total > 0) {
                ((LayoutParams) milestoneProgressArea.getLayoutParams()).weight = closed / total;
                milestoneProgressArea.setVisibility(VISIBLE);
            } else
                milestoneProgressArea.setVisibility(GONE);
            milestoneArea.setVisibility(VISIBLE);
        } else
            milestoneArea.setVisibility(GONE);

        ViewUtils.setGone(progress, true);
        ViewUtils.setGone(list, false);
        updateStateItem(issue);
    }

    private void refreshIssue() {
        new RefreshIssueTask(getActivity(), repositoryId, issueNumber,
                bodyImageGetter, commentImageGetter) {

            @Override
            protected void onException(Exception e) throws RuntimeException {
                super.onException(e);
                ToastUtils.show(getActivity(), e, R.string.error_issue_load);
                ViewUtils.setGone(progress, true);
            }

            @Override
            protected void onSuccess(FullIssue fullIssue) throws Exception {
                super.onSuccess(fullIssue);
                if (!isUsable())
                    return;

                issue = fullIssue.getIssue();

                List<IssueEvent> events = (List<IssueEvent>) fullIssue.getEvents();
                int numEvents = events.size();

                List<Object> allItems = new ArrayList<>();

                int start = 0;
                for (Comment comment : fullIssue) {
                    for (int e = start; e < numEvents; e++) {
                        IssueEvent event = events.get(e);
                        if (comment.getCreatedAt().after(event.getCreatedAt())) {
                            if (shouldAddEvent(event, allItems))
                                allItems.add(event);
                            start++;
                        } else {
                            e = numEvents;
                        }
                    }
                    allItems.add(comment);
                }

                // Adding the last events or if there are no comments
                for (int e = start; e < numEvents; e++) {
                    IssueEvent event = events.get(e);
                    if (shouldAddEvent(event, allItems))
                        allItems.add(event);
                }

                items = allItems;
                updateList(fullIssue.getIssue(), allItems);
            }
        }.execute();
    }

    private void updateList(Issue issue, List<Object> items) {
        adapter.getWrappedAdapter().setItems(items);
        adapter.removeHeader(loadingView);

        headerView.setVisibility(VISIBLE);
        updateHeader(issue);
    }

    @Override
    public void onDialogResult(int requestCode, int resultCode, Bundle arguments) {
        if (RESULT_OK != resultCode)
            return;

        switch (requestCode) {
        case ISSUE_MILESTONE_UPDATE:
            milestoneTask.edit(MilestoneDialogFragment.getSelected(arguments));
            break;
        case ISSUE_ASSIGNEE_UPDATE:
            assigneeTask.edit(AssigneeDialogFragment.getSelected(arguments));
            break;
        case ISSUE_LABELS_UPDATE:
            ArrayList<Label> labels = LabelsDialogFragment
                    .getSelected(arguments);
            if (labels != null && !labels.isEmpty())
                labelsTask.edit(labels.toArray(new Label[labels.size()]));
            else
                labelsTask.edit(null);
            break;
        case ISSUE_CLOSE:
            stateTask.edit(true);
            break;
        case ISSUE_REOPEN:
            stateTask.edit(false);
            break;
        case COMMENT_DELETE:
            final Comment comment = (Comment) arguments.getSerializable(EXTRA_COMMENT);
            new DeleteCommentTask(getActivity(), repositoryId, comment) {
                @Override
                protected void onSuccess(Comment comment) throws Exception {
                    super.onSuccess(comment);
                    // TODO: update the commit without reloading the full issue
                    refreshIssue();
                }
            }.start();
            break;
        }
    }

    private void updateStateItem(Issue issue) {
        if (issue != null && stateItem != null)
            if (STATE_OPEN.equals(issue.getState()))
                stateItem.setTitle(R.string.close);
            else
                stateItem.setTitle(R.string.reopen);
    }

    @Override
    public void onCreateOptionsMenu(Menu optionsMenu, MenuInflater inflater) {
        inflater.inflate(R.menu.issue_view, optionsMenu);
        MenuItem editItem = optionsMenu.findItem(R.id.m_edit);
        stateItem = optionsMenu.findItem(R.id.m_state);
        if (editItem != null && stateItem != null) {
            boolean canEdit;
            if (issue != null)
                canEdit= isCollaborator || issue.getUser().getLogin().equals(loggedUser);
            else
                canEdit= isCollaborator;

            editItem.setVisible(canEdit);
            stateItem.setVisible(canEdit);
        }
        updateStateItem(issue);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode || data == null)
            return;

        switch (requestCode) {
        case ISSUE_EDIT:
            Issue editedIssue = (Issue) data.getSerializableExtra(EXTRA_ISSUE);
            bodyImageGetter.encode(editedIssue.getId(), editedIssue.getBodyHtml());
            updateHeader(editedIssue);
            break;
        case COMMENT_CREATE:
            Comment comment = (Comment) data
                    .getSerializableExtra(EXTRA_COMMENT);
            if (items != null) {
                items.add(comment);
                issue.setComments(issue.getComments() + 1);
                updateList(issue, items);
            } else
                refreshIssue();
            break;
        case COMMENT_EDIT:
            // TODO: update the commit without reloading the full issue
            refreshIssue();
            break;
        }
    }

    /**
     * Edit existing comment
     */
    public void editComment(Comment comment) {
        startActivityForResult(
                CreateCommentActivity.createIntent(repositoryId, issueNumber, user, comment),
                COMMENT_EDIT);
    }

    /**
     * Delete existing comment
     */
    public void deleteComment(Comment comment) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_COMMENT, comment);
        ConfirmDialogFragment.show(
                (DialogFragmentActivity) getActivity(),
                COMMENT_DELETE,
                getActivity().getString(R.string.confirm_comment_delete_title),
                getActivity().getString(R.string.confirm_comment_delete_message),
                args);
    }

    private void shareIssue() {
        String id = repositoryId.generateId();
        if (IssueUtils.isPullRequest(issue))
            startActivity(ShareUtils.create("Pull Request " + issueNumber
                    + " on " + id, "https://github.com/" + id + "/pull/"
                    + issueNumber));
        else
            startActivity(ShareUtils
                    .create("Issue " + issueNumber + " on " + id,
                            "https://github.com/" + id + "/issues/"
                                    + issueNumber));
    }

    private void openPullRequestCommits() {
        if (IssueUtils.isPullRequest(issue)) {
            PullRequest pullRequest = issue.getPullRequest();

            String base = pullRequest.getBase().getSha();
            String head = pullRequest.getHead().getSha();
            Repository repo = pullRequest.getBase().getRepo();
            startActivity(CommitCompareViewActivity.createIntent(repo, base, head));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.m_edit:
            if (issue != null)
                startActivityForResult(EditIssueActivity.createIntent(issue,
                                repositoryId.getOwner(), repositoryId.getName(), user),
                        ISSUE_EDIT);
            return true;
        case R.id.m_comment:
            if (issue != null)
                startActivityForResult(CreateCommentActivity.createIntent(
                        repositoryId, issueNumber, user), COMMENT_CREATE);
            return true;
        case R.id.m_refresh:
            refreshIssue();
            return true;
        case R.id.m_share:
            if (issue != null)
                shareIssue();
            return true;
        case R.id.m_state:
            if (issue != null)
                stateTask.confirm(STATE_OPEN.equals(issue.getState()));
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    static private boolean shouldAddEvent(IssueEvent event, List<Object> allItems) {
        // Exclude some events
        List<String> excludedEvents = Arrays.asList(IssueEvent.TYPE_MENTIONED, IssueEvent.TYPE_SUBSCRIBED);
        if (excludedEvents.contains(event.getEvent()))
            return false;

        // Don't show references to nonexistent commits
        if (IssueEvent.TYPE_REFERENCED.equals(event.getEvent()) && event.getCommitId() == null)
            return false;

        // Don't show the close event after the merged event
        if (!IssueEvent.TYPE_CLOSED.equals(event.getEvent()))
            return true;

        int currentSize = allItems.size();
        if (currentSize == 0)
            return true;

        Object previousItem = allItems.get(currentSize-1);
        if (!(previousItem instanceof IssueEvent))
            return true;

        if (IssueEvent.TYPE_MERGED.equals(((IssueEvent) previousItem).getEvent()))
            return false;

        return true;
    }
}
