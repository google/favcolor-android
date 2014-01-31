/*
 * Copyright 2012-14 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.textuality.favcolor4;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;

class GetToken extends AsyncTask<Void, Void, String> {

    private static final String SCOPE = "audience:server:client_id:" + GKMain.SERVER_CLIENT_ID;

    private final AsyncTask<String, Void, Void> mCustomer;
    private final Activity mActivity;
    private final String mEmail;

    public GetToken(Activity activity, AsyncTask<String, Void, Void> customer, String email) {
        mActivity = activity;
        mCustomer = customer;
        mEmail = email;
    }

    @Override
    protected String doInBackground(Void... params) {
        String token = null;
        try {
            // if this works, token is guaranteed to be usable
            token = GoogleAuthUtil.getToken(mActivity, mEmail, SCOPE);

        } catch (UserRecoverableAuthException userAuthEx) {
            mActivity.startActivityForResult(userAuthEx.getIntent(), FavColorMain.AUTHUTIL_REQUEST_CODE);
            token = null;

        }  catch (Exception e) {
            Log.d(FavColorMain.TAG, "OOPS! " + e.getClass().toString() + "/" + e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
        return token;
    }

    @Override
    protected void onPostExecute(String token) {
        super.onPostExecute(token);
        if (token != null) {
            mCustomer.execute(token);
        }
    }
}
