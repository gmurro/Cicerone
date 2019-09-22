package com.madminds.madminds.VistaGlobetrotter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PrenotaAttivita extends AppCompatActivity {

    private EditText nPartecipanti;
    private Button prenota;

    SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    private String codAttivita;

    public static final String URL = "https://madminds.altervista.org/GestoreAttivita/GestorePrenotaAttivita.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prenota_attivita);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        Intent intent = getIntent();
        codAttivita = intent.getStringExtra("codAttivita");

        nPartecipanti = findViewById(R.id.npartecipanti);
        prenota = findViewById(R.id.btnPrenota);


        prenota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prenotaAttivita();
            }
        });
    }

    public void prenotaAttivita() {
        final String nPartecipanti = this.nPartecipanti.getText().toString();
        final String idAttivita = codAttivita;
        try {
            if(Integer.parseInt(nPartecipanti) < 1) {
                throw new NumberFormatException();
            }

            class Prenota extends AsyncTask<Void, Void, String> {

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("codAttivita", idAttivita);
                    params.put("nPartecipanti", nPartecipanti);

                    //returing the response
                    return requestHandler.sendPostRequest(URL, params);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);
                        //if no error in response
                        if (!obj.getBoolean("error")) {

                            finish();
                            Intent intent = new Intent(PrenotaAttivita.this, ConfermaPrenotazione.class);
                            intent.putExtra("codAttivita", codAttivita);
                            intent.putExtra("nPartecipanti", nPartecipanti);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PrenotaAttivita.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                    }
                }
            }

            Prenota prenotazione = new Prenota();
            prenotazione.execute();

        } catch (NumberFormatException e) {
            Toast.makeText(getApplicationContext(),"Inserire un numero valido", Toast.LENGTH_LONG).show();
        }

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}
