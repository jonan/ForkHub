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
package com.github.mobile.core.issue;

import android.accounts.Account;

import com.github.mobile.accounts.AuthenticatedUserTask;
import com.github.mobile.ui.DialogFragmentActivity;
import com.google.inject.Inject;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.ContentsService;

import java.util.List;

/**
 * Task to get the template for new issues
 */
public class GetIssueTemplateTask extends AuthenticatedUserTask<RepositoryContents> {
    private final static String[] FILE_NAMES = {
            "ISSUE_TEMPLATE",
            "ISSUE_TEMPLATE.md",
            ".github/ISSUE_TEMPLATE",
            ".github/ISSUE_TEMPLATE.md"
    };

    @Inject
    private ContentsService service;

    private final IRepositoryIdProvider repositoryId;

    /**
     * Create task to get the template for new issues
     *
     * @param activity
     * @param repositoryId
     */
    public GetIssueTemplateTask(final DialogFragmentActivity activity,
                                final IRepositoryIdProvider repositoryId) {
        super(activity);

        this.repositoryId = repositoryId;
    }

    @Override
    protected RepositoryContents run(Account account) throws Exception {
        for (String fileName : FILE_NAMES) {
            try {
                List<RepositoryContents> files = service.getContents(repositoryId, fileName);
                if (files.size() > 0) {
                    return files.get(0);
                }
            } catch (RequestException e) {
                if (e.getStatus() != 404) {
                    throw e;
                }
            }
        }
        return null;
    }
}
