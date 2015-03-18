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

import static android.graphics.Bitmap.CompressFormat.PNG;
import static android.graphics.Bitmap.Config.ARGB_8888;
import static android.view.View.VISIBLE;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.actionbarsherlock.app.ActionBar;
import com.github.kevinsawicki.http.HttpRequest;
import com.github.mobile.R;
import com.github.mobile.core.search.SearchUser;
import com.google.inject.Inject;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.User;

import roboguice.util.RoboAsyncTask;

/**
 * Avatar utilities
 */
public class AvatarLoader {

    private static final String TAG = "AvatarLoader";

    private static final float CORNER_RADIUS_IN_DIP = 3;

    private static final int CACHE_SIZE = 75;

    private static abstract class FetchAvatarTask extends
            RoboAsyncTask<BitmapDrawable> {

        private static final Executor EXECUTOR = Executors
                .newFixedThreadPool(1);

        private FetchAvatarTask(Context context) {
            super(context, EXECUTOR);
        }

        @Override
        protected void onException(Exception e) throws RuntimeException {
            Log.d(TAG, "Avatar load failed", e);
        }
    }

    private final float cornerRadius;

    private final Map<Object, BitmapDrawable> loaded = new LinkedHashMap<Object, BitmapDrawable>(
            CACHE_SIZE, 1.0F) {

        private static final long serialVersionUID = -4191624209581976720L;

        @Override
        protected boolean removeEldestEntry(
                Map.Entry<Object, BitmapDrawable> eldest) {
            return size() >= CACHE_SIZE;
        }
    };

    private final Context context;
    private final Picasso p;

    private final File avatarDir;

    private final Drawable loadingAvatar;

    /**
     * The maximal size of avatar images, used to rescale images to save memory.
     */
    private static int avatarSize = 0;

    /**
     * Create avatar helper
     *
     * @param context The context from which you're calling the method.
     */
    @Inject
    public AvatarLoader(final Context context) {
        this.context = context;
        p = Picasso.with(context);
        p.setIndicatorsEnabled(true);

        loadingAvatar = context.getResources().getDrawable(R.drawable.gravatar_icon);

        avatarDir = new File(context.getCacheDir(), "avatars/github.com");
        if (!avatarDir.isDirectory())
            avatarDir.mkdirs();

        float density = context.getResources().getDisplayMetrics().density;
        cornerRadius = CORNER_RADIUS_IN_DIP * density;

        if (avatarSize == 0) {
            avatarSize = getMaxAvatarSize(context);
        }
    }

    private int getMaxAvatarSize(final Context context) {
        int[] attrs = { android.R.attr.layout_height };
        TypedArray array = context.getTheme().obtainStyledAttributes(R.style.AvatarXLarge, attrs);
        // Passing default value of 100px, but it shouldn't resolve to default anyway.
        int size = array.getLayoutDimension(0, 100);
        array.recycle();
        return size;
    }

    private BitmapDrawable getImageBy(final String userId, final String filename) {
        File avatarFile = new File(avatarDir + "/" + userId, filename);

        if (!avatarFile.exists() || avatarFile.length() == 0)
            return null;

        Bitmap bitmap = decode(avatarFile);
        if (bitmap != null)
            return new BitmapDrawable(context.getResources(), bitmap);
        else {
            avatarFile.delete();
            return null;
        }
    }

    private void deleteCachedUserAvatars(final File userAvatarDir) {
        if (userAvatarDir.isDirectory())
            for (File userAvatar : userAvatarDir.listFiles())
                userAvatar.delete();
        userAvatarDir.delete();
    }

    /**
     * Load an avatar from the given file and automatically rescale it to the
     * target dimensions to save memory.
     * @param file The file name to load the avatar from.
     * @return The rescaled avatar bitmap, or null.
     */
    private Bitmap decode(final File file) {
        return ImageUtils.getBitmap(file.getAbsolutePath(), avatarSize, avatarSize);
    }

    private String getAvatarFilenameForUrl(final String avatarUrl) {
        return GravatarUtils.getHash(avatarUrl);
    }

