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
