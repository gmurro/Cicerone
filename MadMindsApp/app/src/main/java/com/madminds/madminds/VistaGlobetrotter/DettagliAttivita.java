package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DefaultItemAnimator;

import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.R;
import com.madminds.madminds.FasciaPrezzo;
import com.madminds.madminds.FasciaPrezzoAdapter;
import com.madminds.madminds.RequestHandler;
import com.madminds.madminds.VistaCicerone.ProfiloCicerone;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_DESCRIZIONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_ID_CICERONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_NOME_CICERONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_SCADENZA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_TITOLO;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_URL;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.COD_ATTIVITA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LINGUA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_CITTA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_MAXPART;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_REGIONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LATIT;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LONG;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_DATA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_ORA;

public class DettagliAttivita extends AppCompatActivity {


    private Button prenotazione;
    private ImageView posizione;
    private TextView textViewFascePrezzi,textViewNomeCicerone;
    private RecyclerView mRecyclerView;
    private ArrayList<FasciaPrezzo> fascePrezzi;
    SharedPreferences sharedPreferences;
    private String codAttivita;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    public static final String URL_PREZZI = "https://madminds.altervista.org/GestoreAttivita/GestoreGetFascePrezzi.php";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_attivita);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);


        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(EXTRA_URL);
        String titolo = intent.getStringExtra(EXTRA_TITOLO);
        String descrizione = intent.getStringExtra(EXTRA_DESCRIZIONE);
        String nomeCicerone = intent.getStringExtra(EXTRA_NOME_CICERONE);
        final String idCicerone = intent.getStringExtra(EXTRA_ID_CICERONE);
        String scadenza = intent.getStringExtra(EXTRA_SCADENZA);
        codAttivita = intent.getStringExtra(COD_ATTIVITA);
        String lingua = intent.getStringExtra(EXTRA_LINGUA);
        String citta = intent.getStringExtra(EXTRA_CITTA);
        String maxPartecipanti = intent.getStringExtra(EXTRA_MAXPART);
        String regione = intent.getStringExtra(EXTRA_REGIONE);
        final String latit = intent.getStringExtra(EXTRA_LATIT);
        final String longit = intent.getStringExtra(EXTRA_LONG);
        String data = intent.getStringExtra(EXTRA_DATA);
        String ora = intent.getStringExtra(EXTRA_ORA);


        ImageView imageView = findViewById(R.id.attivita_dettaglio_foto);
        TextView textViewTitolo = findViewById(R.id.attivita_dettaglio_titolo);
        TextView textViewDescrizione = findViewById(R.id.attivita_dett_descrizione);
        textViewNomeCicerone = findViewById(R.id.attivita_dett_nome_cicerone);
        TextView textViewScadenza = findViewById(R.id.attivita_dett_scadenza);
        TextView textViewData = findViewById(R.id.data);
        TextView textViewOra = findViewById(R.id.ora);
        TextView textViewCitta = findViewById(R.id.citta);
        TextView textViewMaxPartecipanti = findViewById(R.id.maxpartecipanti);
        LinearLayout blocco_cicerone = findViewById(R.id.blocco_dati_cicerone);
        textViewFascePrezzi =findViewById(R.id.fasceprezzo);
        mRecyclerView=findViewById(R.id.recycler_view_fasce_prezzo);

        prenotazione = findViewById(R.id.button_prenota_attivita);
        posizione = findViewById(R.id.posizione);

        fascePrezzi = new ArrayList<>();

        Picasso.get().load(imageUrl).fit().centerInside().into(imageView);
        textViewTitolo.setText(titolo);
        textViewDescrizione.setText("Descrizione:\n" + descrizione);
        textViewNomeCicerone.setText("Attivita di: " + nomeCicerone);
        textViewScadenza.setText("Data scadenza prenotazioni: " + DateConvertor.convertFormat(scadenza, "yyyy-MM-dd", "dd/MM/yyyy"));


        String newDateString = DateConvertor.convertFormat(data, "yyyy-MM-dd", "dd/MM/yyyy");
        textViewData.setText(newDateString);
        textViewOra.setText(ora);
        textViewCitta.setText(citta+", "+regione+", Lingua "+lingua );
        textViewMaxPartecipanti.setText("Max partecipanti: "+maxPartecipanti);

        caricaFascePrezzi();


        blocco_cicerone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sharedPreferences.getString(ID, "0").equals(idCicerone)){
                    //Apertura del proprio profilo Cicerone

                    Intent intent = new Intent(DettagliAttivita.this, ProfiloCicerone.class);
                    intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                    startActivity(intent);
                    finish();
                }else {
                    //Apertura del profilo Cicerone di qualcun'altro

                    Intent intent = new Intent(DettagliAttivita.this, ProfiloCiceroneEsterno.class);
                    intent.putExtra("id_cicerone_esterno", idCicerone );
                    startActivity(intent);
                    finish();
                }

            }
        });


        prenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getString(ID, "0").equals(idCicerone)) {
                    Toast.makeText(getApplicationContext(),"Non puoi prenotarti ad una tua stessa attività!", Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(DettagliAttivita.this, PrenotaAttivita.class);
                    intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                    intent.putExtra("codAttivita", codAttivita);
                    startActivity(intent);
                    finish();
                }
            }
        });

        posizione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double latitude = Double.valueOf(latit);
                double longitude = Double.valueOf(longit);
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f()", latitude, longitude, latitude, longitude, "Punto di incontro");
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
    }

    public void caricaFascePrezzi() {
        class FascePrezzi extends AsyncTask<Void, Void, String> {
            ProgressDialog pdLoading = new ProgressDialog(DettagliAttivita.this);

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

                params.put("codAttivita", codAttivita);

                //returing the response
                return requestHandler.sendPostRequest(URL_PREZZI, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);
                    boolean error = obj.getBoolean("error");

                    if (!error) {

                        JSONArray jsonArray = obj.getJSONArray("prezzi");


                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject hit = jsonArray.getJSONObject(i);

                            String minPartecipanti = hit.getInt("minPartecipanti")+"";
                            String maxPartecipanti = hit.getInt("maxPartecipanti")+ "";
                            String prezzo = hit.getString("prezzo");

                            fascePrezzi.add(new FasciaPrezzo(minPartecipanti,maxPartecipanti,prezzo));
                        }
                        FasciaPrezzoAdapter adapter = new FasciaPrezzoAdapter(fascePrezzi);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                        mRecyclerView.setAdapter(adapter);


                    }else {
                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(DettagliAttivita.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        FascePrezzi prezzi = new FascePrezzi();
        prezzi.execute();
    }

}
