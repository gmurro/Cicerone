package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AlertDialog;


import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class ConfermaPrenotazione extends AppCompatActivity {

    private TextView nPrenotati, titolo, creatore, lingua, citta, regione, data, ora, importo, servizio, totale;
    private Button conferma;
    SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    private String codAttivita;
    private String nPartecipanti;
    private  String surplus;
    private  String prezzoTotale;

    public static final String URL_RESOCONTO = "https://madminds.altervista.org/GestoreAttivita/GestoreResocontoPrenotazione.php";
    public static final String URL_CONFERMA = "https://madminds.altervista.org/GestoreAttivita/GestoreConfermaPrenotazione.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conferma_prenotazione);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        codAttivita = intent.getStringExtra("codAttivita");
        nPartecipanti= intent.getStringExtra("nPartecipanti");

        nPrenotati = findViewById(R.id.nPrenotati);
        titolo = findViewById(R.id.titolo);
        creatore = findViewById(R.id.creatore);
        lingua = findViewById(R.id.lingua);
        citta = findViewById(R.id.citta);
        regione = findViewById(R.id.regione);
        data= findViewById(R.id.data);
        ora= findViewById(R.id.ora);
        importo= findViewById(R.id.importo);
        totale= findViewById(R.id.totale);
        servizio = findViewById(R.id.servizio);
        conferma = findViewById(R.id.button_conferma_prenotazione);

        caricaResoconto();

        conferma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ConfermaPrenotazione.this);
                builder.setCancelable(true);
                builder.setTitle("Conferma prenotazione");
                builder.setMessage("Vuoi confermare la prenotazione pagando l'importo dovuto?");
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                confermaPrenotazione();
                            }
                        });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void caricaResoconto() {


        class ResocontoPrenotazione extends AsyncTask<Void, Void, String> {


            ProgressDialog pdLoading = new ProgressDialog(ConfermaPrenotazione.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
                pdLoading.setMessage("\tCaricamento...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("codAttivita", codAttivita.trim());
                params.put("nPartecipanti", nPartecipanti.trim());

                //returing the response
                return requestHandler.sendPostRequest(URL_RESOCONTO, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    boolean error = obj.getBoolean("error");

                    if (!error) {

                        nPrenotati.setText("Prenotazione per "+nPartecipanti+" persone");
                        titolo.setText(obj.getString("titolo"));
                        creatore.setText(obj.getString("creatore"));
                        lingua.setText(obj.getString("lingua"));
                        citta.setText(obj.getString("citta"));
                        regione.setText(obj.getString("regione"));
                        data.setText(obj.getString("data"));
                        ora.setText(obj.getString("oraAppuntamento"));
                        importo.setText(obj.getString("prezzoParziale")+"€");
                        surplus = obj.getString("surplus");
                        servizio.setText(obj.getString("servizio")+"€ ("+surplus+"%)");
                        totale.setText(obj.getString("prezzoTotale")+"€");
                        prezzoTotale = obj.getString("prezzoTotale");

                    }else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ConfermaPrenotazione.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        ResocontoPrenotazione res = new ResocontoPrenotazione();
        res.execute();
    }



    public void confermaPrenotazione() {
        class Conferma extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(ConfermaPrenotazione.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
                pdLoading.setMessage("\tCaricamento...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("codGlobetrotter", sharedPreferences.getString(ID,"0"));
                params.put("codAttivita", codAttivita);
                params.put("nPartecipanti", nPartecipanti);
                params.put("surplus", surplus);
                params.put("importo", prezzoTotale);

                //returing the response
                return requestHandler.sendPostRequest(URL_CONFERMA, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        finish();
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ConfermaPrenotazione.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        Conferma conf = new Conferma();
        conf.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
