package com.example.deephouse;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.h4313.deephouse.housemodel.House;

/**
 * Created by Steevens on 30/01/14.
 */
public class EchangesModeleMaison
{
	private static String url = "http://www.paul-molins.fr/deephouse/post.php";
	private static String url_maison = "http://paul-molins.fr/deephouse/houseModel.json";
	
    /**
     * Informer le modele de la maison d'une action utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur a actionne. EX : temperature, humidite...
     * @param valeurCapteur
     * @return
     */
	
	public static String recupererMaison(){
        String jsonResponse;
        try{
        	//Arguments formatting
        	jsonResponse = HttpCommunication.sendPost(url_maison, new ArrayList<NameValuePair>(0)); //from HttpCommunication
            return jsonResponse;
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
		} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * reception periodique des valeurs des capteurs depuis le modele de la maison
     * @param jsonHouse
     * @throws JSONException
     */
    public static void majInfosCapteurs()
    {
        String jsonResponse;
        
        try {
        	jsonResponse = HttpCommunication.sendGet(url);
            JSONObject j = new JSONObject(jsonResponse);
            
        	House h = House.getInstance();
        	h = (House) j.get("house");
        	// updateVue(h); // MAJ vue
		} catch (Exception e) {
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
}



