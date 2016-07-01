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
package com.github.mobile.api.service;

import com.github.mobile.api.PageIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public abstract class PaginationService<V> {
    private final int itemsPerPage;
    private final int initialPage;

    public PaginationService(int initialPage) {
        this.itemsPerPage = 100;
        this.initialPage = initialPage;
    }

    public PaginationService() {
        this.itemsPerPage = 100;
        this.initialPage = 0;
    }

    public abstract Collection<V> getSinglePage(int page, int itemsPerPage) throws IOException;

    public PageIterator getIterator() {
        return new PageIterator<V>(initialPage, itemsPerPage) {

            @Override
            protected Collection<V> getPage(int page, int itemsPerPage) {
                try {
                    return getSinglePage(page, itemsPerPage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return new ArrayList<>(0);
            }
        };
    }

    public Collection<V> getAll(PageIterator<V> iterator) {
        Collection<V> result = new ArrayList<>(itemsPerPage);
        while (iterator.hasNext()) {
            result.addAll(iterator.next());
        }
        return result;
    }

    public Collection<V> getAll() {
        return getAll(getIterator());
    }
}
