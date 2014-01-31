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

import java.util.Random;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

public class FavoriteColor {

    private static final String PREF = "FavColor";
    private static final String PREF_NAME = "displayName";
    private static final String PREF_COLOR = "color";

    private final Activity mActivity;
    private final SharedPreferences mPrefs;
    private final Random mRand;
    private final boolean mGitKit;

    public FavoriteColor(Activity activity, boolean gitkit) {
        mActivity = activity;
        mPrefs = activity.getSharedPreferences(PREF, 0);
        mRand = new Random();
        mGitKit = gitkit;
    }

    public void set(int color) {
        if (color != -1) {
            SharedPreferences.Editor editor = mPrefs.edit();
            rememberColor(editor, color);
            editor.commit();
            redraw();
        }
    }

    public void fromAccount(FavColorAccount account) {
        SharedPreferences.Editor editor = mPrefs.edit();
        if (account.displayName() != null) {
            editor.putString(PREF_NAME, account.displayName());
        } 
        rememberColor(editor, account.color());
        if (account.color() != -1) {
            editor.putInt(PREF_COLOR, account.color());
        }
        editor.commit();
        redraw();
    }
    
    private void rememberColor(SharedPreferences.Editor editor, int color) {
        if (color != -1) {
            editor.putInt(PREF_COLOR, color);
        }
    }
    
    private void redraw() {
        String message = mActivity.getString(R.string.hello);
        int color = mPrefs.getInt("color", -1);
        String name = mPrefs.getString(PREF_NAME, null);
        if (name != null) {
            message += " " + name + "!\n";
        } else {
            message += "!\n";
        }
        if (color != -1) {
            message += mActivity.getString(R.string.your_color);
        } else {
            message += mActivity.getString(R.string.unknown_color);
            int r = mRand.nextInt(256), g = mRand.nextInt(256), b = mRand.nextInt(256);
            color = (0xff << 24) | (r << 16) | (g << 8) | b;
        }
        View background = mActivity.findViewById(mGitKit ? R.id.gkcontainer : R.id.container);
        background.setBackgroundColor(color);
        TextView readout = (TextView) mActivity.findViewById(mGitKit ? R.id.gkmessage : R.id.message);
        readout.setText(message);
    }
}
