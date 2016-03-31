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
package com.github.mobile.accounts;

import static android.accounts.AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE;
import static android.accounts.AccountManager.KEY_ACCOUNT_NAME;
import static android.accounts.AccountManager.KEY_ACCOUNT_TYPE;
import static android.accounts.AccountManager.KEY_AUTHTOKEN;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;
import static android.accounts.AccountManager.KEY_INTENT;
import static com.github.mobile.accounts.AccountConstants.ACCOUNT_TYPE;
import static com.github.mobile.accounts.AccountConstants.APP_NOTE;
import static com.github.mobile.accounts.LoginActivity.PARAM_AUTHTOKEN_TYPE;
import static com.github.mobile.accounts.LoginActivity.PARAM_USERNAME;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.github.mobile.DefaultClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.eclipse.egit.github.core.Authorization;
import org.eclipse.egit.github.core.service.OAuthService;

class AccountAuthenticator extends AbstractAccountAuthenticator {

    private static final String TAG = "GitHubAccountAuth";

    private static final List<String> SCOPES = Arrays.asList("repo", "user", "gist");

    private Context context;

    public AccountAuthenticator(final Context context) {
        super(context);

        this.context = context;
    }

    /**
     * The user has requested to add a new account to the system. We return an
     * intent that will launch our login screen if the user has not logged in
     * yet, otherwise our activity will just pass the user's credentials on to
     * the account manager.
     */
    @Override
    public Bundle addAccount(final AccountAuthenticatorResponse response,
            final String accountType, final String authTokenType,
            final String[] requiredFeatures, final Bundle options)
            throws NetworkErrorException {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final Bundle options) {
        return null;
    }

    @Override
    public Bundle editProperties(final AccountAuthenticatorResponse response,
            final String accountType) {
        return null;
    }

    private static boolean isValidAuthorization(final Authorization auth,
            final List<String> requiredScopes) {
        if (auth == null)
            return false;

        List<String> scopes = auth.getScopes();
        return scopes != null && scopes.containsAll(requiredScopes);
    }

    private Intent createLoginIntent(final AccountAuthenticatorResponse response) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, ACCOUNT_TYPE);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        return intent;
    }

    /**
     * Get existing authorization for this app
     *
     * @param service
     * @return token or null if none found
     * @throws IOException
     */
    public static String getAuthorization(final OAuthService service) throws IOException {
        for (Authorization auth : service.getAuthorizations())
            if (isValidAuthorization(auth, SCOPES))
                return auth.getToken();
        return null;
    }

    /**
     * Create authorization for this app
     *
     * @param service
     * @return created token
     * @throws IOException
     */
    public static String createAuthorization(final OAuthService service) throws IOException {
        Authorization auth = new Authorization();
        auth.setNote(APP_NOTE + " " + System.currentTimeMillis());
        auth.setScopes(SCOPES);
        auth = service.createAuthorization(auth);
        return auth != null ? auth.getToken() : null;
    }

    @Override
    public Bundle getAuthToken(final AccountAuthenticatorResponse response,
            final Account account, final String authTokenType,
            final Bundle options) throws NetworkErrorException {
        Log.d(TAG, "Retrieving OAuth2 token");

        final Bundle bundle = new Bundle();

        if (!ACCOUNT_TYPE.equals(authTokenType))
            return bundle;

        AccountManager am = AccountManager.get(context);
        String password = am.getPassword(account);
        if (TextUtils.isEmpty(password)) {
            bundle.putParcelable(KEY_INTENT, createLoginIntent(response));
            return bundle;
        }

        DefaultClient client = new DefaultClient();
        client.setCredentials(account.name, password);
        OAuthService service = new OAuthService(client);

        String authToken;
        try {
            authToken = getAuthorization(service);
            if (TextUtils.isEmpty(authToken))
                authToken = createAuthorization(service);
        } catch (IOException e) {
            Log.e(TAG, "Authorization retrieval failed", e);
            throw new NetworkErrorException(e);
        }

        if (TextUtils.isEmpty(authToken))
            bundle.putParcelable(KEY_INTENT, createLoginIntent(response));
        else {
            bundle.putString(KEY_ACCOUNT_NAME, account.name);
            bundle.putString(KEY_ACCOUNT_TYPE, ACCOUNT_TYPE);
            bundle.putString(KEY_AUTHTOKEN, authToken);
            am.clearPassword(account);
        }
        return bundle;
    }

    @Override
    public String getAuthTokenLabel(final String authTokenType) {
        if (ACCOUNT_TYPE.equals(authTokenType))
            return authTokenType;
        else
            return null;
    }

    @Override
    public Bundle hasFeatures(final AccountAuthenticatorResponse response,
            final Account account, final String[] features)
            throws NetworkErrorException {
        final Bundle result = new Bundle();
        result.putBoolean(KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(
            final AccountAuthenticatorResponse response, final Account account,
            final String authTokenType, final Bundle options) {
        final Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra(PARAM_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
        if (!TextUtils.isEmpty(account.name))
            intent.putExtra(PARAM_USERNAME, account.name);
        final Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_INTENT, intent);
        return bundle;
    }
}
