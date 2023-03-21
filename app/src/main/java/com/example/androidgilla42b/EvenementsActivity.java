package com.example.androidgilla42b;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class EvenementsActivity extends AppCompatActivity {

    protected ArrayList<Evenement> evenements = new ArrayList<>();
    protected EvenementsAdapter adapter;
    protected EvenementsAdapter.OnItemClickListener listener;

    protected RecyclerView rvEvenements;

    // noms des noeuds JSON :
    private static final String TAG_ID = "id";
    private static final String TAG_DATE_TIME = "date_time";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";

    // tableau JSON de la liste des items :
    JSONArray items = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenements);



        // affichage du titre de l'activité dans la barre applicative (app bar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_gilla) + " : " +  getString(R.string.evenements));

        // recherche du RecyclerView dans le calque de l'activité
       rvEvenements = (RecyclerView) findViewById(R.id.rvEvenements);

        // chargement des éléments en fil d'exécution de fond (background thread) :
        new LoadItems().execute();


    }

    /**
     * Tâche de fond pour charger la liste des items par une requête HTTP :
     */
    class LoadItems extends AsyncTask<String, String, JSONObject> {

        // url pour obtenir la liste des items :
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
            pDialog = new ProgressDialog(EvenementsActivity.this);
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
            // Toast.makeText(EvenementsActivity.this, "URL de l'API : " + apiUrl, Toast.LENGTH_LONG).show();
        }

        // obtention en tâche de fond des items au format JSON par une requête HTTP
        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> hmUrlParams = new HashMap<>();
                HashMap<String, String> hmBodyParams = new HashMap<>();
                Log.d("request", "starting");
                JSONObject json = jsonParser.makeHttpRequest(apiUrl, apiKey, "GET", null, hmUrlParams, hmBodyParams);
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {

                Log.d("Ce que je veux voir", "Ce que je veux voir") ;
                e.printStackTrace();
            }
            return null;
        }

        // ferme la boite de dialogue à la terminaison de la tâche de fond
        protected void onPostExecute(JSONObject json) {
            // int success = 0;
            // String message = "";

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                // Toast.makeText(EvenementsActivity.this, json.toString(), Toast.LENGTH_LONG).show();  // TEST/DEBUG
                Log.d("Success!", "Connexion réussie");
                // Liste des items trouvés => obtention du tableau des items
                try {
                    items = json.getJSONArray(TAG_DATA);
                    // boucle sur tous les éléments
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject objData = items.getJSONObject(i);
                        JSONObject objAttrib = objData.getJSONObject(TAG_ATTRIBUTES);

                        // enregistrement de chaque élément JSON dans une variable
                        String id = objAttrib.getString(TAG_ID);
                        String title = objAttrib.getString(TAG_TITLE);
                        String description = objAttrib.getString(TAG_DESCRIPTION);
                        String date_time = objAttrib.getString(TAG_DATE_TIME);

                        // création d'un nouvel objet Evenement et ajout dans la liste evenments
                        evenements.add(new Evenement(id, title, description, date_time));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Failure", "Echec de connexion");
            }

            // charge l'adaptateur avec la liste des éléments et attache celui-ci à la vue RecyclerView
            rvEvenements.setAdapter(new EvenementsAdapter(evenements, new EvenementsAdapter.OnItemClickListener() {
                @Override public void OnItemClick(Evenement item) {
                    // Toast.makeText(EvenementsActivity.this, "Item clicked : " + item.getId(), Toast.LENGTH_LONG).show();
                    String aid = item.getId();

                    // création d'une nouvelle intention (intent)
                    Intent in = new Intent(getApplicationContext(), EvenementActivity.class);
                    // envoi de l'aid à l'activité suivante (activity)
                    in.putExtra(TAG_ID, aid);
                    // lancement de la nouvelle activité (vue de détail) en attente d'une réponse
                    startActivityForResult(in, 100);
                }
            }));

            // Définit le gestionnaire de présentation des éléments
            rvEvenements.setLayoutManager(new LinearLayoutManager(EvenementsActivity.this));
        }
    }

    // réponse de l'activité vue de détail :
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            // raffraichissement de l'écran :
            Intent in = getIntent();
            finish();
            startActivity(in);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_evenements, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère la sélection d'un élément du menu :
        switch (item.getItemId()){
            case R.id.action_add:
                startActivity(new Intent(getApplicationContext(), EvenementEditActivity.class));
                return true;
            case R.id.action_back:
                startActivity(new Intent(this, MainActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
