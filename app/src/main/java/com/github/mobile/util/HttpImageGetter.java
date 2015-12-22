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
package com.github.mobile.util;

import com.google.inject.Inject;

import com.github.mobile.R;
import com.github.mobile.accounts.AuthenticatedUserTask;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eclipse.egit.github.core.RepositoryContents;
import org.eclipse.egit.github.core.RepositoryId;
import org.eclipse.egit.github.core.service.ContentsService;

import android.accounts.Account;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.util.Base64.DEFAULT;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static java.lang.Integer.MAX_VALUE;
import static org.eclipse.egit.github.core.client.IGitHubConstants.HOST_DEFAULT;

/**
 * Getter for an image
 */
public class HttpImageGetter implements ImageGetter {

    private static class LoadingImageGetter implements ImageGetter {

        private final Drawable image;

        private LoadingImageGetter(final Context context, final int size) {
            int imageSize = ServiceUtils.getIntPixels(context, size);
            image = context.getResources().getDrawable(R.drawable.image_loading_icon);
            image.setBounds(0, 0, imageSize, imageSize);
        }

        @Override
        public Drawable getDrawable(String source) {
            return image;
        }
    }

    private static boolean containsImages(final String html) {
        return html.contains("<img");
    }

    private final LoadingImageGetter loading;

    private final Context context;

    private int width;

    private final Map<Object, CharSequence> rawHtmlCache = new HashMap<Object, CharSequence>();

    private final Map<Object, CharSequence> fullHtmlCache = new HashMap<Object, CharSequence>();

    private final ContentsService service;

    /**
     * Create image getter for context
     *
     * @param context
     * @param service
     */
    @Inject
    public HttpImageGetter(Context context, ContentsService service) {
        this.context = context;
        this.service = service;
        width = (int) (ServiceUtils.getDisplayWidth(context) * 0.90);
        loading = new LoadingImageGetter(context, 24);
    }

    private HttpImageGetter show(final TextView view, final CharSequence html) {
        if (TextUtils.isEmpty(html))
            return hide(view);

        view.setText(html);
        view.setVisibility(VISIBLE);
        view.setTag(null);
        return this;
    }

    private HttpImageGetter hide(final TextView view) {
        view.setText(null);
        view.setVisibility(GONE);
        view.setTag(null);
        return this;
    }

    /**
     * Encode given HTML string and map it to the given id
     *
     * @param id
     * @param html
     * @return this image getter
     */
    public HttpImageGetter encode(final Object id, final String html) {
        if (TextUtils.isEmpty(html))
            return this;

        CharSequence encoded = HtmlUtils.encode(html, loading);
        // Use default encoding if no img tags
        if (containsImages(html)) {
            CharSequence currentEncoded = rawHtmlCache.put(id, encoded);
            // Remove full html if raw html has changed
            if (currentEncoded == null
                    || !currentEncoded.toString().equals(encoded.toString()))
                fullHtmlCache.remove(id);
        } else {
            rawHtmlCache.remove(id);
            fullHtmlCache.put(id, encoded);
        }
        return this;
    }

    /**
     * Bind text view to HTML string
     *
     * @param view
     * @param html
     * @param id
     * @return this image getter
     */
    public HttpImageGetter bind(final TextView view, final String html,
            final Object id) {
        if (TextUtils.isEmpty(html))
            return hide(view);

        CharSequence encoded = fullHtmlCache.get(id);
        if (encoded != null)
            return show(view, encoded);

        encoded = rawHtmlCache.get(id);
        if (encoded == null) {
            encoded = HtmlUtils.encode(html, loading);
            if (containsImages(html))
                rawHtmlCache.put(id, encoded);
            else {
                rawHtmlCache.remove(id);
                fullHtmlCache.put(id, encoded);
                return show(view, encoded);
            }
        }

        if (TextUtils.isEmpty(encoded))
            return hide(view);

        show(view, encoded);
        view.setTag(id);
        new AuthenticatedUserTask<CharSequence>(context) {

            @Override
            protected CharSequence run(Account account) throws Exception {
                return HtmlUtils.encode(html, HttpImageGetter.this);
            }

            @Override
            protected void onSuccess(final CharSequence html) throws Exception {
                fullHtmlCache.put(id, html);

                if (id.equals(view.getTag()))
                    show(view, html);
            }
        }.execute();
        return this;
    }

    /**
     * Request an image using the contents API if the source URI is a path to a
     * file already in the repository
     *
     * @param source
     * @return
     * @throws IOException
     */
    private Drawable requestRepositoryImage(final String source) throws IOException {
        if (TextUtils.isEmpty(source))
            return null;

        Uri uri = Uri.parse(source);
        if (!HOST_DEFAULT.equals(uri.getHost()))
            return null;

        List<String> segments = uri.getPathSegments();
        if (segments.size() < 5)
            return null;

        String prefix = segments.get(2);
        // Two types of urls supported:
        // github.com/github/android/raw/master/app/res/drawable-xhdpi/app_icon.png
        // github.com/github/android/blob/master/app/res/drawable-xhdpi/app_icon.png?raw=true
        if (!("raw".equals(prefix) || ("blob".equals(prefix) && !TextUtils
                .isEmpty(uri.getQueryParameter("raw")))))
            return null;

        String owner = segments.get(0);
        if (TextUtils.isEmpty(owner))
            return null;
        String name = segments.get(1);
        if (TextUtils.isEmpty(name))
            return null;
        String branch = segments.get(3);
        if (TextUtils.isEmpty(branch))
            return null;

        StringBuilder path = new StringBuilder(segments.get(4));
        for (int i = 5; i < segments.size(); i++) {
            String segment = segments.get(i);
            if (!TextUtils.isEmpty(segment))
                path.append('/').append(segment);
        }

        if (TextUtils.isEmpty(path))
            return null;

        List<RepositoryContents> contents = service.getContents(
                RepositoryId.create(owner, name), path.toString(), branch);
        if (contents != null && contents.size() == 1) {
            byte[] content = Base64.decode(contents.get(0).getContent(),
                    DEFAULT);
            Bitmap bitmap = ImageUtils.getBitmap(content, width, MAX_VALUE);
            if (bitmap == null)
                return loading.getDrawable(source);
            BitmapDrawable drawable = new BitmapDrawable(
                    context.getResources(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return drawable;
        } else
            return null;
    }

    @Override
    public Drawable getDrawable(final String source) {
        try {
            Drawable repositoryImage = requestRepositoryImage(source);
            if (repositoryImage != null)
                return repositoryImage;
        } catch (Exception e) {
            // Ignore and attempt request over regular HTTP request
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.FROYO) {
            return loading.getDrawable(source);
        }

        try {
            Request request = new Request.Builder().url(source).build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            Bitmap bitmap = ImageUtils.getBitmap(response.body().bytes(), width, MAX_VALUE);
            if (bitmap == null)
                return loading.getDrawable(source);
            BitmapDrawable drawable = new BitmapDrawable(context.getResources(), bitmap);
            drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return drawable;
        } catch (IOException e) {
            return loading.getDrawable(source);
        }
    }
}
