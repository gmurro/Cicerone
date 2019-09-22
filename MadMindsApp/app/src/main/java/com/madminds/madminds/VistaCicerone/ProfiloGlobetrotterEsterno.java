package com.madminds.madminds.VistaCicerone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madminds.madminds.CircleTrasformation;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.COD_ATTIVITA;
import static com.madminds.madminds.VistaGlobetrotter.Navigazione.MY_PREFERENCES;

public class ProfiloGlobetrotterEsterno extends AppCompatActivity {

    TextView nominativo, email, cellulare, dataNascita;
    ImageView mImageView;
    private String fotoProfilo;
    public static final String URL_CARICAMENTO = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreCaricaProfilo.php";
    SharedPreferences sharedPreferences;
    public static final String ID = "id";
    private String idGlobetrotterEsterno;
    private String codAttivita;
    private String nome,cognome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_globetrotter_esterno);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idGlobetrotterEsterno = extras.getString("id_globetrotter_esterno");
        codAttivita = extras.getString("codAttivita");

        nominativo = findViewById(R.id.nomeP);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        email = findViewById(R.id.emailP);
        cellulare = findViewById(R.id.cellulareP);
        dataNascita = findViewById(R.id.dataNascitaP);
        mImageView = findViewById(R.id.immagineProfilo);

        caricaDati();

    }

    @Override
    public void onBackPressed() {
        Intent listaPrenotati = new Intent(ProfiloGlobetrotterEsterno.this, ListaPrenotati.class);
        listaPrenotati.putExtra(ID, sharedPreferences.getString(ID, ""));
        listaPrenotati.putExtra(COD_ATTIVITA, codAttivita);
        startActivity(listaPrenotati);
    }

    private void caricaDati(){

        //final Bitmap image;

        class Carica extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(ProfiloGlobetrotterEsterno.this);

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
                pdLoading.setMessage("\tCaricamento...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }


            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("idGlobetrotter", idGlobetrotterEsterno);

                //returing the response
                return requestHandler.sendPostRequest(URL_CARICAMENTO, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    //JSONArray jsonArray = new JSONArray(s);
                    //Toast.makeText(profiloGlobetrotter.this, "OK out if", Toast.LENGTH_LONG).show();

                    boolean error = obj.getBoolean("error");

                    if (!error) {
                        //Toast.makeText(profiloGlobetrotter.this, "OK", Toast.LENGTH_LONG).show();


                        nome = obj.getString("nome");
                        cognome = obj.getString("cognome");
                        cellulare.setText(obj.getString("cellulare"));
                        email.setText(obj.getString("email"));
                        //String data = DateConvertor.convertFormat(obj.getString("dataNascita"), "yyyy-MM-dd","dd/MM/yyyy");
                        dataNascita.setText(DateConvertor.convertFormat(obj.getString("dataNascita"), "yyyy-MM-dd","dd/MM/yyyy"));
                        nominativo.setText(nome +" "+cognome);
                        fotoProfilo = obj.getString("foto");
                        if(!fotoProfilo.equals("null")) {
                            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotoProfilo).fit().networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(mImageView);

                        }else{
                            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/img/profilo_default.jpg").fit().networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(mImageView);
                        }
                        //Toast.makeText(ProfiloGlobetrotter.this, fotoProfilo, Toast.LENGTH_LONG).show();
                    }

                    /*
                    byte[] decodedString = Base64.decode(fotoProfilo, Base64.DEFAULT);
                    Bitmap immagineDecodificata = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                    mImageView.setImageBitmap(immagineDecodificata);
                    */

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfiloGlobetrotterEsterno.this, "Exception: " + e, Toast.LENGTH_LONG).show();

                }
            }
        }

        Carica caricamento = new Carica();

        caricamento.execute();

    }
}
