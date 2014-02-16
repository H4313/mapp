package com.example.deephouse;

import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.exceptions.DeepHouseDuplicateException;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.housemodel.RoomConstants;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.Constant;
import com.h4313.deephouse.util.DecToHexConverter;

public class HouseActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
    public static final String PREFS_NAME = "DeepHousePrefs";
    static TextView TEST;
    public Handler handler;
    
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
        
        //localHouseInstanciation();
        
        //MAJ vue periodique
        handler = new Handler();
        handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);
	}
	
	private final Runnable refresh = new Runnable()
	{
		@Override
	    public void run()
	    {
	        EchangesModeleMaison.majHouseModel(); // Recuperation du Json House (modele a jour de la maison) sur le serveur
	        updateView(); // MAJ de la vue avec ce nouveau modele.
			handler.postDelayed(refresh, Constant.MILLISECONDS_TILL_REFRESH);            
	    }
	};//runnable

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

	
	// MAJ Affichage de la temperatures et de la presence humaine pour chaque pieces de la maison.
	private void updateView()
	{
		House house = House.getInstance();
		
		// recuperation pour chaque piece de leurs textViews associes a Temeprature et Presence
		for(Room r : house.getRooms())
		{
			int textViewTemperature;
			int textViewPresence;
			
			switch(r.getIdRoom())
			{
				case RoomConstants.ID_LIVING_ROOM : 
					textViewTemperature = R.id.textViewLivingRoomTemperature;
					textViewPresence = R.id.textViewLivingRoomPresence;
					break;
				case RoomConstants.ID_KITCHEN : 
					textViewTemperature = R.id.textViewKitchenTemperature;
					textViewPresence = R.id.textViewKitchenPresence;
					break;
				case RoomConstants.ID_BATHROOM : 
					textViewTemperature = R.id.textViewBathroomTemperature;
					textViewPresence = R.id.textViewBathroomPresence;
					break;
				case RoomConstants.ID_BEDROOM : 
					textViewTemperature = R.id.textViewBedroomTemperature;
					textViewPresence = R.id.textViewBedroomPresence;
					break;
				case RoomConstants.ID_OFFICE : 
					textViewTemperature = R.id.textViewOfficeTemperature;
					textViewPresence = R.id.textViewOfficePresence;
					break;
				case RoomConstants.ID_CORRIDOR : 
					textViewTemperature = R.id.textViewCorridorTemperature;
					textViewPresence = R.id.textViewCorridorPresence;
					break;
				default :
					textViewTemperature = -1;
					textViewPresence = -1;
					System.out.println("Erreur rencontree : tentative de MAJ de la piece n" + r.getIdRoom() + " inexistante.");
					return;
			}
			
			// MAJ si le capteur de temperature / presence existe
			for(String key : r.getSensors().keySet())
			{
				if(r.getSensors().get(key).getType() == SensorType.TEMPERATURE)
				{
					System.out.println("Piece " + r.getIdRoom() + " : temperature = "  + r.getSensors().get(key).getLastValue());
					
					TextView textView = (TextView) findViewById(textViewTemperature);
					textView.setText(r.getSensors().get(key).getLastValue() + "°C");
				}
				else if(r.getSensors().get(key).getType() == SensorType.PRESENCE)
				{
					System.out.println("Piece " + r.getIdRoom() + " : presence = "  + r.getSensors().get(key).getLastValue());

					TextView textView = (TextView) findViewById(textViewPresence);
					textView.setText(r.getSensors().get(key).getLastValue()+"");
				}
			}
		}
	}
		
	
    /**
     * To be used for debug only
     */
    public static void localHouseInstanciation()
    {
    	List<Room> rooms = House.getInstance().getRooms();
        int id = 0;
        for(Room room : rooms)
        {
	        try{
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.TEMPERATURE);
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.WINDOW);
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.LIGHT);
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.DOOR);
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.FLAP);
	            room.addSensor(DecToHexConverter.decToHex(id++), SensorType.PRESENCE);
	
	            room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.RADIATOR);
	            room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.WINDOWCLOSER);
	            room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.LIGHTCONTROL);
	            room.addActuator(DecToHexConverter.decToHex(id++), ActuatorType.DOORCONTROL);
            }
            catch (DeepHouseDuplicateException e)
            {
            	e.printStackTrace();
            }
            catch (Exception e)
            {
            	e.printStackTrace();
            }
        }
    }
}