package com.example.androidgilla42b;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // paramètre de connexion à transmettre par intent à l'activité suivante
    // String apiKey = "";
    // private static final String TAG_API_KEY = "apikey";

    // contrôles de la vue (layout) correspondante
    Button btnEvenements;
    Button btnIncidents;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // affichage du titre de l'activité dans la barre applicative (app bar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));

        // récupération de l'intent de la vue courante
        Intent in = getIntent();

        // obtention des paramêtres du user connecté à partir de l'intent
        // apiKey = in.getStringExtra(TAG_API_KEY);

        // déclaration des boutons
        btnEvenements = (Button) findViewById(R.id.btnEvenements);
        btnIncidents = (Button) findViewById(R.id.btnIncidents);

        // événement de clic sur le bouton de visualisation des Evenements
        btnEvenements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EvenementsActivity.class));
            }
        });

        // événement de clic sur le bouton de visualisation des Incidents
        btnIncidents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), IncidentsActivity.class));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère la sélection d'un élément du menu :
        switch (item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(this, MyPreferenceActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}