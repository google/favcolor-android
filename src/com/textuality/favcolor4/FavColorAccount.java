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

import org.json.JSONObject;

public class FavColorAccount {
    private final String mEmail;
    private final String mDisplayName;
    private final String mPhotoUrl;
    private final int mColor;
    
    public FavColorAccount(JSONObject json) {
        mEmail = json.optString("email", null);
        mDisplayName = json.optString("displayName", null);
        mPhotoUrl = json.optString("photUrl", null);
        String colorHex = json.optString("color", null);
        if (colorHex == null)
            mColor = -1;
        else {
            mColor = Integer.parseInt(colorHex, 16) | 0xff000000;
        }
    }
    
    public String email() {
        return mEmail;
    }
    public String displayName() {
        return mDisplayName;
    }
    public String mPhotoUrl() {
        return mPhotoUrl;
    }
    public int color() {
        return mColor;
    }

    public String toString() {
        return mEmail + "/" + mDisplayName;
    }
}
