/*
 * Copyright 2012 Google Inc.
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;

public class Fetcher extends AsyncTask<String, Void, Void> {

    private final FavoriteColor mColor; 
    private FavColorAccount mAccount = null;
    private int mStatus = 0;
    
    public static void fetch(Activity activity, FavoriteColor color, String email) {
        Fetcher fetcher = new Fetcher(color);
        new GetToken(activity, fetcher, email).execute();

    }

    private Fetcher(FavoriteColor color) {
        mColor = color;
    }

    @Override
    protected Void doInBackground(String... args) {
        String token = args[0];

        byte[] data = ("{ \"id-token\" :\"" + token + "\"}").getBytes();
        HttpURLConnection conn = null;
        try {
            URL get;
            try {
                get = new URL("https://favcolor.net/get-color");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            conn = (HttpURLConnection) get.openConnection();
            conn.setDoOutput(true);
            conn.setFixedLengthStreamingMode(data.length);
            conn.addRequestProperty("Content-type", "application/json");
            conn.setRequestProperty("Content-Length", Integer.toString(data.length));

            InputStream in;
            OutputStream out;
            try {
                // this opens a connection, then sends POST & headers.
                out = conn.getOutputStream(); 
                out.write(data);
                out.flush();
                out.close();

                mStatus = conn.getResponseCode();

                if (mStatus == 404) {
                    return null;
                } else if (mStatus / 100 != 2) {
                    throw new RuntimeException("HTTP Status " + mStatus + ": " + conn.getResponseMessage());
                }

                in = conn.getInputStream(); 
                data = readStream(in);

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }

            try {
                JSONObject json = new JSONObject(new String(data));
                mAccount = new FavColorAccount(json);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        if (mStatus == 200) {
            mColor.fromAccount(mAccount);
        }
    }

    private static byte[] readStream(InputStream in) 
            throws IOException {
        final byte[] buf = new byte[1024];
        int count = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
        while ((count = in.read(buf)) != -1) {
            out.write(buf, 0, count);
        }
        in.close();
        return out.toByteArray();
    }
}

