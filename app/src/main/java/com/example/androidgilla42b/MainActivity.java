package com.example.androidgilla42b;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    // paramètre de connexion à transmettre par intent à l'activité suivante
    String apiKey = "";
    private static final String TAG_API_KEY = "apikey";
    // contrôles de la vue (layout) correspondante
    Button btnEvenements;
    Button btnIncidents;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // récupération de l'intent de la vue courante
        Intent in = getIntent();
        // obtention des paramêtres du user connecté à partir de l'intent
        apiKey = in.getStringExtra(TAG_API_KEY);
        // déclaration des boutons
        btnEvenements = (Button) findViewById(R.id.btnEvenements);
        btnIncidents = (Button) findViewById(R.id.btnIncidents);
        // événement de clic sur le bouton de visualisation des Evenements
        btnEvenements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),
                        EvenementsActivity.class);
                in.putExtra(TAG_API_KEY, apiKey);
                startActivity(in);
            }
        });
        // événement de clic sur le bouton de visualisation des Incidents
        btnIncidents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getApplicationContext(),
                        IncidentsActivity.class);
                in.putExtra(TAG_API_KEY, apiKey);
                startActivity(in);
            }
        });
    }
}
