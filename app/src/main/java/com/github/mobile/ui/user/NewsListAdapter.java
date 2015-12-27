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
package com.github.mobile.ui.user;

import static com.github.kevinsawicki.wishlist.ViewUpdater.FORMAT_INT;
import static org.eclipse.egit.github.core.event.Event.TYPE_COMMIT_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_CREATE;
import static org.eclipse.egit.github.core.event.Event.TYPE_DELETE;
import static org.eclipse.egit.github.core.event.Event.TYPE_DOWNLOAD;
import static org.eclipse.egit.github.core.event.Event.TYPE_FOLLOW;
import static org.eclipse.egit.github.core.event.Event.TYPE_FORK;
import static org.eclipse.egit.github.core.event.Event.TYPE_FORK_APPLY;
import static org.eclipse.egit.github.core.event.Event.TYPE_GIST;
import static org.eclipse.egit.github.core.event.Event.TYPE_GOLLUM;
import static org.eclipse.egit.github.core.event.Event.TYPE_ISSUES;
import static org.eclipse.egit.github.core.event.Event.TYPE_ISSUE_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_MEMBER;
import static org.eclipse.egit.github.core.event.Event.TYPE_PUBLIC;
import static org.eclipse.egit.github.core.event.Event.TYPE_PULL_REQUEST;
import static org.eclipse.egit.github.core.event.Event.TYPE_PULL_REQUEST_REVIEW_COMMENT;
import static org.eclipse.egit.github.core.event.Event.TYPE_PUSH;
import static org.eclipse.egit.github.core.event.Event.TYPE_RELEASE;
import static org.eclipse.egit.github.core.event.Event.TYPE_TEAM_ADD;
import static org.eclipse.egit.github.core.event.Event.TYPE_WATCH;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.github.kevinsawicki.wishlist.SingleTypeAdapter;
import com.github.kevinsawicki.wishlist.ViewUtils;
import com.github.mobile.R;
import com.github.mobile.core.issue.IssueUtils;
import com.github.mobile.ui.StyledText;
import com.github.mobile.util.AvatarLoader;
import com.github.mobile.util.TimeUtils;
import com.github.mobile.util.TypefaceUtils;

import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Release;
import org.eclipse.egit.github.core.Team;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.event.CommitCommentPayload;
import org.eclipse.egit.github.core.event.CreatePayload;
import org.eclipse.egit.github.core.event.DeletePayload;
import org.eclipse.egit.github.core.event.DownloadPayload;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.event.EventPayload;
import org.eclipse.egit.github.core.event.EventRepository;
import org.eclipse.egit.github.core.event.FollowPayload;
import org.eclipse.egit.github.core.event.GistPayload;
import org.eclipse.egit.github.core.event.IssueCommentPayload;
import org.eclipse.egit.github.core.event.IssuesPayload;
import org.eclipse.egit.github.core.event.MemberPayload;
import org.eclipse.egit.github.core.event.PullRequestPayload;
import org.eclipse.egit.github.core.event.PullRequestReviewCommentPayload;
import org.eclipse.egit.github.core.event.PushPayload;
import org.eclipse.egit.github.core.event.ReleasePayload;
import org.eclipse.egit.github.core.event.TeamAddPayload;

/**
 * Adapter for a list of news events
 */
public class NewsListAdapter extends SingleTypeAdapter<Event> {

