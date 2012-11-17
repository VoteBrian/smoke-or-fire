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
import android.widget.LinearLayout;
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
    private LinearLayout mFailBorder;

    private int mResult = 0;
    private Boolean mFail = false;
    private int mCounter = 0;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gbl = (Global) getApplication();

        setContentView(R.layout.main);

        // View Handles
        mTxtNumDrinks = (TextView) findViewById(R.id.txt_num_drinks);
        mFailBorder = (LinearLayout) findViewById(R.id.fail_border);

        // Resource Handle
        mRes = getResources();

        // Register for Shared Preferences
        mSettings = getSharedPreferences(mRes.getString(R.string.prefs), Context.MODE_PRIVATE);
        mSettings.registerOnSharedPreferenceChangeListener(this);
        onSharedPreferenceChanged(mSettings, null);
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
    }

    private void updateCounter() {
        if(mFail) {
            mTxtNumDrinks.setText("Drink " + String.valueOf(mCounter));
        } else {
            mTxtNumDrinks.setText(String.valueOf(mCounter));
        }
    }

    private void resetCounters() {
        mCounter = 0;
        updateCounter();
    }
}

