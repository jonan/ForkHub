package com.github.mobile.core.code;

import android.text.TextUtils;

import java.util.Map;
import java.util.TreeMap;

import static org.eclipse.egit.github.core.TreeEntry.TYPE_BLOB;
import static org.eclipse.egit.github.core.TreeEntry.TYPE_TREE;

public class TreeFolder {
    /**
     * Folder in a tree
     */
    public static class Folder extends TreeEntry.Entry {

        /**
         * Sub folders
         */
        public final Map<String, Folder> folders = new TreeMap<String, Folder>();

        /**
         * Files
         */
        public final Map<String, TreeEntry.Entry> files = new TreeMap<String, TreeEntry.Entry>();

        public Folder() {
            super();
        }

        public Folder(org.eclipse.egit.github.core.TreeEntry entry, Folder parent) {
            super(entry, parent);
        }

        public void addFile(org.eclipse.egit.github.core.TreeEntry entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                TreeEntry.Entry file = new TreeEntry.Entry(entry, this);
                files.put(file.name, file);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFile(entry, pathSegments, index + 1);
            }
        }

        public void addFolder(org.eclipse.egit.github.core.TreeEntry entry, String[] pathSegments, int index) {
            if (index == pathSegments.length - 1) {
                Folder folder = new Folder(entry, this);
                folders.put(folder.name, folder);
            } else {
                Folder folder = folders.get(pathSegments[index]);
                if (folder != null)
                    folder.addFolder(entry, pathSegments, index + 1);
            }
        }

        public void add(final org.eclipse.egit.github.core.TreeEntry entry) {
            String type = entry.getType();
            String path = entry.getPath();
            if (TextUtils.isEmpty(path))
                return;

            if (TYPE_BLOB.equals(type)) {
                String[] segments = path.split("/");
                if (segments.length > 1) {
                    Folder folder = folders.get(segments[0]);
                    if (folder != null)
                        folder.addFile(entry, segments, 1);
                } else if (segments.length == 1) {
                    TreeEntry.Entry file = new TreeEntry.Entry(entry, this);
                    files.put(file.name, file);
                }
            } else if (TYPE_TREE.equals(type)) {
                String[] segments = path.split("/");
                if (segments.length > 1) {
                    Folder folder = folders.get(segments[0]);
                    if (folder != null)
                        folder.addFolder(entry, segments, 1);
                } else if (segments.length == 1) {
                    Folder folder = new Folder(entry, this);
                    folders.put(folder.name, folder);
                }
            }
        }
    }
}
