package com.madminds.madminds.VistaCicerone;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.FasciaPrezzo;
import com.madminds.madminds.CircleTrasformation;
import com.madminds.madminds.VistaGlobetrotter.Navigazione;
import com.madminds.madminds.R;
import com.madminds.madminds.TimePickerFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.madminds.madminds.VistaGlobetrotter.Navigazione.ID;
import static com.madminds.madminds.VistaGlobetrotter.Navigazione.MY_PREFERENCES;


public class AggiungiAttivita extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{

    private ImageView imgAttivita;
    private TextView titolo, descrizione, indirizzo, orario, lingua, citta, regione,
        scadenzaPrenotazioni, dataSvolgimento;
    private Button avanti;
    private double latitude, longitude;
    private Bitmap bitmap;
    private static final int RESULT_LOAD_IMAGE = 1;
    private String images = "";
    boolean flagCaricaFoto;
    private int numMaxPartecipanti;
    private ArrayList<FasciaPrezzo> fasce;
    private SharedPreferences sharedPreferences;
    private String idAttivita;
    private ProgressDialog pDialog;

    public static final String URL = "https://madminds.altervista.org/GestoreAttivita/GestorePubblicaAttivita.php";

    //TextView asd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_attivita);

        imgAttivita = findViewById(R.id.imgAttivitaPubblica);
        titolo = findViewById(R.id.titoloAttivitaPubblica);
        descrizione = findViewById(R.id.descrizioneAttivitaPubblica);
        indirizzo = findViewById(R.id.indirizzoAppuntamento);
        orario = findViewById(R.id.orarioAppuntamento);
        lingua = findViewById(R.id.linguaAttivita);
        citta = findViewById(R.id.cittaAttivita);
        regione = findViewById(R.id.regioneAttivita);
        //nMaxPartecipanti = findViewById(R.id.maxAttivita);
        scadenzaPrenotazioni = findViewById(R.id.scadenzaPrenotazioniAttivita);
        dataSvolgimento = findViewById(R.id.dataAttivita);
        avanti = findViewById(R.id.buttonAvantiAddAttivita);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle args = intent.getBundleExtra("BUNDLE");
        numMaxPartecipanti = args.getInt("nMax");
        fasce = (ArrayList<FasciaPrezzo>) args.getSerializable("mylistFascie");

        scadenzaPrenotazioni.setKeyListener(null);
        dataSvolgimento.setKeyListener(null);
        //ArrayList<String> myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        //fasce = gson.fromJson(strObj, ArrayList<FasciaPrezzo>);
        //titolo.setText(fasce.get(0).getMax());

        //asd = findViewById(R.id.asd);