    /**
     * Fetch avatar from URL
     *
     * @param url The URL from which to retrieve the avatar.
     * @param cachedAvatarFilename The filename under which to store the cached avatar.
     * @return bitmap
     */
    protected BitmapDrawable fetchAvatar(final String url,
            final String userId, final String cachedAvatarFilename) {
        File userAvatarDir = new File(avatarDir, userId);
        deleteCachedUserAvatars(userAvatarDir);
        userAvatarDir.mkdirs();

        File rawAvatar = new File(userAvatarDir, cachedAvatarFilename + "-raw");
        HttpRequest request = HttpRequest.get(url);
        if (request.ok())
            request.receive(rawAvatar);

        if (!rawAvatar.exists() || rawAvatar.length() == 0)
            return null;

        Bitmap bitmap = decode(rawAvatar);
        if (bitmap == null) {
            rawAvatar.delete();
            return null;
        }

        bitmap = ImageUtils.roundCorners(bitmap, cornerRadius);
        if (bitmap == null) {
            rawAvatar.delete();
            return null;
        }

        File roundedAvatar = new File(userAvatarDir, cachedAvatarFilename);
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(roundedAvatar);
            if (bitmap.compress(PNG, 100, output))
                return new BitmapDrawable(context.getResources(), bitmap);
            else
                return null;
        } catch (IOException e) {
            Log.d(TAG, "Exception writing rounded avatar", e);
            return null;
        } finally {
            if (output != null)
                try {
                    output.close();
                } catch (IOException e) {
                    // Ignored
                }
            rawAvatar.delete();
        }
    }

    /**
     * Sets the logo on the {@link ActionBar} to the user's avatar.
     *
     * @param actionBar An ActionBar object on which you're placing the user's avatar.
     * @param user An AtomicReference that points to the desired user.
     * @return this helper
     */
    public AvatarLoader bind(final ActionBar actionBar, final User user) {
        return bind(actionBar, new AtomicReference<User>(user));
    }

    /**
     * Sets the logo on the {@link ActionBar} to the user's avatar.
     *
     * @param actionBar An ActionBar object on which you're placing the user's avatar.
     * @param userReference An AtomicReference that points to the desired user.
     * @return this helper
     */
    public AvatarLoader bind(final ActionBar actionBar,
            final AtomicReference<User> userReference) {
        if (userReference == null)
            return this;

        final User user = userReference.get();
        final String userId = getId(user);
        if (userId == null)
            return this;

        final String avatarUrl = user.getAvatarUrl();
        if (TextUtils.isEmpty(avatarUrl))
            return this;

        BitmapDrawable loadedImage = loaded.get(userId);
        if (loadedImage != null) {
            actionBar.setLogo(loadedImage);
            return this;
        }

        new FetchAvatarTask(context) {

            @Override
            public BitmapDrawable call() throws Exception {
                final String avatarFilename = getAvatarFilenameForUrl(getAvatarUrl(user));
                final BitmapDrawable image = getImageBy(userId, avatarFilename);
                if (image != null)
                    return image;
                else
                    return fetchAvatar(avatarUrl, userId, avatarFilename);
            }

            @Override
            protected void onSuccess(BitmapDrawable image) throws Exception {
                if (userId.equals(getId(userReference.get())))
                    actionBar.setLogo(image);
            }
        }.execute();

        return this;
    }

    private AvatarLoader setImage(final Drawable image, final ImageView view) {
        return setImage(image, view, null);
    }

    private AvatarLoader setImage(final Drawable image, final ImageView view,
            Object tag) {
        view.setImageDrawable(image);
        view.setTag(R.id.iv_avatar, tag);
        view.setVisibility(VISIBLE);
        return this;
    }

    private String getAvatarUrl(String id) {
        if (!TextUtils.isEmpty(id))
            return "https://secure.gravatar.com/avatar/" + id + "?d=404";
        else
            return null;
    }

    private String getAvatarUrl(User user) {
        String avatarUrl = user.getAvatarUrl();
        if (TextUtils.isEmpty(avatarUrl)) {
            String gravatarId = user.getGravatarId();
            if (TextUtils.isEmpty(gravatarId))
                gravatarId = GravatarUtils.getHash(user.getEmail());
            avatarUrl = getAvatarUrl(gravatarId);
        }
        return avatarUrl;
    }

    private String getAvatarUrl(CommitUser user) {
        return getAvatarUrl(GravatarUtils.getHash(user.getEmail()));
    }

    private String getId(final User user) {
        if (user == null)
            return null;

        int id = user.getId();
        if (id > 0)
            return Integer.toString(id);

        String gravatarId = user.getGravatarId();
        if (!TextUtils.isEmpty(gravatarId))
            return gravatarId;
        else
            return GravatarUtils.getHash(user.getEmail());
    }

    /**
     * Bind view to image at URL
     *
     * @param view The ImageView that is to display the user's avatar.
     * @param user A User object that points to the desired user.
     */
    public void bind(final ImageView view, final User user) {
        p.load(user.getAvatarUrl()).placeholder(R.drawable.gravatar_icon).resize(avatarSize, avatarSize).into(view);
    }

    /**
     * Bind view to image at URL
     *
     * @param view The ImageView that is to display the user's avatar.
     * @param user A CommitUser object that points to the desired user.
     * @return this helper
     */
    public AvatarLoader bind(final ImageView view, final CommitUser user) {
        if (user == null)
            return setImage(loadingAvatar, view);

        final String avatarUrl = getAvatarUrl(user);

        if (TextUtils.isEmpty(avatarUrl))
            return setImage(loadingAvatar, view);

        final String userId = user.getEmail();

        BitmapDrawable loadedImage = loaded.get(userId);
        if (loadedImage != null)
            return setImage(loadedImage, view);

        setImage(loadingAvatar, view, userId);
        fetchAvatarTask(avatarUrl, userId, view).execute();

        return this;
    }

    /**
     * Bind view to image at URL
     *
     * @param view The ImageView that is to display the user's avatar.
     * @param contributor A Contributor object that points to the desired user.
     * @return this helper
     */
    public AvatarLoader bind(final ImageView view, final Contributor contributor) {
        if (contributor == null)
            return setImage(loadingAvatar, view);

        final String avatarUrl = contributor.getAvatarUrl();

        if (TextUtils.isEmpty(avatarUrl))
            return setImage(loadingAvatar, view);

        final String contributorId = contributor.getLogin();

        BitmapDrawable loadedImage = loaded.get(contributorId);
        if (loadedImage != null)
            return setImage(loadedImage, view);

        setImage(loadingAvatar, view, contributorId);
        fetchAvatarTask(avatarUrl, contributorId, view).execute();

        return this;
    }

    /**
     * Bind view to image at URL
     *
     * @param view The ImageView that is to display the user's avatar.
     * @param user a SearchUser object that refers to the desired user.
     * @return this helper
     */
    public AvatarLoader bind(final ImageView view, final SearchUser user) {
        if (user == null)
            return setImage(loadingAvatar, view);

        final String avatarUrl = getAvatarUrl(user.getGravatarId());

        if (TextUtils.isEmpty(avatarUrl))
            return setImage(loadingAvatar, view);

        final String userId = user.getId();

        BitmapDrawable loadedImage = loaded.get(userId);
        if (loadedImage != null)
            return setImage(loadedImage, view);

        setImage(loadingAvatar, view, userId);
        fetchAvatarTask(avatarUrl, userId, view).execute();

        return this;
    }

    private FetchAvatarTask fetchAvatarTask(final String avatarUrl,
            final String userId, final ImageView view) {
        return new FetchAvatarTask(context) {

            @Override
            public BitmapDrawable call() throws Exception {
                if (!userId.equals(view.getTag(R.id.iv_avatar)))
                    return null;

                final String avatarFilename = getAvatarFilenameForUrl(avatarUrl);
                final BitmapDrawable image = getImageBy(userId, avatarFilename);
                if (image != null)
                    return image;
                else
                    return fetchAvatar(avatarUrl, userId, avatarFilename);
            }

            @Override
            protected void onSuccess(final BitmapDrawable image) throws Exception {
                if (image == null)
                    return;
                loaded.put(userId, image);
                if (userId.equals(view.getTag(R.id.iv_avatar)))
                    setImage(image, view);
            }
        };
    }
}
