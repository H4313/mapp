package com.example.deephouse;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import android.hardware.Sensor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.h4313.deephouse.actuator.Actuator;
import com.h4313.deephouse.adapter.ActuatorAdapter;
import com.h4313.deephouse.adapter.RoomAdapter;
import com.h4313.deephouse.adapter.SensorAdapter;
import com.h4313.deephouse.housemodel.House;
import com.h4313.deephouse.housemodel.Room;

/**
 * Created by Steevens on 30/01/14.
 */
public class EchangesModeleMaison
{
	private static String url = "http://10.0.2.2:8080";
	private static String urlPaul = "http://www.paul-molins.fr";
	private static String url_maison_paul = urlPaul+"/deephouse/houseModel";
	private static String url_maison = url+"/deepHouse/rest/houseModel";
	
    /**
     * Informer le modele de la maison d'une action a VALEUR de la part de l'utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur a actionne. EX : temperature, humidite...
     * @param newValue
     * @return
     */


    public static void actionUtilisateur(int numPiece, String objetAction, double newValue) throws JSONException
    {
	    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	    nameValuePairs.add(new BasicNameValuePair("piece", Integer.toString(numPiece)));
	    nameValuePairs.add(new BasicNameValuePair("typeAction", objetAction));
	    nameValuePairs.add(new BasicNameValuePair("valeur", Double.toString(newValue)));
        
        try {
            ParseJSONPost jsonParser = new ParseJSONPost(url,nameValuePairs);
            String resultat = jsonParser.getJson();
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
            ParseJSONPost jsonParser = new ParseJSONPost(url,nameValuePairs);
            String resultat = jsonParser.getJson();
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
            ParseJSONPost jsonParser = new ParseJSONPost(url,nameValuePairs);
            String resultat = jsonParser.getJson();
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
            ParseJSONPost jsonParser = new ParseJSONPost(url,nameValuePairs);
            String resultat = jsonParser.getJson();
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
        //String jHouse = recupererMaison();
        
        try {
            //House maison = getHouseFromJson(jHouse); // throws an exception if jHouse isn't a valid representation of a House
            //House.setInstance(maison);
        	//System.out.println("House update verification :" + House.getInstance().getRooms().get(0).getSensors().toString());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
    }
    

	public static String recupererMaison()
	{
		System.out.println("Recuperation de la maison depuis le serveur.");
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
    
    public static House getHouse(){
    	System.out.println(url_maison);
    	String maison = recupererMaison();
    	House house = new House();
    	try {
			house = getHouseFromJson(maison);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return house;
    }
}