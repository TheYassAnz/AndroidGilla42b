package com.example.androidgilla42b;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class EvenementActivity extends AppCompatActivity {

    // contrôles de la vue (layout) correspondante
    TextView txtDateTime;
    TextView txtTitle;
    TextView txtDescription;

    // noms des noeuds JSON
    private static final String TAG_ID = "id";
    private static final String TAG_DATE_TIME = "date_time";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";

    // variables associées aux noeuds JSON
    String aid = "";
    String date_time = "";
    String title = "";
    String description = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenement);

        // affichage du titre de l'activité dans la barre applicative (app bar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_gilla) + " : " +  getString(R.string.evenement));

        // récupération de l'intent de la vue de détail :
        Intent in = getIntent();

        // obtention de l'aid de l'élément à partir de l'intent :
        aid = in.getStringExtra(TAG_ID);

        // DEBUG : affichage temporaire de aid
        // Toast.makeText(MessageActivity.this, "Elément demandé aid : "+aid, Toast.LENGTH_LONG).show();

        // chargement de l'élément en fil d'exécution de fond (background thread) :
        new GetItem().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Tâche de fond pour charger les détails d'un élément par une requête HTTP :
     * */
    class GetItem extends AsyncTask<String, String, JSONObject> {

        // url pour obtenir un élément :
        String apiUrl = "";
        // la clé applicative (apikey) est le jeton du super-utilisateur Joomla 4 (SLAM) :
        String apiKey = getString(R.string.x_joomla_token);

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String TAG_DATA = "data";
        private static final String TAG_ATTRIBUTES = "attributes";

        // affiche une barre de progression avant d'activer la tâche de fond :
        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            pDialog = new ProgressDialog(EvenementActivity.this);
            pDialog.setMessage("Attente de connexion...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            apiUrl = "http://" + SP.getString("PREF_API_URL_LOC", getString(R.string.pref_default_api_url_loc)) + "/gilla/events";
            String prefAPI = SP.getString("PREF_API", "0");
            if (prefAPI.equals("1")) {
                apiUrl = "https://" + SP.getString("PREF_API_URL_DIST", getString(R.string.pref_default_api_url_dist)) + "/gilla/events";
            }
            // Toast.makeText(EvenementActivity.this, "URL de l'API : " + apiUrl, Toast.LENGTH_LONG).show();
        }

        // obtention en tâche de fond de l'élément au format JSON par une requête HTTP :
        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> hmUrlParams = new HashMap<>();
                HashMap<String, String> hmBodyParams = new HashMap<>();
                Log.d("request", "starting");
                JSONObject json = jsonParser.makeHttpRequest(apiUrl, apiKey, "GET", aid, hmUrlParams, hmBodyParams);
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // ferme la boite de dialogue à la terminaison de la tâche de fond :
        protected void onPostExecute(JSONObject json) {
            // int success = 0;
            // String message = "";

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                // Toast.makeText(EvenementActivity.this, json.toString(), Toast.LENGTH_LONG).show();  // TEST/DEBUG
                Log.d("Success!", "Connexion réussie");
                // détail de l'objet JSON reçu :
                try {
                    JSONObject objData = json.getJSONObject(TAG_DATA);
                    JSONObject objAttrib = objData.getJSONObject(TAG_ATTRIBUTES);

                    // enregistrement de chaque attribut dans une variable
                    date_time = objAttrib.getString(TAG_DATE_TIME);
                    title = objAttrib.getString(TAG_TITLE);
                    description = objAttrib.getString(TAG_DESCRIPTION);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Failure", "Echec de connexion)");
            }

            // mise à jour de l'interface utilisateur (UI) depuis le thread principal :
            runOnUiThread(new Runnable() {
                public void run() {

                    // mise à jour des TextView avec les données JSON :
                    txtDateTime = (TextView) findViewById(R.id.date_time);
                    txtTitle = (TextView) findViewById(R.id.title);
                    txtDescription = (TextView) findViewById(R.id.description);

                    // affiche les données de l'élément dans les TextView :

                    txtDateTime.setText(date_time);
                    txtTitle.setText(title);
                    txtDescription.setText(description);



                }
            });
        }
    }
    /**
     * Tâche de fond pour supprimer l'élément sélectionné par une requête HTTP :
     * */
    class DeleteItem extends AsyncTask<String, String, JSONObject> {

        // url pour supprimer un élément :
        String apiUrl = "";
        // la clé applicative (apikey) est le jeton du super-utilisateur Joomla 4 (SLAM) :
        String apiKey = getString(R.string.x_joomla_token);

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        // affiche une barre de progression avant d'activer la tâche de fond :
        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            pDialog = new ProgressDialog(EvenementActivity.this);
            pDialog.setMessage("Attente de connexion...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            apiUrl = "http://" + SP.getString("PREF_API_URL_LOC", getString(R.string.pref_default_api_url_loc)) + "/gilla/events";
            String prefAPI = SP.getString("PREF_API", "0");
            if (prefAPI.equals("1")) {
                apiUrl = "https://" + SP.getString("PREF_API_URL_DIST", getString(R.string.pref_default_api_url_dist)) + "/gilla/events";
            }
            // Toast.makeText(EvenementActivity.this, "URL de l'API : " + apiUrl, Toast.LENGTH_LONG).show();
        }

        // obtention en tâche de fond de l'élément au format JSON par une requête HTTP :
        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> hmUrlParams = new HashMap<>();
                HashMap<String, String> hmBodyParams = new HashMap<>();
                Log.d("request", "starting");
                JSONObject json = jsonParser.makeHttpRequest(apiUrl, apiKey, "DELETE", aid, hmUrlParams, hmBodyParams);
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // ferme la boite de dialogue à la terminaison de la tâche de fond :
        protected void onPostExecute(JSONObject json) {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
            // retourne à la liste des évènements
            startActivity(new Intent(getApplicationContext(), EvenementsActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_evenement, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère la sélection d'un élément du menu :
        switch (item.getItemId()){
            case R.id.action_modify:
                Intent in = new Intent(getApplicationContext(), EvenementEditActivity.class);
                in.putExtra(TAG_ID, aid);
                in.putExtra(TAG_DATE_TIME, date_time);
                in.putExtra(TAG_TITLE, title);
                in.putExtra(TAG_DESCRIPTION, description);
                startActivity(in);
                return true;

            case R.id.action_delete:
                // crée et initialise le builder de la boîte de dialogue d'alerte
                AlertDialog.Builder builder = new AlertDialog.Builder(EvenementActivity.this);
                builder.setMessage(getString(R.string.question_confirm_delete));
                builder.setTitle(getString(R.string.title_warning));

                // définit l'annulation à faux pour maintenir affichée la boîte de dialogue si l'utilisateur clique à côté
                builder.setCancelable(false);

                // associe le bouton Oui à la suppression de l'élément en fil d'exécution de fond (background thread)
                builder.setPositiveButton("Oui", (DialogInterface.OnClickListener) (dialog, which) -> {
                    new DeleteItem().execute();
                });

                // associe le bouton Non à l'annulation de la suppression
                builder.setNegativeButton("Non", (DialogInterface.OnClickListener) (dialog, which) -> {
                    dialog.cancel();
                });

                // affiche la boîte de dialogue d'alerte
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            case R.id.action_back:
                // retourne à la liste des évènements
                startActivity(new Intent(this, EvenementsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}