package com.example.deephouse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
//import deephouse.housemodel.House;

public class MainActivity extends Activity {

    public final static String EXTRA_MESSAGE = "com.example.deephouse.MESSAGE";
    public static final String PREFS_NAME = "DeepHousePrefs";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        restoreLoginInfos();
        getActionBar().hide();
    }

    private void restoreLoginInfos(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME,0);
        String ip = settings.getString("ip","");
        String login = settings.getString("login", "");
        String password = settings.getString("password","");
        final TextView textViewIp = (TextView) findViewById(R.id.editTextIP);
        final TextView textViewLogin = (TextView) findViewById(R.id.editTextLogin);
        final TextView textViewPassword = (TextView) findViewById(R.id.editTextPassword);
        if (!ip.equals("") || !login.equals("") || !password.equals("")){
            textViewIp.setText(ip);
            textViewLogin.setText(login);
            textViewPassword.setText(password);
            //Try connecting with the infos
            connexion(findViewById(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    /** Called when the user clicks the Connexion button */
    public void connexion(View view) {
        Intent intent = new Intent(this, HouseActivity.class); //creates next activity
        //Putting text field content in intent
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        //Password easy check
        editText = (EditText) findViewById(R.id.editTextLogin);
        if (editText.getText().toString().equals("Login") /*|| 1==1 /a enlever pour verification*/){
            startActivity(intent);
        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Adresse IP, nom d'utilisateur ou mot de passe invalide.")
                    .setCancelable(true)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        };
    }

    @Override
    protected void onStop(){
        super.onStop();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        final CheckBox checkBoxInfos = (CheckBox) findViewById(R.id.checkBoxSaveInfo);

        if (checkBoxInfos.isChecked()){
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            //Retrieving infos
            final TextView textViewIp = (TextView) findViewById(R.id.editTextIP);
            final TextView textViewLogin = (TextView) findViewById(R.id.editTextLogin);
            final TextView textViewPassword = (TextView) findViewById(R.id.editTextPassword);
            String ip = textViewIp.getText().toString();
            String login = textViewLogin.getText().toString();
            String password = textViewPassword.getText().toString();
            //Saving infos
            editor.putString("ip", ip)
                    .putString("password", password)
                    .putString("login", login)
                    // Commit the edits!
                    .commit();
        }
    }

}
