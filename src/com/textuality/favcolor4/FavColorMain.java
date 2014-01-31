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

import yuku.ambilwarna.AmbilWarnaDialog;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.AccountPicker;

public class FavColorMain extends FragmentActivity {

    public static final String TAG = "FavColor";

    private static final int PICKER_REQUEST_CODE = 44312;
    static final int AUTHUTIL_REQUEST_CODE = 43594;

    private String mEmail = null;
    private FavoriteColor mColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favcolor);

        // still buggy after all these years
        System.setProperty("http.keepAlive", "false");

        mColor = new FavoriteColor(this, false);

        TextView t = (TextView) findViewById(R.id.message);
        t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mEmail == null) {
                    return;
                }
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(FavColorMain.this, 0, new AmbilWarnaDialog.OnAmbilWarnaListener() {

                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        mColor.set(color);
                        Updater.update(FavColorMain.this, color, mEmail);
                    }

                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                    }
                });
                dialog.show();
            }
        });

        t = (TextView) findViewById(R.id.switcher);
        t.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                pickAndGo();                
            }
        });

        pickAndGo();
    }

    private void pickAndGo() {
        Intent intent = AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, 
                false, null, null, null, null);  
        startActivityForResult(intent, PICKER_REQUEST_CODE);        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            mEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
            Fetcher.fetch(this, mColor, mEmail);
        } else if (requestCode == AUTHUTIL_REQUEST_CODE && resultCode == RESULT_OK) {
            Fetcher.fetch(this, mColor, mEmail);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}