    /**
     * Can the given event be rendered by this view holder?
     *
     * @param event
     * @return true if renderable, false otherwise
     */
    public static boolean isValid(final Event event) {
        if (event == null)
            return false;

        final EventPayload payload = event.getPayload();
        if (payload == null || EventPayload.class.equals(payload.getClass()))
            return false;

        final String type = event.getType();
        if (TextUtils.isEmpty(type))
            return false;

        return TYPE_COMMIT_COMMENT.equals(type) //
                || (TYPE_CREATE.equals(type) //
                && ((CreatePayload) payload).getRefType() != null) //
                || TYPE_DELETE.equals(type) //
                || TYPE_DOWNLOAD.equals(type) //
                || TYPE_FOLLOW.equals(type) //
                || TYPE_FORK.equals(type) //
                || TYPE_FORK_APPLY.equals(type) //
                || (TYPE_GIST.equals(type) //
                && ((GistPayload) payload).getGist() != null) //
                || TYPE_GOLLUM.equals(type) //
                || (TYPE_ISSUE_COMMENT.equals(type) //
                && ((IssueCommentPayload) payload).getIssue() != null) //
                || (TYPE_ISSUES.equals(type) //
                && ((IssuesPayload) payload).getIssue() != null) //
                || TYPE_MEMBER.equals(type) //
                || TYPE_PUBLIC.equals(type) //
                || TYPE_PULL_REQUEST.equals(type) //
                || TYPE_PULL_REQUEST_REVIEW_COMMENT.equals(type) //
                || TYPE_PUSH.equals(type) //
                || (TYPE_RELEASE.equals(type) //
                && ((ReleasePayload) payload).getRelease() != null) //
                || TYPE_TEAM_ADD.equals(type) //
                || TYPE_WATCH.equals(type);
    }

    private static void appendComment(final StyledText details,
            final Comment comment) {
        if (comment != null)
            appendText(details, comment.getBody());
    }

    private static void appendCommitComment(final StyledText details,
            final CommitComment comment) {
        if (comment == null)
            return;

        String id = comment.getCommitId();
        if (!TextUtils.isEmpty(id)) {
            if (id.length() > 10)
                id = id.substring(0, 10);
            appendText(details, "Comment in");
            details.append(' ');
            details.monospace(id);
            details.append(':').append('\n');
        }
        appendComment(details, comment);
    }

    private static void appendText(final StyledText details, String text) {
        if (text == null)
            return;
        text = text.trim();
        if (text.length() == 0)
            return;

        details.append(text);
    }

    private static StyledText boldActor(final StyledText text, final Event event) {
        return boldUser(text, event.getActor());
    }

    private static StyledText boldUser(final StyledText text, final User user) {
        if (user != null)
            text.bold(user.getLogin());
        return text;
    }

    private static StyledText boldRepo(final StyledText text, final Event event) {
        EventRepository repo = event.getRepo();
        if (repo != null)
            text.bold(repo.getName());
        return text;
    }

    private static StyledText boldRepoName(final StyledText text,
            final Event event) {
        EventRepository repo = event.getRepo();
        if (repo != null) {
            String name = repo.getName();
            if (!TextUtils.isEmpty(name)) {
                int slash = name.indexOf('/');
                if (slash != -1 && slash + 1 < name.length())
                    text.bold(name.substring(slash + 1));
            }
        }
        return text;
    }

    private final AvatarLoader avatars;

    private final boolean showRepoName;

    /**
     * Create list adapter
     *
     * @param inflater
     * @param elements
     * @param avatars
     */
    public NewsListAdapter(LayoutInflater inflater, Event[] elements,
                           AvatarLoader avatars) {
        super(inflater, R.layout.news_item);

        this.avatars = avatars;
        setItems(elements);
        this.showRepoName = true;
    }

    /**
     * Create list adapter
     *
     * @param inflater
     * @param elements
     * @param avatars
     */
    public NewsListAdapter(LayoutInflater inflater, Event[] elements,
                           AvatarLoader avatars, boolean showRepoName) {
        super(inflater, R.layout.news_item);

        this.avatars = avatars;
        setItems(elements);
        this.showRepoName = showRepoName;
    }

    /**
     * Create list adapter
     *
     *
     * @param inflater
     * @param avatars
     */
    public NewsListAdapter(LayoutInflater inflater, AvatarLoader avatars) {
        this(inflater, null, avatars);
    }

    @Override
    public long getItemId(final int position) {
        final String id = getItem(position).getId();
        return !TextUtils.isEmpty(id) ? id.hashCode() : super
                .getItemId(position);
    }

    @Override
    protected int[] getChildViewIds() {
        return new int[] { R.id.iv_avatar, R.id.tv_event, R.id.tv_event_details,
                R.id.tv_event_icon, R.id.tv_event_date };
    }

