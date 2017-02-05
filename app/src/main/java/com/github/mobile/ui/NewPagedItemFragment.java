/*
 * Copyright 2017 Jon Ander Peñalba
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
package com.github.mobile.ui;

import android.os.Bundle;

import com.github.mobile.api.service.PaginationService;
import com.github.mobile.core.ResourcePager;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


public abstract class NewPagedItemFragment<E> extends PagedItemFragment<E> {
    final private int emptyTextResource;
    final private int loadingMessageResource;
    final private int errorMessageResource;

    public NewPagedItemFragment(int emptyTextResource, int loadingMessageResource, int errorMessageResource) {
        this.emptyTextResource = emptyTextResource;
        this.loadingMessageResource = loadingMessageResource;
        this.errorMessageResource = errorMessageResource;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyText(emptyTextResource);
    }

    @Override
    protected ResourcePager<E> createPager() {
        return new ResourcePager<E>() {

            @Override
            protected Object getId(E resource) {
                return getResourceId(resource);
            }

            @Override
            public Iterator<Collection<E>> createIterator(int page, int size) {
                return new PaginationService<E>(page) {
                    @Override
                    public Collection<E> getSinglePage(int page, int itemsPerPage) throws IOException {
                        return getPage(page, itemsPerPage);
                    }
                }.getIterator();
            }
        };
    }

    protected abstract Object getResourceId(E resource);

    protected abstract Collection<E> getPage(int page, int itemsPerPage) throws IOException;

    @Override
    protected int getLoadingMessage() {
        return loadingMessageResource;
    }

    @Override
    protected int getErrorMessage(Exception exception) {
        return errorMessageResource;
    }
}
