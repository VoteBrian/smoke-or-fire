package net.votebrian.games.sof;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class SoFActivity extends Activity
{
    Global gbl;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);

        // if this works, the app should start with a red-ish SurfaceView background
        gbl = (Global) getApplication();
        gbl.setSomething(1);  // blue = 0, red = 1, green = 2 in GLESRenderer.

        Spinner s1 = (Spinner) findViewById(R.id.blend_func);
        ArrayAdapter<CharSequence> sAdapter1 = ArrayAdapter.createFromResource(this, R.array.blends, android.R.layout.simple_spinner_item);
        sAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(sAdapter1);

        s1.setOnItemSelectedListener(
            new OnItemSelectedListener(){
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    gbl.setBlendFunction(position);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    // chill
                }
        });

        Spinner s2 = (Spinner) findViewById(R.id.tex_env);
        ArrayAdapter<CharSequence> sAdapter2 = ArrayAdapter.createFromResource(this, R.array.tex_envs, android.R.layout.simple_spinner_item);
        sAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s2.setAdapter(sAdapter2);

        s2.setOnItemSelectedListener(
            new OnItemSelectedListener(){
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    gbl.setTextureFunction(position);
                }

                public void onNothingSelected(AdapterView<?> parent) {
                    // chill
                }
        });
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
                gbl.reset();
                return true;
        }

        return true;
    }
}