    @Override
    protected View initialize(View view) {
        view = super.initialize(view);

        TypefaceUtils.setOcticons(textView(view, 3));
        return view;
    }

    @Override
    protected void update(int position, Event event) {
        avatars.bind(imageView(0), event.getActor());

        StyledText main = new StyledText();
        StyledText details = new StyledText();
        String icon = null;

        String type = event.getType();
        switch (type) {
        case TYPE_COMMIT_COMMENT:
            icon = TypefaceUtils.ICON_COMMENT;
            formatCommitComment(event, main, details);
            break;
        case TYPE_CREATE:
            String refType = ((CreatePayload) event.getPayload()).getRefType();
            switch (refType) {
                case "branch":
                    icon = TypefaceUtils.ICON_GIT_BRANCH;
                    break;
                case "tag":
                    icon = TypefaceUtils.ICON_TAG;
                    break;
                default:
                    icon = TypefaceUtils.ICON_REPO;
                    break;
            }
            formatCreate(event, main, details);
            break;
        case TYPE_DELETE:
            icon = TypefaceUtils.ICON_TRASHCAN;
            formatDelete(event, main, details);
            break;
        case TYPE_DOWNLOAD:
            icon = TypefaceUtils.ICON_CLOUD_UPLOAD;
            formatDownload(event, main, details);
            break;
        case TYPE_FOLLOW:
            icon = TypefaceUtils.ICON_PERSON;
            formatFollow(event, main, details);
            break;
        case TYPE_FORK:
            icon = TypefaceUtils.ICON_REPO_FORKED;
            formatFork(event, main, details);
            break;
        case TYPE_GIST:
            icon = TypefaceUtils.ICON_GIST;
            formatGist(event, main, details);
            break;
        case TYPE_GOLLUM:
            icon = TypefaceUtils.ICON_BOOK;
            formatWiki(event, main, details);
            break;
        case TYPE_ISSUE_COMMENT:
            icon = TypefaceUtils.ICON_COMMENT_DISCUSSION;
            formatIssueComment(event, main, details);
            break;
        case TYPE_ISSUES:
            String action = ((IssuesPayload) event.getPayload()).getAction();
            switch (action) {
            case "opened":
                icon = TypefaceUtils.ICON_ISSUE_OPENED;
                break;
            case "reopened":
                icon = TypefaceUtils.ICON_ISSUE_REOPENED;
                break;
            case "closed":
                icon = TypefaceUtils.ICON_ISSUE_CLOSED;
                break;
            }
            formatIssues(event, main, details);
            break;
        case TYPE_MEMBER:
            icon = TypefaceUtils.ICON_PERSON;
            formatAddMember(event, main, details);
            break;
        case TYPE_PUBLIC:
            formatPublic(event, main, details);
            break;
        case TYPE_PULL_REQUEST:
            icon = TypefaceUtils.ICON_GIT_PULL_REQUEST;
            formatPullRequest(event, main, details);
            break;
        case TYPE_PULL_REQUEST_REVIEW_COMMENT:
            icon = TypefaceUtils.ICON_COMMENT;
            formatReviewComment(event, main, details);
            break;
        case TYPE_PUSH:
            icon = TypefaceUtils.ICON_GIT_COMMIT;
            formatPush(event, main, details);
            break;
        case TYPE_RELEASE:
            icon = TypefaceUtils.ICON_CLOUD_DOWNLOAD;
            formatRelease(event, main, details);
            break;
        case TYPE_TEAM_ADD:
            icon = TypefaceUtils.ICON_PERSON;
            formatTeamAdd(event, main, details);
            break;
        case TYPE_WATCH:
            icon = TypefaceUtils.ICON_STAR;
            formatWatch(event, main, details);
            break;
        }

        if (icon != null)
            ViewUtils.setGone(setText(3, icon), false);
        else
            setGone(3, true);

        setText(1, main);

        if (!TextUtils.isEmpty(details))
            ViewUtils.setGone(setText(2, details), false);
        else
            setGone(2, true);

        setText(4, TimeUtils.getRelativeTime(event.getCreatedAt()));
    }

