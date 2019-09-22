package com.madminds.madminds.VistaGlobetrotter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class ModificaProfiloGlobetrotter extends AppCompatActivity {

    private EditText nome, cognome, email, cellulare, dataNascita;
    private Button invia;

    private static final int RESULT_LOAD_IMAGE = 1;

    private String id;
    public static final String URL_MODIFIED = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreModificaProfilo.php";
    //public static final String SERVER_ADDRESS = "https://madminds.altervista.org/ModificaGlobetrotter/SalvaImmagineProfilo.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = extras.getString("id");


        nome = findViewById(R.id.titoloAttivita);
        cognome = findViewById(R.id.cognome);
        email = findViewById(R.id.email);
        cellulare = findViewById(R.id.cellulare);
        dataNascita = findViewById(R.id.dataNascita);
        invia = findViewById(R.id.btnmodifica);

        nome.setText(extras.getString("nome"));
        cognome.setText(extras.getString("cognome"));
        email.setText(extras.getString("email"));
        cellulare.setText(extras.getString("cell"));
        dataNascita.setText(DateConvertor.convertFormat(extras.getString("data"), "dd/MM/yyyy", "yyyy-MM-dd"));


        dataNascita.setKeyListener(null);

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modificaProfilo();
            }
        });

    }


    private void modificaProfilo() {
        final String nome = this.nome.getText().toString();
        final String cognome = this.cognome.getText().toString();
        final String email = this.email.getText().toString();
        final String cellulare = this.cellulare.getText().toString();
        final String MydataNascita = this.dataNascita.getText().toString();

        //Toast.makeText(this, MydataNascita, Toast.LENGTH_SHORT).show();

        if (nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || cellulare.isEmpty()) {
            Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
        } else {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MODIFIED,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            parseData(response);

                            Intent intent = new Intent(ModificaProfiloGlobetrotter.this, ProfiloGlobetrotter.class);
                            startActivity(intent);
                            finish();

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(ModificaProfiloGlobetrotter.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("nome", nome);
                    params.put("cognome", cognome);
                    params.put("dataNascita", MydataNascita);
                    params.put("cellulare", cellulare);
                    params.put("email", email);
                    params.put("idGlobetrotter", id);

                    return params;
                }

            };

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
    }


    private void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean error = jsonObject.getBoolean("error");

            if (!error) {
                Toast.makeText(ModificaProfiloGlobetrotter.this, "Modifica avvenuta con successo", Toast.LENGTH_LONG).show();
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void dataPicker(View view) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dataNascita.setText(sdf.format(myCalendar.getTime()));
            }
        };

        new DatePickerDialog(ModificaProfiloGlobetrotter.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public void onBackPressed() {
            startActivity(new Intent(ModificaProfiloGlobetrotter.this, ProfiloGlobetrotter.class));
    }

}
