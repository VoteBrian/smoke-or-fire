package net.votebrian.games.sof;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.util.Log;

public class SoFActivity extends Activity
        implements SharedPreferences.OnSharedPreferenceChangeListener {
    private Global gbl;

    public SharedPreferences        mSettings;
    public SharedPreferences.Editor mEditor;

    private Resources mRes;

    private TextView mTxtNumDrinks;

    private int mResult = 0;

    private int mCounter = 0;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gbl = (Global) getApplication();

        setContentView(R.layout.main);

        mTxtNumDrinks = (TextView) findViewById(R.id.txt_num_drinks);

        // need a handle to resources
        mRes = getResources();

        mSettings = getSharedPreferences(mRes.getString(R.string.prefs), Context.MODE_PRIVATE);
        mSettings.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(mSettings, null);

        // create onClickListeners
        /*
        mBtnSmoke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // clear table if not already cleared
                if(gbl.getClearable() == 1) {
                    gbl.burnTable();
                    gbl.setClearable(0);
                }

                mResult = gbl.deal(gbl.SMOKE);

                if(mResult == Global.GOOD) {
                    mCounter++;
                    updateCounter();
                    if(mCounter == 1) {     // first correct pick
                        // make higher and lower buttons visible
                        mBtnHigher.setVisibility(View.VISIBLE);
                        mBtnLower.setVisibility(View.VISIBLE);
                    }
                } else {
                    mCounter = 0;
                    updateCounter();
                    gbl.setClearable(1);

                    // make all buttons invisible
                    // mBtnSmoke.setVisibility(View.INVISIBLE);
                    // mBtnFire.setVisibility(View.INVISIBLE);
                    mBtnHigher.setVisibility(View.INVISIBLE);
                    mBtnLower.setVisibility(View.INVISIBLE);
                }
            }
        });

        mBtnFire.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // clear table if not already cleared
                if(gbl.getClearable() == 1) {
                    gbl.burnTable();
                    gbl.setClearable(0);
                }

                mResult = gbl.deal(gbl.FIRE);

                if(mResult == Global.GOOD) {
                    mCounter++;
                    updateCounter();
                    if(mCounter == 1) {     // first correct pick
                        // make higher and lower buttons visible
                        mBtnHigher.setVisibility(View.VISIBLE);
                        mBtnLower.setVisibility(View.VISIBLE);
                    }
                } else {
                    mCounter = 0;
                    updateCounter();
                    gbl.setClearable(1);

                    // make all buttons invisible
                    // mBtnSmoke.setVisibility(View.INVISIBLE);
                    // mBtnFire.setVisibility(View.INVISIBLE);
                    mBtnHigher.setVisibility(View.INVISIBLE);
                    mBtnLower.setVisibility(View.INVISIBLE);
                }
            }
        });

        mBtnHigher.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // clear table if not already cleared
                if(gbl.getClearable() == 1) {
                    gbl.burnTable();
                    gbl.setClearable(0);
                }

                mResult = gbl.deal(gbl.HIGHER);

                if(mResult == Global.GOOD || mResult == Global.SOCIAL) {
                    mCounter++;
                    updateCounter();
                    if(mCounter == 1) {     // first correct pick
                        // make higher and lower buttons visible
                        mBtnHigher.setVisibility(View.VISIBLE);
                        mBtnLower.setVisibility(View.VISIBLE);
                    }
                } else if(mResult == Global.BAD) {
                    mCounter = 0;
                    updateCounter();
                    gbl.setClearable(1);

                    // make all buttons invisible
                    // mBtnSmoke.setVisibility(View.INVISIBLE);
                    // mBtnFire.setVisibility(View.INVISIBLE);
                    mBtnHigher.setVisibility(View.INVISIBLE);
                    mBtnLower.setVisibility(View.INVISIBLE);
                } else {
                }
            }
        });

        mBtnLower.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // clear table if not already cleared
                if(gbl.getClearable() == 1) {
                    gbl.burnTable();
                    gbl.setClearable(0);
                }

                mResult = gbl.deal(gbl.LOWER);

                if(mResult == Global.GOOD || mResult == Global.SOCIAL) {
                    mCounter++;
                    updateCounter();
                    if(mCounter == 1) {     // first correct pick
                        // make higher and lower buttons visible
                        mBtnHigher.setVisibility(View.VISIBLE);
                        mBtnLower.setVisibility(View.VISIBLE);
                    }
                } else if(mResult == Global.BAD) {
                    mCounter = 0;
                    updateCounter();
                    gbl.setClearable(1);

                    // make all buttons invisible
                    // mBtnSmoke.setVisibility(View.INVISIBLE);
                    // mBtnFire.setVisibility(View.INVISIBLE);
                    mBtnHigher.setVisibility(View.INVISIBLE);
                    mBtnLower.setVisibility(View.INVISIBLE);
                } else {
                }
            }
        });
        */
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.layout.menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_reset:
                mCounter = 0;
                updateCounter();

                gbl.reset();
                return true;
        }

        return true;
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v("ACTIVITY", "Shared Preference Changed");
        mCounter = sharedPreferences.getInt( getString(R.string.counter_pref), -1);
        updateCounter();
    }

    private void updateCounter() {
        mTxtNumDrinks.setText(String.valueOf(mCounter));
    }

    private void resetCounters() {
        mCounter = 0;
        updateCounter();

        // make the smoke and fire buttons visible
        // mBtnSmoke.setVisibility(View.VISIBLE);
        // mBtnFire.setVisibility(View.VISIBLE);
    }
}