    private void formatCommitComment(Event event, StyledText main,
                                     StyledText details) {
        boldActor(main, event);
        main.append(" commented");

        if (showRepoName) {
            main.append(" on ");
            boldRepo(main, event);
        }

        CommitCommentPayload payload = (CommitCommentPayload) event
                .getPayload();
        appendCommitComment(details, payload.getComment());
    }

    private void formatDownload(Event event, StyledText main,
                                StyledText details) {
        boldActor(main, event);
        main.append(" uploaded a file");

        if (showRepoName) {
            main.append(" to ");
            boldRepo(main, event);
        }

        DownloadPayload payload = (DownloadPayload) event.getPayload();
        Download download = payload.getDownload();
        if (download != null)
            appendText(details, download.getName());
    }

    private void formatCreate(Event event, StyledText main,
                              StyledText details) {
        boldActor(main, event);

        main.append(" created ");
        CreatePayload payload = (CreatePayload) event.getPayload();
        String refType = payload.getRefType();
        main.append(refType);
        if (!"repository".equals(refType)) {
            main.append(' ');
            main.bold(payload.getRef());
            if (showRepoName) {
                main.append(" at ");
                boldRepo(main, event);
            }
        } else if (showRepoName) {
            main.append(' ');
            boldRepo(main, event);
        }
    }

    private void formatDelete(Event event, StyledText main,
                              StyledText details) {
        boldActor(main, event);

        DeletePayload payload = (DeletePayload) event.getPayload();
        main.append(" deleted ");
        main.append(payload.getRefType());
        main.append(' ');
        main.bold(payload.getRef());

        if (showRepoName) {
            main.append(" at ");
            boldRepo(main, event);
        }
    }

    private void formatFollow(Event event, StyledText main,
                              StyledText details) {
        boldActor(main, event);
        main.append(" started following ");
        boldUser(main, ((FollowPayload) event.getPayload()).getTarget());
    }

    private void formatFork(Event event, StyledText main,
                            StyledText details) {
        boldActor(main, event);
        main.append(" forked ");

        if (showRepoName) {
            boldRepo(main, event);
        } else {
            main.append("repository");
        }
    }

    private void formatGist(Event event, StyledText main,
                            StyledText details) {
        boldActor(main, event);

        GistPayload payload = (GistPayload) event.getPayload();

        main.append(' ');
        String action = payload.getAction();
        if ("create".equals(action))
            main.append("created");
        else if ("update".equals(action))
            main.append("updated");
        else
            main.append(action);
        main.append(" Gist ");
        main.append(payload.getGist().getId());
    }

    private void formatWiki(Event event, StyledText main,
                            StyledText details) {
        boldActor(main, event);
        main.append(" updated the wiki");

        if (showRepoName) {
            main.append(" in ");
            boldRepo(main, event);
        }
    }

    private void formatIssueComment(Event event, StyledText main,
                                    StyledText details) {
        boldActor(main, event);

        main.append(" commented on ");

        IssueCommentPayload payload = (IssueCommentPayload) event.getPayload();

        Issue issue = payload.getIssue();
        String number;
        if (IssueUtils.isPullRequest(issue))
            number = "pull request " + issue.getNumber();
        else
            number = "issue " + issue.getNumber();
        main.bold(number);

        if (showRepoName) {
            main.append(" on ");
            boldRepo(main, event);
        }

        appendComment(details, payload.getComment());
    }

    private void formatIssues(Event event, StyledText main,
                              StyledText details) {
        boldActor(main, event);

        IssuesPayload payload = (IssuesPayload) event.getPayload();
        String action = payload.getAction();
        Issue issue = payload.getIssue();
        main.append(' ');
        main.append(action);
        main.append(' ');
        main.bold("issue " + issue.getNumber());

        if (showRepoName) {
            main.append(" on ");
            boldRepo(main, event);
        }

        appendText(details, issue.getTitle());
    }

    private void formatAddMember(Event event, StyledText main,
                                 StyledText details) {
        boldActor(main, event);
        main.append(" added ");
        User member = ((MemberPayload) event.getPayload()).getMember();
        if (member != null)
            main.bold(member.getLogin());
        main.append(" as a collaborator");

        if (showRepoName) {
            main.append(" to ");
            boldRepo(main, event);
        }
    }

