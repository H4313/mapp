package com.example.deephouse;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.adapter.ActuatorAdapter;
import com.h4313.deephouse.adapter.RoomAdapter;
import com.h4313.deephouse.adapter.SensorAdapter;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;
import com.h4313.deephouse.sensor.Sensor;

/**
 * Created by Steevens on 30/01/14.
 */
public class EchangesModeleMaison
{
	private static String url;
	private static String url_maison;
	private static String url_userAction;
	private static String url_addSensor;
	private static String url_addActuator;
	private static String url_date;
	
    /**
     * Informer le modele de la maison d'une action a VALEUR de la part de l'utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur a actionne. EX : temperature, humidite...
     * @param newValue
     * @return
     */
	
	public static void setIp(String ip){
		System.out.println("ip = " + ip);
		url = "http://"+ip;
		url_maison = url+"/deepHouse/rest/houseModel";
		url_userAction = url+"/deepHouse/rest/userAction";
		url_addSensor = url+"/deepHouse/rest/addSensor";
		url_addActuator = url+"/deepHouse/rest/addActuator";
		url_date = url+"/deepHouse/rest/date";
	}
	
	public static String getBaseIp(){
		return url;
	}
	
    public static void actionUtilisateur(int numPiece, String objetAction, double newValue) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("typeAction", objetAction.toUpperCase(Locale.FRANCE)));
	    nameValuePairs.add(new BasicNameValuePair("valeur", Double.toString(newValue)));
        
        try {
            ParseJSONPost jsonParser = new ParseJSONPost(url_userAction,nameValuePairs);
            String resultat = jsonParser.getJson();
            System.out.println(nameValuePairs.toString()); //TODO : Remove when debug is done
            System.out.println(resultat); //TODO : Remove when debug is done
            } 
        catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Informer le modele de la maison d'une action TOUT ou RIEN de l'utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur a actionne. EX : temperature, humidite...
     * @param valeurCapteur
     * @return
     */
    public static void actionUtilisateur(int numPiece, String objetAction, boolean valeurCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("typeAction", objetAction));
	    nameValuePairs.add(new BasicNameValuePair("valeur", Boolean.toString(valeurCapteur)));
        
        try {
            ParseJSONPost jsonParser = new ParseJSONPost(url_userAction,nameValuePairs);
            String resultat = jsonParser.getJson();
            System.out.println(nameValuePairs.toString()); //TODO : Remove when debug is done
            System.out.println(resultat); //TODO : Remove when debug is done
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
    public static boolean ajoutCapteur(String numPiece, String idCapteur, String typeCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", numPiece));
	    nameValuePairs.add(new BasicNameValuePair("capteur", idCapteur));
	    nameValuePairs.add(new BasicNameValuePair("type", typeCapteur));
	
	    boolean success = false;
        try {
            ParseJSONPost jsonParser = new ParseJSONPost(url_addSensor,nameValuePairs);
            String resultat = jsonParser.getJson();
            System.out.println(resultat); //TODO : Remove when debug is done
            System.out.println(nameValuePairs.toString());
            success = resultat.contains("true");
            } 
        catch (Exception e) {
			e.printStackTrace();
		}
        
        return success;
    }
    
     /**
     * Informer le modele de la maison de l'ajout d'un actionneur.
     * @param numPiece
     * @param idactionneur : numero de serie de l'actionneur (saisi par l'utilisateur).
     * @param typeCapteur : nature de l'actionneur (capteur de presence, de lumiere...) identifie par un entier.
     * @return
     */
    public static boolean ajoutActionneur(String numPiece, String idactionneur, String typeCapteur) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", numPiece));
	    nameValuePairs.add(new BasicNameValuePair("actionneur", idactionneur));
	    nameValuePairs.add(new BasicNameValuePair("type", typeCapteur));
	    
	    boolean success = false;
        try {
            ParseJSONPost jsonParser = new ParseJSONPost(url_addActuator,nameValuePairs);
            String resultat = jsonParser.getJson();
            System.out.println(resultat); //TODO : Remove when debug is done
            success = resultat.contains("true");
        } 
        catch (Exception e) {
			e.printStackTrace();
		}
        
        return success;
    }

    /**
     * reception periodique des valeurs des capteurs depuis le modele de la maison
     * @param jsonHouse
     * @throws JSONException
     */
	public static String recupererMaison()
	{
		System.out.println("Updating house from server."); //TODO : Delete when debug is done
        ParseJSON jsonParser = new ParseJSON(url_maison);
        String maison = jsonParser.getJson();
        return maison;
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
    
    public static boolean updateHouse() {
    	String maison = recupererMaison();
    	boolean success = false;
    	try {
			House house = getHouseFromJson(maison);
			House.setInstance(house);
			success = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return success;
    }
    
    public static String getCurrentDate() throws Exception {
    	String milliseconds="0";
    	
		ParseJSON parser = new ParseJSON(url_date);
		JSONObject obj = parser.getJSONObject();
		Boolean success = (obj.get("success").toString()).equals("true");
		if(!success.booleanValue())
		{
			throw new Exception();
		}
		
		milliseconds = obj.get("date").toString();
		return milliseconds;
    }
}