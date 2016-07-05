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
package com.github.mobile.core.gist;

import android.content.Intent;
import android.text.TextUtils;

import com.github.mobile.core.commit.CommitUtils;
import com.github.mobile.ui.gist.GistsViewActivity;

import java.util.List;

import org.eclipse.egit.github.core.Gist;

/**
 * Parses a {@link Gist} from a path
 */
public class GistUriMatcher {

    /**
     * Get an intent for an exact {@link Gist} match
     *
     * @param pathSegments
     * @return {@link Intent} or null if path is not valid
     */
    public static Intent getGistIntent(List<String> pathSegments) {
        if (pathSegments.size() != 1 && pathSegments.size() != 2)
            return null;

        String gistId = pathSegments.get(pathSegments.size()-1);
        if (TextUtils.isEmpty(gistId))
            return null;

        if (TextUtils.isDigitsOnly(gistId))
            return GistsViewActivity.createIntent(new Gist().setId(gistId));

        if (CommitUtils.isValidCommit(gistId))
            return GistsViewActivity.createIntent(new Gist().setId(gistId));

        return null;
    }
}
