package com.madminds.madminds.VistaCicerone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.madminds.madminds.VistaGlobetrotter.ModificaFotoProfilo;
import com.madminds.madminds.VistaGlobetrotter.ModificaPassword;
import com.madminds.madminds.VistaGlobetrotter.Navigazione;
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

public class ProfiloCicerone extends AppCompatActivity {



    TextView nominativo, email, cellulare, dataNascita, citta, descrizione, dataUpgrade;
    ImageView mImageView;
    private String fotoProfilo;
    public static final String URL_CARICAMENTO = "https://madminds.altervista.org/GestoreCicerone/GestoreVisualizzaProfiloCicerone.php";
    SharedPreferences sharedPreferences;
    Button modificaProfilo, modificaPsw;
    private String nome,cognome;
    private RecyclerView mRecyclerView;
    private ArrayList<Recensione> recensioni;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_cicerone);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);



        nominativo = findViewById(R.id.nomeC);

        descrizione = findViewById(R.id.descrizione_cicerone);
        citta = findViewById(R.id.citta);
        email = findViewById(R.id.emailC);
        cellulare = findViewById(R.id.cellulareC);
        dataNascita = findViewById(R.id.dataNascitaC);
        mImageView = findViewById(R.id.immagineProfiloC);
        dataUpgrade = findViewById(R.id.dataUpgrade);

        modificaProfilo = findViewById(R.id.button_modifica_profiloC);
        modificaPsw = findViewById(R.id.button_modifica_pswC);

        mRecyclerView=findViewById(R.id.recycler_view_recensioni);
        recensioni = new ArrayList<>();

        caricaDati();



        modificaProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfiloCicerone.this, ModificaProfiloCicerone.class);
                intent.putExtra("nome", nome);
                intent.putExtra("cognome", cognome);
                intent.putExtra("email", email.getText().toString());
                intent.putExtra("cell", cellulare.getText().toString());
                intent.putExtra("data", dataNascita.getText().toString());
                intent.putExtra("citta", citta.getText().toString());
                intent.putExtra("descrizione", descrizione.getText().toString());
                intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                startActivity(intent);
                finish();

            }
        });



        modificaPsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfiloCicerone.this, ModificaPassword.class);
                intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                startActivity(intent);
                finish();
            }
        });



        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfiloCicerone.this);
                builder.setCancelable(true);
                builder.setTitle("Modifica immagine profilo");
                builder.setMessage("Vuoi modificare la tua immagine profilo?");
                builder.setPositiveButton("Si",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ProfiloCicerone.this, ModificaFotoProfilo.class);
                                intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                                intent.putExtra("fotoprofilo", fotoProfilo);
                                startActivity(intent);
                                finish();
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


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Navigazione.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void caricaDati(){

        //final Bitmap image;

        class Carica extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(ProfiloCicerone.this);

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

                params.put("idCicerone", sharedPreferences.getString(ID, "0"));

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
                            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotoProfilo).fit().networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_STORE).centerInside().transform(new CircleTrasformation()).into(mImageView);

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
                    Toast.makeText(ProfiloCicerone.this, "Exception: " + e, Toast.LENGTH_LONG).show();

                }
            }
        }

        Carica caricamento = new Carica();
        caricamento.execute();

    }
}


