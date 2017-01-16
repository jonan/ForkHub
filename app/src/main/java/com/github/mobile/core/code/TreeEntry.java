package com.github.mobile.core.code;

import com.github.mobile.core.commit.CommitUtils;

public class TreeEntry {
    public static class Entry implements Comparable<Entry> {

        /**
         * Parent folder
         */
        public final TreeFolder.Folder parent;

        /**
         * Raw tree entry
         */
        public final org.eclipse.egit.github.core.TreeEntry entry;

        /**
         * Name
         */
        public final String name;

        public Entry() {
            this.parent = null;
            this.entry = null;
            this.name = null;
        }

        public Entry(org.eclipse.egit.github.core.TreeEntry entry, TreeFolder.Folder parent) {
            this.entry = entry;
            this.parent = parent;
            this.name = CommitUtils.getName(entry.getPath());
        }

        @Override
        public int compareTo(Entry another) {
            return name.compareTo(another.name);
        }
    }
}
