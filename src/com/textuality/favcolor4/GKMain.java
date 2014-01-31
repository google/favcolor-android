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

import yuku.ambilwarna.AmbilWarnaDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.identitytoolkit.client.GitkitClient;
import com.google.identitytoolkit.client.GitkitClient.SignInCallbacks;
import com.google.identitytoolkit.model.Account;
import com.google.identitytoolkit.model.IdToken;
import com.google.identitytoolkit.model.Provider;

public class GKMain extends FragmentActivity implements SignInCallbacks {

    public static final String TAG = "FavColor";

    static final String SERVER_CLIENT_ID = "183720951444.apps.googleusercontent.com";

    private static final String API_KEY = "AIzaSyABHes3BD9JVQONKH_oBlffvDmy6HD-5_w";
    private static final String WIDGET_URL = "https://favcolor.net/gat-callback";
    private static final String TOS_URL = "https://www.google.com";

    private IdToken mGatToken = null;
    private GitkitClient mGatClient;
    private FavoriteColor mColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gkfavcolor);

        // still buggy after all these years
        System.setProperty("http.keepAlive", "false");

        mColor = new FavoriteColor(this, true);

        mGatClient = GitkitClient.newBuilder(this, this, API_KEY, SERVER_CLIENT_ID, WIDGET_URL) 
                .setTosUrl(TOS_URL)
                .showProviders(Provider.GOOGLE, Provider.FACEBOOK, Provider.YAHOO)
                .build();

        mGatToken = GitkitClient.getSavedIdToken(this);

        // set up sign-in button
        Button button = (Button) findViewById(R.id.sign_in);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGatToken != null) {
                    mGatClient.signOut();
                    mGatToken = null;
                }
                mGatClient.startSignIn();
            }
        });

        // set up color picker
        TextView t = (TextView) findViewById(R.id.gkmessage);
        t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mGatToken == null) {
                    return;
                }
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(GKMain.this, 0, new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mColor.set(color);
                        Updater.gatUpdate(color, mGatToken.getTokenString());
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }
                });
                dialog.show();
            }
        });

        // if signed in
        if (mGatToken != null) {
            Fetcher.fetch(this, mColor, mGatToken.getEmail());
            button.setText(getString(R.string.switcher));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mGatClient.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public void onSignInFailed() {
        mGatToken = null;
        ((Button) findViewById(R.id.sign_in)).setText(getString(R.string.gkhello));
    }

    @Override
    public void onSignIn(IdToken idToken, Account account) {
        mGatToken = idToken;        
        ((Button) findViewById(R.id.sign_in)).setText(getString(R.string.switcher));
        Fetcher.fetch(this, mColor, mGatToken.getEmail()); 
    }
}
