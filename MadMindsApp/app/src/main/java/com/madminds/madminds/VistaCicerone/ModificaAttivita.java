package com.madminds.madminds.VistaCicerone;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.R;
import com.madminds.madminds.TimePickerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.COD_ATTIVITA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_CITTA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_DESCRIZIONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LATIT;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LONG;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_ORA;

public class ModificaAttivita extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private String codAttivita, descrizione, lat, longi, citta, ora;
    private EditText desc, via, oraApp;
    private Button invia;
    private ProgressDialog pDialog;
    private double latitudine, longitudine;

    private static final String URL_MODIFICA = "https://www.madminds.altervista.org/GestoreAttivita/GestoreModificaAttivita.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_attivita);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        codAttivita = extras.getString(COD_ATTIVITA);
        descrizione = extras.getString(EXTRA_DESCRIZIONE);
        lat = extras.getString(EXTRA_LATIT);
        longi = extras.getString(EXTRA_LONG);
        citta = extras.getString(EXTRA_CITTA);
        ora = extras.getString(EXTRA_ORA);

        invia = findViewById(R.id.buttonModificaAttivita);
        desc = findViewById(R.id.descrizioneAttivitaPubblica1);
        via = findViewById(R.id.indirizzoAppuntamento1);
        oraApp = findViewById(R.id.orarioAppuntamento1);

        desc.setText(descrizione);
        oraApp.setText(ora);

        Geocoder geocoder = new Geocoder(ModificaAttivita.this);
        List<Address> addresses;
        addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(longi), 1);
        } catch (IOException e) {
            Toast.makeText(ModificaAttivita.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
        if (addresses.size() > 0) {
            via.setText(addresses.get(0).getAddressLine(0));
        } else {
            Toast.makeText(this, "Indirizzo non trovato!", Toast.LENGTH_SHORT).show();
        }

        oraApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ModificaAttivita.this);
                builder.setCancelable(true);
                builder.setTitle("Vuoi davvero modificare l'attività?");
                builder.setMessage("Provvederemo ad inviare una notifica a tutti i prenotati");
                builder.setPositiveButton("Conferma",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                displayLoader();
                                modificaAttivita();
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

    private boolean checktimings(String time, String endtime) {

        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                return true;
            } else {

                return false;
            }
        } catch (ParseException e){
        }
        return false;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        //int ora = Integer.parseInt(ora.substring(0, 3));
        //Toast.makeText(ModificaAttivita.this, ora.substring(0, 5), Toast.LENGTH_LONG).show();
        /*
        if {
            oraApp.setText(time);
        } else {
            Toast.makeText(ModificaAttivita.this, "modifica troppo drastica all'orario", Toast.LENGTH_LONG).show();
        }*/
        oraApp.setText(time);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ModificaAttivita.this);
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void modificaAttivita() {

        if(desc.getText().toString().isEmpty() || via.getText().toString().isEmpty() || oraApp.getText().toString().isEmpty()) {
            Toast.makeText(ModificaAttivita.this, "inserire tutti i campi", Toast.LENGTH_LONG).show();
        } else {

            Geocoder geocoder = new Geocoder(ModificaAttivita.this);
            List<Address> addresses;
            addresses = null;
            String indirizzoCompleto = via.getText().toString() + "," + citta;
            try {
                addresses = geocoder.getFromLocationName(indirizzoCompleto, 1);
            } catch (IOException e) {
                Toast.makeText(ModificaAttivita.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if(addresses.size() > 0) {
                latitudine = addresses.get(0).getLatitude();
                longitudine = addresses.get(0).getLongitude();
                //asd.setText(Double.toString(latitude) + " " + Double.toString(longitude));



            } else {
                Toast.makeText(this, "Indirizzo non trovato!", Toast.LENGTH_SHORT).show();
            }


            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MODIFICA,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            pDialog.dismiss();
                            parseData(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            pDialog.dismiss();
                            Toast.makeText(ModificaAttivita.this, error.toString(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    params.put("codAttivita", codAttivita);
                    params.put("descrizione", desc.getText().toString());
                    params.put("latiApp", Double.toString(latitudine));
                    params.put("longApp", Double.toString(longitudine));
                    params.put("oraApp", oraApp.getText().toString());

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
                Toast.makeText(ModificaAttivita.this, "Modifica avvenuta, aggiorna con uno swipe down", Toast.LENGTH_LONG).show();
                finish();

            } else {
                pDialog.dismiss();
                Toast.makeText(ModificaAttivita.this, "Errore, riprovare", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
        }

    }
}