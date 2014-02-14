package com.example.deephouse;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.adapter.ActuatorAdapter;
import com.h4313.deephouse.adapter.RoomAdapter;
import com.h4313.deephouse.adapter.SensorAdapter;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;

public class HouseActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
    public static final String PREFS_NAME = "DeepHousePrefs";
    static TextView TEST;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house);

        //Getting intent content
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        //Creating local house model
        String jHouse=recupererMaison();
        System.out.println("jHouse" + jHouse);
    	
    	//Instantiation de la maison depuis le Json avec Gson
    	final GsonBuilder builder2 = new GsonBuilder();
        builder2.registerTypeAdapter(Room.class, new RoomAdapter());
        builder2.registerTypeAdapter(Sensor.class, new SensorAdapter());
        builder2.registerTypeAdapter(Actuator.class, new ActuatorAdapter());
        final Gson gson2 = builder2.create();
        House house = gson2.fromJson(jHouse, House.class);
        House.setInstance(house);
        System.out.println("Verif fonctionnement :" + House.getInstance().getRooms().get(0).getSensors().toString());
        
        //TODO : Fixer la MAJ vue
        //MAJ vue periodique
        /*Handler viewHandler = new Handler();
	    Runnable updateView = new Runnable{
    		@Override
    		public void run(){
    			EchangesModeleMaison.majInfosCapteurs();
    			findViewById(R.id.mainLayout).invalidate();
    			viewHandler.postDelayed(updateView, Constant.MILLISECONDS_TILL_UPDATE);
      		}*/
	    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.house, menu);
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
            case R.id.action_deconnexion:
                //Clear login informations
                SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
                SharedPreferences.Editor editor = settings.edit();
                editor.remove("ip")
                        .remove("login")
                        .remove("password")
                        .commit();
                //Relaunch application
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
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
            View rootView = inflater.inflate(R.layout.fragment_house, container, false);
            return rootView;
        }
    }

    /** Called when the user clicks on a room of the house */
    public void openConfigurationL(View view){
        openConfiguration(view,0);
    }
    public void openConfigurationK(View view){
        openConfiguration(view,1);
    }
    public void openConfigurationWC(View view){
        openConfiguration(view,2);
    }
    public void openConfigurationB1(View view){
        openConfiguration(view,3);
    }
    public void openConfigurationB2(View view){
        openConfiguration(view,4);
    }
    public void openConfigurationC(View view){
        openConfiguration(view,5);
    }

    public void openConfiguration(View view, Integer position) {
        Intent intent = new Intent(this, HouseConfigActivity.class); //creates configuration activity
        //Putting text field content in intent
        intent.putExtra(EXTRA_MESSAGE, position);
        startActivity(intent);
    }

	public static String recupererMaison()
	{
		String url = "http://10.0.2.2:8080/deepHouse/rest/houseModel";
        ParseJSON jsonParser = new ParseJSON(url);
        String maison = jsonParser.getJson();
        return maison;
	}
	
	private static void updateView(){
		//TEST = (TextView)findViewById(R.id.textViewLivingRoom);
	}
}