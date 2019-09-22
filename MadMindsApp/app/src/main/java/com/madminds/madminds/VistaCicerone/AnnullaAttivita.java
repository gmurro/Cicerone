package com.madminds.madminds.VistaCicerone;

import android.app.AlertDialog;
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

import com.madminds.madminds.VistaGlobetrotter.Navigazione;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.COD_ATTIVITA;

public class AnnullaAttivita extends AppCompatActivity {

    private TextView textViewMotivo;
    private SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    private String codAttivita;
    private String URL_ANNULLA_ATT ="https://madminds.altervista.org/GestoreAttivita/GestoreAnnullaAttivita.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annulla_attivita);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        codAttivita = intent.getStringExtra(COD_ATTIVITA);

        Button annulla_attivita = findViewById(R.id.btnAnnullaAttivita);
        textViewMotivo = findViewById(R.id.motivo_annull_attivita);


        annulla_attivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AnnullaAttivita.this);
                builder.setCancelable(true);
                builder.setTitle("Annulla attivita");
                builder.setMessage("Vuoi annullare la prenotazione? \n Dovrai rimborsare tutti i Globetrotter prenotati.");
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                annullaAttivita();
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


    public void annullaAttivita() {
        class Annulla extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(AnnullaAttivita.this);

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
                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                final String dataCorrente = formatter.format(date).toString();

                params.put("dataCancellazione", dataCorrente);
                params.put("codAttivita", codAttivita);
                params.put("motivo", motivo);

                //returing the response
                return requestHandler.sendPostRequest(URL_ANNULLA_ATT, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AnnullaAttivita.this);
                        builder.setCancelable(false);
                        builder.setTitle("Attività annullata");
                        builder.setMessage(obj.getString("message"));
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(AnnullaAttivita.this, Navigazione.class);
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
                    Toast.makeText(AnnullaAttivita.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
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
