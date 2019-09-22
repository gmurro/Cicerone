package com.madminds.madminds.VistaGlobetrotter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;


public class AggiungiRecensione extends AppCompatActivity {

    private EditText voto, descrizione;
    private Button invia;

    private static final int RESULT_LOAD_IMAGE = 1;

    private String id, id_cicerone;
    public static final String URL_MODIFIED = "https://madminds.altervista.org/GestoreCicerone/GestoreAggiungiRecensione.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_recensione);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = extras.getString("id");
        id_cicerone = extras.getString("id_cicerone");

        voto = findViewById(R.id.voto);
        descrizione = findViewById(R.id.descrizione);


        invia = findViewById(R.id.btnmodifica);


        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aggiungiRecensione();
            }
        });

    }


    private void aggiungiRecensione() {

        final String descrizione = this.descrizione.getText().toString();
        final String voto = this.voto.getText().toString();

        //Voto vuoto, voto sbagliato
        int voto_rec;
        if (voto.equals("")){
            voto_rec = 0;
        }else{
            voto_rec = Integer.valueOf(voto);
        }

        if (descrizione.isEmpty() || voto_rec <= 0 || voto_rec > 5) {
            Toast.makeText(this, "Alcuni campi sono invalidi", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MODIFIED,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseData(response);
                            Intent intent = new Intent(AggiungiRecensione.this, ProfiloCiceroneEsterno.class);
                            intent.putExtra("id_cicerone_esterno", id_cicerone );
                            startActivity(intent);
                            finish();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(AggiungiRecensione.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("voto", voto);
                    params.put("descrizione", descrizione);
                    params.put("idGlobetrotter", id);
                    params.put("idCicerone", id_cicerone);

                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }


    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean error = jsonObject.getBoolean("error");

            if (!error) {
                Toast.makeText(AggiungiRecensione.this, "Modifica avvenuta con successo", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AggiungiRecensione.this, ProfiloCiceroneEsterno.class);
        intent.putExtra("id_cicerone_esterno", id_cicerone );
        startActivity(intent);
        finish();
    }

}