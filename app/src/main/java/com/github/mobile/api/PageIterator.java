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
package com.github.mobile.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class PageIterator<V> implements Iterator<Collection<V>> {
    private final int itemsPerPage;

    private int nextPage;

    public PageIterator(int initialPage, int itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
        this.nextPage = initialPage;
    }

    @Override
    public boolean hasNext() {
        return this.nextPage != -1;
    }

    @Override
    public Collection<V> next() {
        if (!this.hasNext()) {
            throw new NoSuchElementException();
        }

        Collection<V> resources = getPage(nextPage, itemsPerPage);

        if (resources.size() == 0) {
            nextPage = -1;
        } else {
            ++nextPage;
        }

        return resources;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException("Remove not supported");
    }

    protected abstract Collection<V> getPage(int page, int itemsPerPage);
}
