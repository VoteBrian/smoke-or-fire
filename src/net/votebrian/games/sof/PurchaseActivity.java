/*
 *  Copyright 2012 Brian Flores
 *
 *  This file is part of SmokeOrFire.
 *
 *  SmokeOrFire is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SmokeOrFire is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SmokeOrFire.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.votebrian.games.sof;

import android.app.Activity;
import android.content.Intent;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import android.util.Log;

public class PurchaseActivity extends Activity {
    private Context mCtx;
    private Activity mActivity;

    public SharedPreferences         mSettings;
    public SharedPreferences.Editor mEditor;

    private Resources mRes;

    private Button mAdPriceBtn;
    private String mAdPrice;

    private String mPubKey;
    private IabHelper mIabHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCtx = this;
        mActivity = this;

        // Shared Preferences Setup
        mRes = mCtx.getResources();

        mSettings = mCtx.getSharedPreferences( mRes.getString(R.string.prefs), Context.MODE_PRIVATE);
        mEditor = mSettings.edit();

        // Connect to Google Play app
        mPubKey = Global.getPubKey();
        mIabHelper = new IabHelper(this, mPubKey);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if(result.isSuccess()) {
                    setContentView(R.layout.purchase);


                    // Handles to Views
                    mAdPriceBtn = (Button) findViewById(R.id.ads_purchase);

                    // Get Price of Purchases
                    Bundle extras = getIntent().getExtras();
                    if(extras != null) {
                        mAdPrice = extras.getString("ad_price");
                    }

                    mAdPriceBtn.setText(mAdPrice);
                    mAdPriceBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            mIabHelper.launchPurchaseFlow(mActivity,
                                    Global.SKU_NO_ADS, 00001, mPurchaseFinishedListener, "");
                        }
                    });
                } else {
                    Log.v("PURCHASE ACTIVITY", "IAB Setup Failed");
                    Toast.makeText(mCtx, "Connection to Google Play failed...", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mIabHelper != null) {
            mIabHelper.dispose();
        }
        mIabHelper = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mIabHelper.handleActivityResult(requestCode, resultCode, data);
    }


    final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            if(result.isFailure()) {
                Log.v("***PURCHASE***", "Error purchasing: " + result);
                return;
            } else {
                Log.v("***PURCHASE***", "Purchase succeeded: " + result);
            }

            if (purchase.getSku().equals(Global.SKU_NO_ADS)) {
                // set shared preference
                mEditor.putInt(mRes.getString(R.string.ad_pref), 1);
                mEditor.commit();

                finish();
            } else if (purchase.getSku().equals("android.test.purchased")) {
                // mEditor.putInt(mRes.getString(R.string.ad_pref), 1);
                // mEditor.commit();

                finish();
            } else {
                Log.v("***PURCHASE***", "Unknown SKU: " + purchase.getSku());
                finish();
            }
        }
    };
}