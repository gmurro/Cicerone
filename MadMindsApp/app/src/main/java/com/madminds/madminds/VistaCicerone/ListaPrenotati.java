package com.madminds.madminds.VistaCicerone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.VistaGlobetrotter.Navigazione;
import com.madminds.madminds.Prenotazione;
import com.madminds.madminds.PrenotazioneAdapter;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.FragmentPrenotazioniEffettuate.COD_ATTIVITA;

public class ListaPrenotati extends AppCompatActivity implements PrenotazioneAdapter.OnPrenotazioneClickListener {

    SharedPreferences sharedPreferences;
    private String codAttivita;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    public static final String URL = "https://madminds.altervista.org/GestoreAttivita/GestoreListaPrenotati.php";

    private RecyclerView mRecyclerView;
    private PrenotazioneAdapter mPrenotazioneAdapter;
    private ArrayList<Prenotazione> mPrenotazioniList;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_prenotati);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        codAttivita = intent.getStringExtra(COD_ATTIVITA);

        mRecyclerView = findViewById(R.id.recycler_view_prenotati);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mPrenotazioniList = new ArrayList<>();
        caricaLista();

        final SwipeRefreshLayout pullToRefresh = findViewById(R.id.pullToRefreshPrenotati);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPrenotazioniList.clear();
                caricaLista();
                pullToRefresh.setRefreshing(false);
            }
        });
    }

    @Override
    public void onPrenotazioneClick(int position) {
        Intent profilo = new Intent(ListaPrenotati.this, ProfiloGlobetrotterEsterno.class);
        Prenotazione clickedPrenotato = mPrenotazioniList.get(position);
        profilo.putExtra("id_globetrotter_esterno", clickedPrenotato.getmCodAttivita() );
        profilo.putExtra("codAttivita", codAttivita );
        startActivity(profilo);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Navigazione.class);
        startActivity(intent);
    }

    public void caricaLista() {

        class ListaPrenotazioni extends AsyncTask<Void, Void, String> {

            ProgressDialog pdLoading = new ProgressDialog(ListaPrenotati.this);
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                //this method will be running on UI thread
                pdLoading.setMessage("\tCaricamento...");
                pdLoading.setCancelable(false);
                pdLoading.show();
            }

            @Override
            protected String doInBackground(Void... voids) {

                //creating request handler object
                RequestHandler requestHandler = new RequestHandler();

                //creating request parameters
                HashMap<String, String> params = new HashMap<>();

                params.put("codAttivita", codAttivita);

                //returing the response
                return requestHandler.sendPostRequest(URL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject response = new JSONObject(s);
                    JSONArray jsonArray = response.getJSONArray("prenotati");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String mNome = hit.getString("nome");
                        String mCognome = hit.getString("cognome");
                        String mCodGlobetrotter = hit.getString("codGlobetrotter");
                        String mNPartecipanti = hit.getString("nPartecipanti");
                        String mUrlImage = hit.getString("foto");

                        String mDataCancellazionePrenotazione= hit.getString("dataCancellazionePrenotazione");
                        String mStato;

                        if(!mUrlImage.equals("null")) {
                            mUrlImage = "https://madminds.altervista.org/GestoreGlobetrotter/"+mUrlImage;
                        }

                        if(!mDataCancellazionePrenotazione.equals("null")) {
                            mStato="Prenotazione annullata il "+ DateConvertor.convertFormat(mDataCancellazionePrenotazione, "yyyy-MM-dd","dd/MM/yyyy");
                        } else {
                            mStato="Prenotazione valida per "+mNPartecipanti+" persone";
                        }
                        mPrenotazioniList.add(new Prenotazione (mNome, mCognome,  null,  mCodGlobetrotter,  mNome+ " "+mCognome,  null,  null,  null,  null,  null,  mUrlImage,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  null,  mDataCancellazionePrenotazione,  null,  mStato));
                    }


                    mPrenotazioneAdapter = new PrenotazioneAdapter(ListaPrenotati.this, mPrenotazioniList);
                    mRecyclerView.setAdapter(mPrenotazioneAdapter);
                    mPrenotazioneAdapter.setOnClickListner(ListaPrenotati.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(ListaPrenotati.this, "Non c'e nessuna prenotazione per questa attività", Toast.LENGTH_LONG).show();
                }
            }
        }

        ListaPrenotazioni res = new ListaPrenotazioni();
        res.execute();
    }
}
