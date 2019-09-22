package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.madminds.madminds.CircleTrasformation;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.R;
import com.madminds.madminds.Recensione;
import com.madminds.madminds.RecensioneAdapter;
import com.madminds.madminds.RequestHandler;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.Navigazione.ID;
import static com.madminds.madminds.VistaGlobetrotter.Navigazione.MY_PREFERENCES;

public class ProfiloCiceroneEsterno extends AppCompatActivity {



    TextView nominativo, email, cellulare, dataNascita, citta, descrizione, dataUpgrade;
    ImageView mImageView;
    private String fotoProfilo,idCiceroneEsterno;
    public static final String URL_CARICAMENTO = "https://madminds.altervista.org/GestoreCicerone/GestoreVisualizzaProfiloCicerone.php";
    SharedPreferences sharedPreferences;
    Button aggiungiRecensione;
    private String nome,cognome;
    private RecyclerView mRecyclerView;
    private ArrayList<Recensione> recensioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_cicerone_esterno);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        idCiceroneEsterno = extras.getString("id_cicerone_esterno");

        nominativo = findViewById(R.id.nomeC);

        descrizione = findViewById(R.id.descrizione_cicerone);
        citta = findViewById(R.id.citta);
        email = findViewById(R.id.emailC);
        cellulare = findViewById(R.id.cellulareC);
        dataNascita = findViewById(R.id.dataNascitaC);
        mImageView = findViewById(R.id.immagineProfiloC);
        dataUpgrade = findViewById(R.id.dataUpgrade);

        aggiungiRecensione = findViewById(R.id.btn_aggiungiRec);

        aggiungiRecensione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfiloCiceroneEsterno.this, AggiungiRecensione.class);
                intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                intent.putExtra("id_cicerone", idCiceroneEsterno );
                startActivity(intent);
                finish();

            }
        });

        mRecyclerView=findViewById(R.id.recycler_view_recensioni);
        recensioni = new ArrayList<>();

        caricaDati();


    }



    private void caricaDati(){

        //final Bitmap image;

        class Carica extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(ProfiloCiceroneEsterno.this);

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

                params.put("idCicerone", idCiceroneEsterno);

                //returing the response
                return requestHandler.sendPostRequest(URL_CARICAMENTO, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);

                    boolean error = obj.getBoolean("error");

                    if (!error) {

                        nome = obj.getString("nome");
                        cognome = obj.getString("cognome");
                        nominativo.setText(nome +" "+cognome);

                        citta.setText(obj.getString("citta"));
                        descrizione.setText(obj.getString("descrizione"));

                        cellulare.setText(obj.getString("cellulare"));
                        email.setText(obj.getString("email"));
                        String data = DateConvertor.convertFormat(obj.getString("dataNascita"), "yyyy-MM-dd","dd/MM/yyyy");
                        dataNascita.setText(data);


                        data = DateConvertor.convertFormat(obj.getString("dataUpgrade"), "yyyy-MM-dd","dd/MM/yyyy");
                        dataUpgrade.setText("Cicerone dal "+data);


                        fotoProfilo = obj.getString("foto");
                        if(!fotoProfilo.equals("null")) {
                            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotoProfilo).fit().networkPolicy(NetworkPolicy.NO_CACHE).
                                    memoryPolicy(MemoryPolicy.NO_STORE).centerInside().transform(new CircleTrasformation()).into(mImageView);

                        }else{
                            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/img/profilo_default.jpg").fit().networkPolicy(NetworkPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(mImageView);
                        }

                        String num = obj.getString("numRecensioni");
                        int numRecensioni = Integer.parseInt(num);


                        for (int i = 0; i < numRecensioni; i++) {

                            String nomeRecensore = obj.getString("nome"+i);
                            String cognomeRecensore = obj.getString("cognome"+i);
                            String votoRecensione = obj.getString("voto"+i);
                            String descrizioneRecensione = obj.getString("descrizione"+i);

                            recensioni.add(new Recensione(nomeRecensore,cognomeRecensore,votoRecensione,descrizioneRecensione));
                        }

                        Context context = getApplicationContext();

                        RecensioneAdapter adapter = new RecensioneAdapter(context,recensioni);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(adapter);


                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ProfiloCiceroneEsterno.this, "Exception: " + e, Toast.LENGTH_LONG).show();

                }
            }
        }

        Carica caricamento = new Carica();
        caricamento.execute();

    }


}