    private void formatPublic(Event event, StyledText main,
                              StyledText details) {
        boldActor(main, event);
        main.append(" open sourced ");

        if (showRepoName) {
            boldRepo(main, event);
        } else {
            main.append("repository");
        }
    }

    private void formatWatch(Event event, StyledText main,
                             StyledText details) {
        boldActor(main, event);
        main.append(" starred ");

        if (showRepoName) {
            boldRepo(main, event);
        } else {
            main.append("repository");
        }
    }

    private void formatReviewComment(Event event, StyledText main,
                                     StyledText details) {
        PullRequestReviewCommentPayload payload = (PullRequestReviewCommentPayload) event
                .getPayload();

        boldActor(main, event);
        main.append(" reviewed ");
        main.bold("pull request " + payload.getPullRequest().getNumber());

        if (showRepoName) {
            main.append(" on ");
            boldRepo(main, event);
        }

        appendCommitComment(details, payload.getComment());
    }

    private void formatPullRequest(Event event, StyledText main,
                                   StyledText details) {
        boldActor(main, event);

        PullRequestPayload payload = (PullRequestPayload) event.getPayload();
        String action = payload.getAction();
        if ("synchronize".equals(action))
            action = "updated";
        main.append(' ');
        main.append(action);
        main.append(' ');
        main.bold("pull request " + payload.getNumber());

        if (showRepoName) {
            main.append(" on ");
            boldRepo(main, event);
        }

        if ("opened".equals(action) || "closed".equals(action)) {
            PullRequest request = payload.getPullRequest();
            if (request != null) {
                String title = request.getTitle();
                if (!TextUtils.isEmpty(title))
                    details.append(title);
            }
        }
    }

    private void formatPush(Event event, StyledText main,
                            StyledText details) {
        boldActor(main, event);

        main.append(" pushed to ");
        PushPayload payload = (PushPayload) event.getPayload();
        String ref = payload.getRef();
        if (ref.startsWith("refs/heads/"))
            ref = ref.substring(11);
        main.bold(ref);

        if (showRepoName) {
            main.append(" at ");
            boldRepo(main, event);
        }

        final List<Commit> commits = payload.getCommits();
        int size = commits != null ? commits.size() : -1;
        if (size > 0) {
            if (size != 1)
                details.append(FORMAT_INT.format(size)).append(" new commits");
            else
                details.append("1 new commit");

            int max = 3;
            int appended = 0;
            for (Commit commit : commits) {
                if (commit == null)
                    continue;

                String sha = commit.getSha();
                if (TextUtils.isEmpty(sha))
                    continue;

                details.append('\n');
                if (sha.length() > 7)
                    details.monospace(sha.substring(0, 7));
                else
                    details.monospace(sha);

                String message = commit.getMessage();
                if (!TextUtils.isEmpty(message)) {
                    details.append(' ');
                    int newline = message.indexOf('\n');
                    if (newline > 0)
                        details.append(message.subSequence(0, newline));
                    else
                        details.append(message);
                }

                appended++;
                if (appended == max)
                    break;
            }
        }
    }

    private void formatRelease(Event event, StyledText main,
                               StyledText details) {
        boldActor(main, event);

        ReleasePayload payload = (ReleasePayload) event.getPayload();

        main.append(" released ");

        Release release = payload.getRelease();
        main.bold(release.getName());

        if (showRepoName) {
            main.append(" at ");
            boldRepo(main, event);
        }
    }

    private void formatTeamAdd(Event event, StyledText main,
                               StyledText details) {
        boldActor(main, event);

        TeamAddPayload payload = (TeamAddPayload) event.getPayload();

        main.append(" added ");

        User user = payload.getUser();
        if (user != null)
            boldUser(main, user);
        else
            boldRepoName(main, event);

        main.append(" to team");

        Team team = payload.getTeam();
        String teamName = team != null ? team.getName() : null;
        if (teamName != null)
            main.append(' ').bold(teamName);
    }
}
