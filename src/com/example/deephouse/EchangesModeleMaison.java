package com.example.deephouse;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.hardware.Sensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.adapter.ActuatorAdapter;
import com.h4313.deephouse.adapter.RoomAdapter;
import com.h4313.deephouse.adapter.SensorAdapter;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.SensorType;
import com.h4313.deephouse.util.DecToHexConverter;

/**
 * Created by Steevens on 30/01/14.
 */
public class EchangesModeleMaison
{
	// Todo : better use WebServices URL from "Constant" (Utils)
	private static String url = "http://www.paul-molins.fr/deephouse/post.php";
	private static String url_maison = "http://paul-molins.fr/deephouse/houseModel.json";
	
    /**
     * Informer le modele de la maison d'une action utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur a actionne. EX : temperature, humidite...
     * @param valeurCapteur
     * @return
     */
	
	public static String recupererMaison()
	{
        try{
        	//Arguments formatting
        	return HttpCommunication.sendPost(url_maison, new ArrayList<NameValuePair>(0)); //from HttpCommunication
        }
        catch (Exception e) {
			e.printStackTrace();
			return "Fail";
		}
	}

    public static void actionUtilisateur(int numPiece, int objetAction, float valeurCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("typeAction", Integer.toString(objetAction)));
	    nameValuePairs.add(new BasicNameValuePair("valeur", Float.toString(valeurCapteur)));
        
        try {
        	HttpCommunication.sendPost(url,nameValuePairs);
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Informer le modele de la maison de l'ajout d'un capteur.
     * @param numPiece
     * @param idCapteur : numero de serie du capteur (saisit par l'utilisateur).
     * @param typeCapteur : nature du capteur (capteur de presence, de lumiere...) identifie par un entier.
     * @return
     */
    public static void ajoutCapteur(int numPiece, int idCapteur, int typeCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("capteur", Integer.toString(idCapteur)));
	    nameValuePairs.add(new BasicNameValuePair("type", Integer.toString(typeCapteur)));
	    
        try {
        	HttpCommunication.sendPost(url,nameValuePairs);
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
    }
    
     /**
     * Informer le modele de la maison de l'ajout d'un actioneur.
     * @param numPiece
     * @param idActioneur : numero de serie de l'actioneur (saisi par l'utilisateur).
     * @param typeCapteur : nature de l'actioneur (capteur de presence, de lumiere...) identifie par un entier.
     * @return
     */
    public static void ajoutActioneur(int numPiece, int idActioneur, int typeCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("actioneur", Integer.toString(idActioneur)));
	    nameValuePairs.add(new BasicNameValuePair("type", Integer.toString(typeCapteur)));
	    
        try {
        	HttpCommunication.sendPost(url,nameValuePairs);
		} 
        catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * reception periodique des valeurs des capteurs depuis le modele de la maison
     * @param jsonHouse
     * @throws JSONException
     */
    public static void majHouseModel()
    {
        String jHouse = recupererMaison();
        
        try {
            House maison = getHouseFromJson(jHouse); // throws an exception if jHouse isn't a valid representation of a House
            House.setInstance(maison);
        	System.out.println("House update verification :" + House.getInstance().getRooms().get(0).getSensors().toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    //TODO : To be deleted soon, kept as an example 
    public static void testCommunication()
    {
        String jsonResponse;

        try{
        	//Arguments formatting
    	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	    nameValuePairs.add(new BasicNameValuePair("id", "12345"));
    	    nameValuePairs.add(new BasicNameValuePair("fakepassword", "ilovemum"));
    	    jsonResponse = HttpCommunication.sendPost(url, nameValuePairs); //from HttpCommunication
            JSONObject j = new JSONObject(jsonResponse); 
            System.out.println(j.toString());
	    }
	    catch (Exception e) {
			e.printStackTrace();
	    }
    }
    
    public static House getHouseFromJson(String jHouse) throws Exception
    {
    	final GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Room.class, new RoomAdapter());
        builder.registerTypeAdapter(Sensor.class, new SensorAdapter());
        builder.registerTypeAdapter(Actuator.class, new ActuatorAdapter());
        
        final Gson gson = builder.create();
        House house = gson.fromJson(jHouse, House.class);
        
        return house;
    }
}