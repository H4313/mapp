package com.example.deephouse;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class AddActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        //Getting the room number
        //TODO : Dynamic page title (display room name) 
        Intent intent = getIntent();
        String position = Integer.toString(intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0));
        
        //Setting sensors list
        //TODO : Get sensors types dynamically from web server 
        String[] listeTypesCapteurs = {"Temperature","Humidite","Presence"};
        Spinner listeCapteurs = (Spinner) findViewById(R.id.spinnerSensors);
        listeCapteurs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeTypesCapteurs));
        
        //Setting actuators list
        //TODO : Get actuators types dynamically from web server 
        String[] listeTypesActuateurs = {"Actuateur1","Actuateur2","Actuateur3"};
        Spinner listeActuateurs = (Spinner) findViewById(R.id.spinnerActuators);
        listeActuateurs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeTypesActuateurs));
    }

    public void endActivity(View view){
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_add, container, false);
            return rootView;
        }
    }

}