        orario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(aquisisciDati()) {
                    displayLoader();
                    pubblicaAttivita();
                }
            }
        });

        imgAttivita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String time = hourOfDay + ":" + minute;
        orario.setText(time);
    }

    private boolean checkdate(String time, String endtime) {

        String pattern = "yyyy-mm-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);

        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.compareTo(date2) < 0) {
                return true;
            } else {
                return false;
            }
        } catch (ParseException e){
        }
        return false;
    }

    private void pubblicaAttivita(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseData(response);

                        //Intent intent = new Intent(AggiungiAttivita.this, ProfiloGlobetrotter.class);
                        //startActivity(intent);
                        //finish();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AggiungiAttivita.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("add", "addAttivita");
                params.put("creatore", sharedPreferences.getString(ID, null));
                params.put("titolo", titolo.getText().toString());
                params.put("descrizione", descrizione.getText().toString());
                params.put("latiApp", Double.toString(latitude));
                params.put("longApp", Double.toString(longitude));
                params.put("oraApp", orario.getText().toString());
                params.put("lingua", lingua.getText().toString());
                params.put("citta", citta.getText().toString());
                params.put("regione", regione.getText().toString());
                params.put("maxPartecipanti", Integer.toString(numMaxPartecipanti));
                params.put("luogo", images);
                params.put("scadenzaPrenotazione", scadenzaPrenotazioni.getText().toString());
                params.put("data", dataSvolgimento.getText().toString());

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean error = jsonObject.getBoolean("error");
            idAttivita = jsonObject.getString("codAttivita");

            if (!error) {
                //Toast.makeText(AggiungiAttivita.this, "Attivita pubblicata!", Toast.LENGTH_LONG).show();
                //finish();

                idAttivita = jsonObject.getString("codAttivita");
                for(int i=0; i<fasce.size(); i++) {
                    aggiungiFascePrezzo(i);

                    if(i == fasce.size()-1) {
                        pDialog.dismiss();
                        Toast.makeText(AggiungiAttivita.this, "Attivita pubblicata!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), Navigazione.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        //finish();
                    }
                }

            } else {
                pDialog.dismiss();
                Toast.makeText(AggiungiAttivita.this, "Errore, riprovare", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void aggiungiFascePrezzo(final int index) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        /*
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean error = jsonObject.getBoolean("error");
                            idAttivita = jsonObject.getString("codAttivita");

                            if (!error) {
                                //Toast.makeText(AggiungiAttivita.this, "Attivita pubblicata!", Toast.LENGTH_LONG).show();
                                //finish();

                                idAttivita = jsonObject.getString("codAttivita");
                                for(int i=0; i<fasce.size(); i++) {
                                    aggiungiFascePrezzo(i);

                                    if(i == fasce.size()) {
                                        pDialog.dismiss();
                                        finish();
                                    }
                                }

                            } else {
                                pDialog.dismiss();
                                Toast.makeText(AggiungiAttivita.this, "Errore, riprovare", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(AggiungiAttivita.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("add", "addFascia");
                params.put("codAttivita", idAttivita);
                params.put("minPartecipanti", fasce.get(index).getMin());
                params.put("maxPartecipanti", fasce.get(index).getMax());
                params.put("prezzo", fasce.get(index).getPrezzo());

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
                Picasso.get().load(filePath).fit().centerInside().transform(new CircleTrasformation()).into(imgAttivita);
                //Picasso.load(bitmap).fit().centerInside().transform(new CircleTrasformation()).into(immagineprofilo);
                //immagineprofilo.setImageBitmap(bitmap);
                images = getStringImage(bitmap);
                flagCaricaFoto = true;
            } catch (IOException e) {
                Toast.makeText(AggiungiAttivita.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getStringImage(Bitmap bitmap){
        Log.i("MyHitesh",""+bitmap);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(AggiungiAttivita.this);
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private boolean aquisisciDati() {
        boolean flag = false;

        if(titolo.getText().toString().isEmpty() || descrizione.getText().toString().isEmpty() ||
            indirizzo.getText().toString().isEmpty() || orario.getText().toString().isEmpty() ||
            lingua.getText().toString().isEmpty() || citta.getText().toString().isEmpty() ||
            regione.getText().toString().isEmpty() ||
            scadenzaPrenotazioni.getText().toString().isEmpty() || dataSvolgimento.getText().toString().isEmpty() || images.isEmpty()) {
            Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
        } else if (checkdate(dataSvolgimento.getText().toString(), scadenzaPrenotazioni.getText().toString())) {
            Toast.makeText(AggiungiAttivita.this, "Data di scadenza non valida", Toast.LENGTH_LONG).show();
        }else {
            Geocoder geocoder = new Geocoder(AggiungiAttivita.this);
            List<Address> addresses;
            addresses = null;
            String indirizzoCompleto = indirizzo.getText().toString() + "," + citta.getText().toString();
            try {
                addresses = geocoder.getFromLocationName(indirizzoCompleto, 1);
            } catch (IOException e) {
                Toast.makeText(AggiungiAttivita.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
            if(addresses.size() > 0) {
                latitude= addresses.get(0).getLatitude();
                longitude= addresses.get(0).getLongitude();
                //asd.setText(Double.toString(latitude) + " " + Double.toString(longitude));



            } else {
                Toast.makeText(this, "Indirizzo non trovato!", Toast.LENGTH_SHORT).show();
            }
            flag = true;
        }

        return flag;
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
                scadenzaPrenotazioni.setText(sdf.format(myCalendar.getTime()));
            }
        };

        new DatePickerDialog(AggiungiAttivita.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void dataPicker1(View view) {
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
                dataSvolgimento.setText(sdf.format(myCalendar.getTime()));
            }
        };

        new DatePickerDialog(AggiungiAttivita.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //finish();
    }

}
