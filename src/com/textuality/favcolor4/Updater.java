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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class Updater extends AsyncTask<String, Void, Void> {

    private final URL mFavColorSet;
    private String mProblem = null;

    private final int mColor;

    private boolean mGitkit = false;

    private Updater(int color) {
        mColor = color & 0x00ffffff;
        try {
            mFavColorSet = new URL("https://favcolor.net/set-color");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void update(Activity activity, int color, String email) {
        Updater updater = new Updater(color);
        new GetToken(activity, updater, email).execute();
    }
    
    public static void gatUpdate(int color, String token) {
        Updater updater = new Updater(color);
        updater.mGitkit = true;
        updater.execute(token);
    }
    
    @Override
    protected Void doInBackground(String... params) {
        final String token = params[0];
        String payload = (mGitkit) ? "git_token=" : "id_token=";
        payload += encode(token) + "&color=" + encode(Integer.toHexString(mColor).toUpperCase(Locale.CANADA));
        byte[] data = payload.getBytes();
        HttpURLConnection conn = null;

        OutputStream out;
        int http_status;
        try {
            conn = (HttpURLConnection) mFavColorSet.openConnection();
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(data.length);
            conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            // this opens a connection, then sends POST & headers.
            out = conn.getOutputStream(); 
            out.write(data);

            http_status = conn.getResponseCode();
            if (http_status / 100 != 2) {
                mProblem = "HTTP failure, status=" + http_status + ", " + conn.getResponseMessage();
            }
        } catch (IOException e) {
            mProblem = e.getLocalizedMessage();
        } finally {
            conn.disconnect(); // Let's practice good hygiene
        }

        return null;
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } 
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mProblem != null) {
            Log.d(FavColorMain.TAG, "Color update failed: " + mProblem);
        }
    }
}
