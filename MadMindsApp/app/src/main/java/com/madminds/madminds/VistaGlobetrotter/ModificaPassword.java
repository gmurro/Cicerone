package com.madminds.madminds.VistaGlobetrotter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.madminds.madminds.VistaCicerone.ProfiloCicerone;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.madminds.madminds.VistaGlobetrotter.Navigazione.MY_PREFERENCES;

public class ModificaPassword extends AppCompatActivity {


    private EditText password, nuovapassword, confermapassword;
    private Button invia;

    SharedPreferences sharedPreferences;
    public static final String CICERONE = "cicerone";

    private String id;
    public static final String URL_MODIFIED = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreModificaPassword.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_password);


        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = extras.getString("id");

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);


        password = findViewById(R.id.vecchiapsw);
        nuovapassword = findViewById(R.id.nuovapsw);
        confermapassword = findViewById(R.id.confermapsw);
        invia = findViewById(R.id.buttonModPsw);

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificaPassword();
                finish();
            }
        });

    }


    private void modificaPassword() {
        final String password = this.password.getText().toString();
        final String nuovapassword = this.nuovapassword.getText().toString();
        final String confermapassword = this.confermapassword.getText().toString();


        if (password.isEmpty() || nuovapassword.isEmpty() || confermapassword.isEmpty() ) {
            Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
        } else if(!nuovapassword.equals(confermapassword)) {
                Toast.makeText(this, "Password di conferma diversa", Toast.LENGTH_SHORT).show();
            }else{
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MODIFIED,
                           new Response.Listener<String>() {
                                @Override
                               public void onResponse(String response) {
                                  parseData(response);

                                    Intent intent = new Intent(ModificaPassword.this, ProfiloGlobetrotter.class);
                                    startActivity(intent);
                                    finish();


                               }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(ModificaPassword.this, error.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("vecchiapsw", password);
                        params.put("nuovapsw", nuovapassword);
                        params.put("idGlobetrotter", id);

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
                Toast.makeText(ModificaPassword.this, "Modifica avvenuta con successo", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

        if (sharedPreferences.getBoolean(CICERONE, false)) {
            startActivity(new Intent(ModificaPassword.this, ProfiloCicerone.class));
        }else{
            startActivity(new Intent(ModificaPassword.this, ProfiloGlobetrotter.class));
        }
    }


}
