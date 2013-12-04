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
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.util.Log;

import com.google.ads.*;

import java.util.List;
import java.util.ArrayList;

public class SoFActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Global gbl;

    private IabHelper mIabHelper;

    public SharedPreferences        mSettings;
    public SharedPreferences.Editor mEditor;

    private Resources mRes;

    private SurfaceView mSurfaceView;
    private TextView mDrinkCounter;
    private LinearLayout mFailBorder;
    private TextView mFailMessage;
    private AdView mAdView;

    private Menu mMenu;

    private int mResult = 0;
    private Boolean mFail = false;
    private int mCounter = 0;
    private int mAdFreeThreshold = 6;

    private String mPubKey;

    private Boolean mAdFree = true;

    private List<String> mSkuList;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gbl = (Global) getApplication();

        setContentView(R.layout.main);

        // View Handles
        mSurfaceView = (SurfaceView) findViewById(R.id.canvas);

        mDrinkCounter = (TextView) findViewById(R.id.drink_counter);
        mFailBorder = (LinearLayout) findViewById(R.id.fail_border);
        mFailMessage = (TextView) findViewById(R.id.fail_message);

        mAdView = (AdView) findViewById(R.id.adView);

        mMenu = null;

        // Resource Handle
        mRes = getResources();

        // Register for Shared Preferences
        mSettings = getSharedPreferences(mRes.getString(R.string.prefs), Context.MODE_PRIVATE);
        mSettings.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(mSettings, null);
        mEditor = mSettings.edit();

        // Clear Ad Preference (Testing purposes only)
        // mEditor.putInt(mRes.getString(R.string.ad_pref), 0);
        // mEditor.commit();


        /* ---------------------------
            ADS AND IN-APP PURCHASING
           --------------------------- */
        final IabHelper.OnConsumeFinishedListener mConsumeFinishedListener
                = new IabHelper.OnConsumeFinishedListener() {
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                if(result.isSuccess()) {
                    Log.v("SoFActivity", "Purchase consumption succeeded");
                }
            }
        };

        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if(result.isFailure()) {
                    // handle failure
                    Log.v("SoFActivity", "onQueryInventoryFinished FAILED");
                } else {
                    mAdFree = inventory.hasPurchase(Global.SKU_NO_ADS);
                    if(!mAdFree) {
                        if(mSettings.getInt(mRes.getString(R.string.ad_pref), 0) != 1) {
                            mAdView.loadAd(new AdRequest());

                            mEditor.putInt(mRes.getString(R.string.ad_pref), 0);
                            mEditor.commit();
                        }
                    } else {
                        // set shared preference
                        mEditor.putInt(mRes.getString(R.string.ad_pref), 1);
                        mEditor.commit();
                    }


                    /*
                    // For testing  purposes.  Consume test purchase at startup.
                    Boolean testPurchase = inventory.hasPurchase("android.test.purchased");
                    if(testPurchase) {
                        mIabHelper.consumeAsync(inventory.getPurchase("android.test.purchased"),
                                    mConsumeFinishedListener);
                        mEditor.putInt(mRes.getString(R.string.ad_pref), 0);
                        mEditor.commit();
                    }
                    */
                }
            }
        };

        mPubKey = Global.getPubKey();
        mIabHelper = new IabHelper(this, mPubKey);
        mIabHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if(result.isSuccess()) {
                    mIabHelper.queryInventoryAsync(mGotInventoryListener);
                } else {
                    Log.v("IAB", "IAB Setup Failed");
                }
            }
        });

        mSkuList = new ArrayList<String>();
        mSkuList.add(Global.SKU_NO_ADS);
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
    public void onResume() {
        super.onResume();
        mSurfaceView.onResume();

        setImersiveMode();
    }

    @Override
    public void onPause() {
        super.onPause();
        mSurfaceView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_activity, menu);

        // Check if ad have been removed
        if(mSettings.getInt( getString(R.string.ad_pref), 0) == 1) {
            MenuItem adItem = mMenu.findItem(R.id.menu_ads);
            adItem.setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_tutorial:
                Intent tutIntent = new Intent(this, TutorialActivity.class);
                startActivity(tutIntent);
                break;
            case R.id.menu_ads:
                final Context context = this;
                IabHelper.QueryInventoryFinishedListener
                        mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener () {
                    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                        if(result.isFailure()) {
                            // handle failure
                            return;
                        }

                        String price = inventory.getSkuDetails(Global.SKU_NO_ADS).getPrice();

                        Intent purchaseIntent = new Intent(context, PurchaseActivity.class);
                        purchaseIntent.putExtra("ad_price", price);
                        startActivity(purchaseIntent);
                    }
                };
                mIabHelper.queryInventoryAsync(true, mSkuList, mQueryFinishedListener);
                break;
            case R.id.menu_about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);
                break;
        }

        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // Check Fail Condition
        mFail = sharedPreferences.getBoolean( getString(R.string.fail_pref), false);
        if(mFail) {
            mFailBorder.setVisibility(LinearLayout.VISIBLE);
        } else {
            mFailBorder.setVisibility(LinearLayout.GONE);
        }

        // Update Drink Counter
        mCounter = sharedPreferences.getInt( getString(R.string.counter_pref), -1);
        updateCounter();


        // Check ad preference
        if(sharedPreferences.getInt( getString(R.string.ad_pref), 0) == 1) {
            // remove ad
            mAdView.setVisibility(View.GONE);

            // remove purchase menu option
            if(mMenu != null){
                MenuItem adItem = mMenu.findItem(R.id.menu_ads);
                adItem.setEnabled(false);
            }
        }
    }

    private void updateCounter() {
        if(mFail) {
            mDrinkCounter.setVisibility(LinearLayout.GONE);

            mFailMessage.setVisibility(TextView.VISIBLE);
            if(mCounter == 1) {
                mFailMessage.setText("Take a Drink");
            } else {
                mFailMessage.setText("Take " + String.valueOf(mCounter) + " Drinks");
            }
        } else {
            mFailMessage.setVisibility(TextView.GONE);

            mDrinkCounter.setVisibility(LinearLayout.VISIBLE);
            mDrinkCounter.setText(String.valueOf(mCounter));
        }
    }

    private void resetCounters() {
        mCounter = 0;
        updateCounter();
    }

    private void setImersiveMode() {
        View decorView = getWindow().getDecorView();
        int uiOptions = decorView.getSystemUiVisibility();
        int newUiOptions = uiOptions;

        newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(newUiOptions);
    }
}

