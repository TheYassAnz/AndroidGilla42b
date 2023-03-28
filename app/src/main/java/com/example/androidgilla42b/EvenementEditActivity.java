package com.example.androidgilla42b;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class EvenementEditActivity extends AppCompatActivity {

    private static final String TAG_ID = "id";
    private static final String TAG_DATE_TIME = "date_time";
    private static final String TAG_TITLE = "title";
    private static final String TAG_DESCRIPTION = "description";

    String method = "";
    String aid;
    String date_time = "";
    String title = "";
    String description = "";

    // contrôles de la vue (layout) correspondante
    EditText edtDate_time, edtTitle, edtDescription;
    TextView txtDate, txtTime;
    private int mYear, mMonth, mDay, mHour, mMinute;

    AlertDialog.Builder alert;

    private void setDateTimeStr(){
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (Objects.equals(date_time, "")){
            Calendar rightNow = Calendar.getInstance();
            date_time = dateTimeFormat.format(rightNow.getTime());
        } else {
            Calendar mCalendar = Calendar.getInstance();
            mCalendar.set(Calendar.YEAR, mYear);
            mCalendar.set(Calendar.MONTH, mMonth);
            mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
            mCalendar.set(Calendar.HOUR_OF_DAY, mHour);
            mCalendar.set(Calendar.MINUTE, mMinute);
            mCalendar.set(Calendar.SECOND, 0);
            mCalendar.set(Calendar.MILLISECOND, 0);
            date_time = dateTimeFormat.format(mCalendar.getTime());
            edtDate_time.setText(date_time, TextView.BufferType.EDITABLE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evenement_edit);

        // affichage du titre de l'activité dans la barre applicative (app bar)
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.title_gilla) + " : " +  getString(R.string.evenement));

        // récupération de l'intent de la vue de détail :
        Intent in = getIntent();

        // obtention de l'aid de l'évènement à partir de l'intent :
        try {
            aid = in.getStringExtra(TAG_ID);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }

        if (Objects.isNull(aid)){
            // cas de la création d'un évènement
            method = "POST";
            setDateTimeStr();
        } else {
            // cas de la modification d'un évènement
            method = "PATCH";
            date_time = in.getStringExtra(TAG_DATE_TIME);
            title = in.getStringExtra(TAG_TITLE);
            description = in.getStringExtra(TAG_DESCRIPTION);
        }

        // association des éléments du layout aux objets correspondants :


        edtDate_time = (EditText) findViewById(R.id.edtDate_time);
        txtDate=(TextView) findViewById(R.id.in_date);
        txtTime=(TextView) findViewById(R.id.in_time);

        edtTitle = (EditText) findViewById(R.id.edtTitle);
        edtDescription = (EditText) findViewById(R.id.edtDescription);




        // initialisation des objets de type EditText :
        edtDate_time.setText(date_time, TextView.BufferType.EDITABLE);
        edtTitle.setText(title, TextView.BufferType.EDITABLE);
        edtDescription.setText(description, TextView.BufferType.EDITABLE);

        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date dateTimeObj = null;
        try {
            dateTimeObj = dateTimeFormat.parse(date_time);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String dateStr = DateFormat.getDateInstance(DateFormat.FULL).format(dateTimeObj);
        txtDate.setText(dateStr);
        String timeStr = DateFormat.getTimeInstance(DateFormat.SHORT).format(dateTimeObj);
        txtTime.setText(timeStr);

        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        mYear = Integer.parseInt(yearFormat.format(dateTimeObj));
        mMonth = Integer.parseInt(monthFormat.format(dateTimeObj)) - 1;
        mDay = Integer.parseInt(dayFormat.format(dateTimeObj));
        mHour = Integer.parseInt(hourFormat.format(dateTimeObj));
        mMinute = Integer.parseInt(minuteFormat.format(dateTimeObj));

        txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ouvre la boîte de dialogue DatePicker
                DatePickerDialog dpDialog = new DatePickerDialog(EvenementEditActivity.this,
                        (view, year, month, day) -> {
                            Calendar mCalendar = Calendar.getInstance();
                            mCalendar.set(Calendar.YEAR, year);
                            mCalendar.set(Calendar.MONTH, month);
                            mCalendar.set(Calendar.DAY_OF_MONTH, day);
                            String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
                            txtDate.setText(selectedDate);

                            mYear = year;
                            mMonth = month;
                            mDay = day;
                            setDateTimeStr();
                        }, mYear, mMonth, mDay);
                dpDialog.show();
            }
        });

        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ouvre la boîte de dialogue TimePicker
                TimePickerDialog tpDialog = new TimePickerDialog(EvenementEditActivity.this,
                        (view, hour, minute) -> {
                            txtTime.setText(hour + ":" + minute);

                            // HOUR24_MINUTE !?
                            // Calendar mCalendar = Calendar.getInstance();
                            // mCalendar.set(Calendar.YEAR, mYear);
                            // mCalendar.set(Calendar.MONTH, mMonth);
                            // mCalendar.set(Calendar.DAY_OF_MONTH, mDay);
                            // String selectedDate = DateFormat.getDateInstance(DateFormat.HOUR24_MINUTE).format(mCalendar.getTime());
                            // txtDate.setText(selectedDate);

                            mHour = hour;
                            mMinute = minute;
                            setDateTimeStr();
                        }, mHour, mMinute, true);
                tpDialog.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Tâche de fond pour poster l'ajout de l'item par une requête HTTP :
     */
    class SaveItem extends AsyncTask<String, String, JSONObject> {

        // url pour enregistrer un élément
        String apiUrl = "";
        // la clé applicative (apikey) est le jeton du super-utilisateur Joomla 4 (SLAM) :
        String apiKey = getString(R.string.x_joomla_token);

        JSONParser jsonParser = new JSONParser();

        private ProgressDialog pDialog;

        private static final String TAG_DATA = "data";
        private static final String TAG_ATTRIBUTES = "attributes";

        // affiche une barre de progression avant d'activer la tâche de fond
        @Override
        protected void onPreExecute() {
            // super.onPreExecute();
            pDialog = new ProgressDialog(EvenementEditActivity.this);
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
            // Toast.makeText(EvenementEditActivity.this,"URL de l'API : " + apiUrl,Toast.LENGTH_LONG).show();
        }

        // obtention en tâche de fond de la réponse au format JSON par une requête HTTP
        @Override
        protected JSONObject doInBackground(String... args) {
            try {
                HashMap<String, String> hmUrlParams = new HashMap<>();
                HashMap<String, String> hmBodyParams = new HashMap<>();
                hmBodyParams.put("date_time", edtDate_time.getText().toString());
                hmBodyParams.put("title", edtTitle.getText().toString());
                hmBodyParams.put("description", edtDescription.getText().toString());
                Log.d("request", "starting");
                JSONObject json = jsonParser.makeHttpRequest(apiUrl, apiKey, method, aid, hmUrlParams, hmBodyParams);
                if (json != null) {
                    Log.d("JSON result", json.toString());
                    return json;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(EvenementEditActivity.this,"Requête invalide !",Toast.LENGTH_LONG).show();
            }
            return null;
        }

        protected void onPostExecute(JSONObject json) {
            int success = 0;

            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (json != null) {
                // Toast.makeText(EvenementEditActivity.this, json.toString(), Toast.LENGTH_LONG).show();
                Log.d("Success!", "Connexion réussie");
                // Details de la réponse reçue :
                try {
                    JSONObject objData = json.getJSONObject(TAG_DATA);
                    JSONObject objAttrib = objData.getJSONObject(TAG_ATTRIBUTES);

                    success = 1;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("Failure", "Echec de connexion)");
            }

            if (success == 1) {
                // Enregistrement réussi :
                // retourne à la liste des évènements
                startActivity(new Intent(getApplicationContext(), EvenementsActivity.class));
            } else {
                // Echec de l'enregistrement !
                alert = new AlertDialog.Builder(EvenementEditActivity.this);
                alert.setMessage("Saisie incorrecte : réessayer !");
                alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ajoute les entrées de menu_test à l'ActionBar
        getMenuInflater().inflate(R.menu.menu_evenement_edit, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // gère la sélection d'un élément du menu :
        switch (item.getItemId()){
            case R.id.action_save:
                if (edtTitle.getText().toString().equals("") || edtDescription.getText().toString().equals(""))
                {
                    Toast.makeText(EvenementEditActivity.this, getString(R.string.warning_save), Toast.LENGTH_LONG).show();
                    return false;
                }
                else
                {
                    // enregistrement de l'élément en fil d'exécution de fond (background thread)
                    new SaveItem().execute();
                    return true;
                }
//                return true;
            case R.id.action_back:
                startActivity(new Intent(this, EvenementsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}