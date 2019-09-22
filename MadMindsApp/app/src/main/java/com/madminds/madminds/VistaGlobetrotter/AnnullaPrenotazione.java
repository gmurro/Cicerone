package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.FragmentPrenotazioniEffettuate.*;

public class AnnullaPrenotazione extends AppCompatActivity {

    private TextView textViewMotivo;
    private SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    private String codAttivita, scadenza,codPrenotazione, importo, surplus, data;
    private String URL_ANNULLA_PREN ="https://madminds.altervista.org/GestoreAttivita/GestoreAnnullaPrenotazione.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annulla_prenotazione);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        scadenza = intent.getStringExtra(SCADENZA);
        codAttivita = intent.getStringExtra(COD_ATTIVITA);
        codPrenotazione= intent.getStringExtra(COD_PRENOTAZIONE);
        importo=intent.getStringExtra(IMPORTO);
        surplus= intent.getStringExtra(SURPLUS);
        data = intent.getStringExtra(DATA);

        Button annulla_prenotazione = findViewById(R.id.btnAnnullaPrenotazione);
        textViewMotivo = findViewById(R.id.motivo_annull_pren);

        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        final String dataCorrente = formatter.format(date).toString();

        annulla_prenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AnnullaPrenotazione.this);
                builder.setCancelable(true);
                builder.setTitle("Annulla prenotazione");
                String rimborso;
                if(scadenza.compareTo(dataCorrente) < 0) {
                    BigDecimal bd = new BigDecimal(Double.toString(Double.parseDouble(importo)/2));
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    rimborso="Attenzione riceverai un rimborso parziale di "+bd.toString()+" € poichè è stata superata la data di scadenza delle prenotazioni!";
                } else {
                    rimborso="Riceverai un rimborso totale di "+importo+" €";
                }
                builder.setMessage("Vuoi annullare la prenotazione? \n"+rimborso);
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                annullaPrenotazione();
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


    public void annullaPrenotazione() {
        class Annulla extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(AnnullaPrenotazione.this);

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

                String motivo = textViewMotivo.getText().toString();

                params.put("codGlobetrotter", sharedPreferences.getString(ID,"0"));
                params.put("codAttivita", codAttivita);
                params.put("motivo", motivo);
                params.put("surplus", surplus);
                params.put("importo", importo);
                params.put("codPrenotazione", codPrenotazione);
                params.put("scadenzaPrenotazioni", scadenza);
                params.put("dataAttivita", data);

                //returing the response
                return requestHandler.sendPostRequest(URL_ANNULLA_PREN, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AnnullaPrenotazione.this);
                        builder.setCancelable(false);
                        builder.setTitle("Prenotazione annullata");
                        builder.setMessage(obj.getString("message"));
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(AnnullaPrenotazione.this, Navigazione.class);
                                        startActivity(intent);
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AnnullaPrenotazione.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        Annulla annulla = new Annulla();
        annulla.execute();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
