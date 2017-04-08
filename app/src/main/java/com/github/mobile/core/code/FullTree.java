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
package com.github.mobile.core.code;

import com.github.mobile.core.ref.RefUtils;

import java.util.List;

import org.eclipse.egit.github.core.Reference;
import org.eclipse.egit.github.core.Tree;
import org.eclipse.egit.github.core.TreeEntry;
import com.github.mobile.core.code.TreeFolder.Folder;

/**
 * {@link Tree} with additional information
 */
public class FullTree {
    /**
     * Tree
     */
    public final Tree tree;

    /**
     * Root folder
     */
    public final Folder root;

    /**
     * Reference
     */
    public final Reference reference;

    /**
     * Branch where tree is present
     */
    public final String branch;

    /**
     * Create tree with branch
     *
     * @param tree
     * @param reference
     */
    public FullTree(final Tree tree, final Reference reference) {
        this.tree = tree;
        this.reference = reference;
        this.branch = RefUtils.getName(reference);

        root = new TreeFolder.Folder();
        List<TreeEntry> entries = tree.getTree();
        if (entries != null && !entries.isEmpty())
            for (TreeEntry entry : entries)
                root.add(entry);
    }
}
