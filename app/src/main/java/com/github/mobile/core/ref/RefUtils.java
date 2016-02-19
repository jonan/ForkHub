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
package com.github.mobile.core.ref;

import org.eclipse.egit.github.core.Reference;

import android.text.TextUtils;

/**
 * Utilities for working with {@link Reference}s
 */
public class RefUtils {

    private static final String PREFIX_REFS = "refs/";

    private static final String PREFIX_PULL = PREFIX_REFS + "pull/";

    private static final String PREFIX_TAG = PREFIX_REFS + "tags/";

    private static final String PREFIX_HEADS = PREFIX_REFS + "heads/";

    private static final String TYPE_TAG = "tag";

    /**
     * Is reference a branch?
     *
     * @param ref
     * @return true if branch, false otherwise
     */
    public static boolean isBranch(final Reference ref) {
        return ref != null && isBranch(ref.getRef());
    }

    /**
     * Is reference a branch?
     *
     * @param name
     * @return true if branch, false otherwise
     */
    public static boolean isBranch(final String name) {
        return !TextUtils.isEmpty(name) && name.startsWith(PREFIX_HEADS);
    }

    /**
     * Is reference a tag?
     *
     * @param ref
     * @return true if tag, false otherwise
     */
    public static boolean isTag(final Reference ref) {
        return ref != null && isTag(ref.getRef());
    }

    /**
     * Is reference an annotated tag?
     *
     * @param ref
     * @return true if annotated tag, false otherwise
     */
    public static boolean isAnnotatedTag(final Reference ref) {
        return ref != null && ref.getObject() != null
                && TYPE_TAG.equals(ref.getObject().getType());
    }

    /**
     * Is reference a tag?
     *
     * @param name
     * @return true if tag, false otherwise
     */
    public static boolean isTag(final String name) {
        return !TextUtils.isEmpty(name) && name.startsWith(PREFIX_TAG);
    }

    /**
     * Get path of ref with leading 'refs/' segment removed if present
     *
     * @param ref
     * @return full path
     */
    public static String getPath(final Reference ref) {
        if (ref == null)
            return null;
        String name = ref.getRef();
        if (!TextUtils.isEmpty(name) && name.startsWith(PREFIX_REFS))
            return name.substring(PREFIX_REFS.length());
        else
            return name;
    }

    /**
     * Get short name for ref
     *
     * @param ref
     * @return short name
     */
    public static String getName(final Reference ref) {
        if (ref != null)
            return getName(ref.getRef());
        else
            return null;
    }

    /**
     * Get short name for ref
     *
     * @param name
     * @return short name
     */
    public static String getName(final String name) {
        if (TextUtils.isEmpty(name))
            return name;
        if (name.startsWith(PREFIX_HEADS))
            return name.substring(PREFIX_HEADS.length());
        else if (name.startsWith(PREFIX_TAG))
            return name.substring(PREFIX_TAG.length());
        else if (name.startsWith(PREFIX_REFS))
            return name.substring(PREFIX_REFS.length());
        else
            return name;
    }

    /**
     * Should the given reference be included as valid?
     * <p>
     * This filters out pull request refs
     *
     * @param ref
     * @return true if valid, false otherwise
     */
    public static boolean isValid(final Reference ref) {
        return isBranch(ref) || isTag(ref);
    }
}
