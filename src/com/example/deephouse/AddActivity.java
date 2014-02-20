package com.example.deephouse;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.h4313.deephouse.actuator.ActuatorType;
import com.h4313.deephouse.sensor.SensorType;

public class AddActivity extends Activity {

	public Integer tailleId = 8; //TODO : Define as a constant in utils
	public Integer roomNb = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment())
			.commit();
		}

		//Setting the page title
		Intent intent = getIntent();
		roomNb = intent.getIntExtra(MainActivity.EXTRA_MESSAGE,0);
		TextView textView = (TextView) findViewById(R.id.textViewTitre);
		textView.setText("Ajout d'un capteur ou actionneur dans la piece " + getRoomNameById(roomNb).toLowerCase());
		//Setting sensors type
		setSensors();

		//Setting radio button behavior
		RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroup1);        
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() 
		{
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// checkedId is the RadioButton selected
				if (checkedId==2131296266){ //sensor
					TextView textView = (TextView) findViewById(R.id.textViewIDcapt);
					textView.setText("ID du capteur");
					TextView textView1 = (TextView) findViewById(R.id.TextViewSensorType);
					textView1.setText("Type de capteur");
					Button boutonAjout = (Button) findViewById(R.id.buttonAddSC);
					boutonAjout.setText("Ajouter capteur");
					setSensors();
				}
				if (checkedId==2131296267){ //actuator
					TextView textView = (TextView) findViewById(R.id.textViewIDcapt);
					textView.setText("ID de l'actionneur");
					TextView textView1 = (TextView) findViewById(R.id.TextViewSensorType);
					textView1.setText("Type d'actionneur");
					Button boutonAjout = (Button) findViewById(R.id.buttonAddSC);
					boutonAjout.setText("Ajouter actionneur");
					setActuators();
				}
			}
		});
	}

	public void setSensors(){
		SensorType[] possibleValues = SensorType.FLAP.getDeclaringClass().getEnumConstants();
		String[] listeTypesCapteurs = new String[possibleValues.length];
		for (int i=0;i<possibleValues.length;i++){
			listeTypesCapteurs[i] = possibleValues[i].toString();
		}
		Spinner listeCapteurs = (Spinner) findViewById(R.id.spinnerSensors);
		listeCapteurs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,listeTypesCapteurs));
	}

	public void setActuators(){
		ActuatorType[] possibleValues = ActuatorType.DOORCONTROL.getDeclaringClass().getEnumConstants();
		String[] actuatorsTypesList = new String[possibleValues.length];
		for (int i=0;i<possibleValues.length;i++){
			actuatorsTypesList[i] = possibleValues[i].toString();
		}
		Spinner listeCapteurs = (Spinner) findViewById(R.id.spinnerSensors);
		listeCapteurs.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,actuatorsTypesList));
	}

	public void boutonAjout(View view){
		RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
		if (rg.getCheckedRadioButtonId()==2131296266){
			ajoutCapteur(true);
		}
		else{
			ajoutCapteur(false);
		}
	}

	public void ajoutCapteur(boolean isSensor){
		String idCapteur = ((EditText) findViewById(R.id.editTextIDcapt)).getText().toString();
		if (idCapteur.matches("^[0-9a-fA-F]+$") && idCapteur.length() == tailleId){ //TODO : Define constant for sensor size
			//the ID is valid
			if (1==1){ //TODO : Use webservice response to define if ID is already used (contains "false")
				//the ID is not used yet
				String typeCapteur = ((Spinner) findViewById(R.id.spinnerSensors)).getSelectedItem().toString();
				try {
					if (isSensor){
						EchangesModeleMaison.ajoutCapteur(roomNb.toString(), idCapteur, typeCapteur.toUpperCase());
						}
					else{
						EchangesModeleMaison.ajoutActionneur(roomNb.toString(), idCapteur, typeCapteur.toUpperCase());
						}
				} catch (JSONException e) {
					e.printStackTrace();
				} 
				onBackPressed();
			}
			else{
				displayErrorDialog("Identifiant de capteur deja en service.");
			}
		}
		else{
			displayErrorDialog("Identifiant de capteur invalide !\nVeuillez verifier que votre identifiant contient "+ tailleId + " chiffres (0-9) ou lettres (a-f) et n'est pas deja renseigne.");
		}
	}

	public void displayErrorDialog(String errorMsg){
		final Context context = this;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

		// set title
		alertDialogBuilder.setTitle("Erreur");

		// set dialog message
		alertDialogBuilder
		.setMessage(errorMsg)
		.setCancelable(true)
		.setNegativeButton("Ok",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				// if this button is clicked, just close
				// the dialog box and do nothing
				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
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

	public String getRoomNameById(int position) {
		switch (position) {
		case 0:
			return getString(R.string.title_section1);
		case 1:
			return getString(R.string.title_section2);
		case 2:
			return getString(R.string.title_section3);
		case 3:
			return getString(R.string.title_section4);
		case 4:
			return getString(R.string.title_section5);
		case 5:
			return getString(R.string.title_section6);
		}
		return null;
	}
}
