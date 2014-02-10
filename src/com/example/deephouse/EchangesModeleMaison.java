package com.example.deephouse;

import com.example.deephouse.HttpCommunication;
import com.h4313.deephouse.housemodel.House;
import org.json.*;

/**
 * Created by Steevens on 30/01/14.
 */
public class EchangesModeleMaison
{
	private static String url = "";
	
    /**
     * Informer le modèle de la maison d’une action utilisateur.
     * @param numPiece
     * @param objetAction : ce que l'utilisateur à actionné. EX : temperature, humidité...
     * @param valeurCapteur
     * @return
     */
    public static void actionUtilisateur(int numPiece, int objetAction, float valeurCapteur) throws JSONException
    {
        JSONObject j = new JSONObject();
        
        j.put("piece", numPiece);
        j.put("typeAction", objetAction);
        j.put("valeur", valeurCapteur);
        
        try {
        	HttpCommunication.sendPost(url,j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Informer le modèle de la maison à l’ajout d’un capteur.
     * @param numPiece
     * @param idCapteur : numero de serie du capteur (saisit par l'utilisateur).
     * @param typeCapteur : nature du capteur (capteur de presence, de lumière...) identifié par un entier.
     * @return
     */
    public static void ajoutCapteur(int numPiece, int idCapteur, int typeCapteur) throws JSONException
    {
        JSONObject j = new JSONObject();
        
        j.put("piece", numPiece);
        j.put("capteur", idCapteur);
        j.put("type", typeCapteur);

        try {
        	HttpCommunication.sendPost(url,j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * reception periodique des valeurs des capteurs depuis le modele de la maison
     * @param jsonHouse
     * @throws JSONException
     */
    public static void receptionInfosCapteurs() throws JSONException
    {
        String jsonResponse;
        
        try {
        	jsonResponse = HttpCommunication.sendGet("URLURLURL");
            JSONObject j = new JSONObject(jsonResponse);
            
        	House h = House.getInstance();
        	h = (House) j.get("house");
        	// updateVue(h); // MAJ vue
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}



