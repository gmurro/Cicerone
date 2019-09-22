package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.Prenotazione;
import com.madminds.madminds.PrenotazioneAdapter;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class FragmentPrenotazioniEffettuate extends Fragment implements  PrenotazioneAdapter.OnPrenotazioneClickListener{
    private static final String URL = "https://madminds.altervista.org/GestoreAttivita/GestoreVisualizzaPrenotazioni.php";
    private static final String ID = "id";
    public static final String IMG = "img";
    public static final String TITOLO = "titolo";
    public static final String DESCRIZIONE = "descrizione";
    public static final String NOME_CICERONE = "nomeCicerone";
    public static final String ID_CICERONE = "idCicerone";
    public static final String SCADENZA = "scadenza";
    public static final String COD_ATTIVITA = "codAttivita";
    public static final String LINGUA = "lingua";
    public static final String CITTA = "citta";
    public static final String REGIONE = "regione";
    public static final String MAXPART =  "maxPartecipanti";
    public static final String LATIT = "latit";
    public static final String LONG = "long";
    public static final String DATA = "data";
    public static final String ORA = "ora";
    public static final String COD_PRENOTAZIONE = "codPrenotazione";
    public static final String N_PARTECIPANTI = "nPartecipanti";
    public static final String IMPORTO = "importo";
    public static final String SURPLUS = "surplus";
    public static final String PRESENZA_GPS = "presenzaGps";
    public static final String PRESENZA_QRCODE = "presenzaQrCode";
    public static final String CANC_PRENOTAZIONE = "dataCancellazionePrenotazione";
    public static final String CANC_ATTIVITA = "dataCancellazioneAttivita";
    public static final String STATO = "stato";

    private String id;
    private RecyclerView mRecyclerView;
    private PrenotazioneAdapter mPrenotazioneAdapter;
    private ArrayList<Prenotazione> mPrenotazioniList;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    private OnFragmentPEInteractionListener mListener;

    public FragmentPrenotazioniEffettuate() {
        // Required empty public constructor
    }


    public static FragmentPrenotazioniEffettuate newInstance(String id) {
        FragmentPrenotazioniEffettuate fragment = new FragmentPrenotazioniEffettuate();
        Bundle args = new Bundle();
        args.putString(ID, id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prenotazioni_effettuate, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_attivitaPubblicate);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mPrenotazioniList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        caricaPrenotazioni();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefreshPrenotazioni);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPrenotazioniList.clear();
                caricaPrenotazioni();
                pullToRefresh.setRefreshing(false);
            }
        });
        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentPEInteraction(uri);
        }
    }

    @Override
    public void onPrenotazioneClick(int position) {
        Intent dett_prenotazione = new Intent(getActivity(), DettagliPrenotazione.class);
        Prenotazione clickedPrenotazione = mPrenotazioniList.get(position);

        dett_prenotazione.putExtra(IMG, clickedPrenotazione.getmUrlImage());
        String codAttivita = clickedPrenotazione.getmCodAttivita() + "";
        dett_prenotazione.putExtra(COD_ATTIVITA, codAttivita);
        dett_prenotazione.putExtra(TITOLO, clickedPrenotazione.getmTitolo());
        dett_prenotazione.putExtra(DESCRIZIONE, clickedPrenotazione.getmDescrizione());
        String nomeCicerone = clickedPrenotazione.getmNome() + " " + clickedPrenotazione.getmCognome();
        dett_prenotazione.putExtra(NOME_CICERONE, nomeCicerone);
        String idCicerone = clickedPrenotazione.getmCodCreatore() + "";
        dett_prenotazione.putExtra(ID_CICERONE, idCicerone);
        dett_prenotazione.putExtra(SCADENZA, clickedPrenotazione.getmScadenzaPrenotazioni());
        dett_prenotazione.putExtra(LINGUA, clickedPrenotazione.getmLingua());
        dett_prenotazione.putExtra(CITTA, clickedPrenotazione.getmCitta());
        dett_prenotazione.putExtra(MAXPART, clickedPrenotazione.getmMaxPartecipanti()+ "");
        dett_prenotazione.putExtra(REGIONE, clickedPrenotazione.getmRegione());
        dett_prenotazione.putExtra(LATIT, clickedPrenotazione.getmLatitAppuntamento());
        dett_prenotazione.putExtra(LONG, clickedPrenotazione.getmLongAppuntamento());
        dett_prenotazione.putExtra(DATA, clickedPrenotazione.getmData());
        dett_prenotazione.putExtra(ORA, clickedPrenotazione.getmOraAppuntamento());
        dett_prenotazione.putExtra(COD_PRENOTAZIONE, clickedPrenotazione.getmCodPrenotazione());
        dett_prenotazione.putExtra(N_PARTECIPANTI, clickedPrenotazione.getmNPartecipanti());
        dett_prenotazione.putExtra(IMPORTO, clickedPrenotazione.getmImporto());
        dett_prenotazione.putExtra(SURPLUS, clickedPrenotazione.getmSurplus());
        dett_prenotazione.putExtra(PRESENZA_GPS, clickedPrenotazione.getmPresenzaGps());
        dett_prenotazione.putExtra(PRESENZA_QRCODE, clickedPrenotazione.getmPresenzaQrCode());
        dett_prenotazione.putExtra(CANC_PRENOTAZIONE, clickedPrenotazione.getmDataCancellazionePrenotazione());
        dett_prenotazione.putExtra(CANC_ATTIVITA, clickedPrenotazione.getmDataCancellazioneAttivita());
        dett_prenotazione.putExtra(STATO, clickedPrenotazione.getmStato());

        startActivity(dett_prenotazione);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentPEInteractionListener) {
            mListener = (OnFragmentPEInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentPEInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public void caricaPrenotazioni() {

        class ListaPrenotazioni extends AsyncTask<Void, Void, String> {

            ProgressDialog pdLoading = new ProgressDialog(getActivity());
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

                params.put("codGlobetrotter", id);

                //returing the response
                return requestHandler.sendPostRequest(URL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject response = new JSONObject(s);
                    JSONArray jsonArray = response.getJSONArray("prenotazione");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject hit = jsonArray.getJSONObject(i);

                        String mNome = hit.getString("nome");
                        String mCognome = hit.getString("cognome");
                        String mCodCreatore = hit.getString("creatore");
                        String mCodAttivita = hit.getString("codAttivita");
                        String mTitolo = hit.getString("titolo");
                        String mDescrizione = hit.getString("descrizione");
                        String mLingua = hit.getString("lingua");
                        String mCitta = hit.getString("citta");
                        String mRegione = hit.getString("regione");
                        String mMaxPartecipanti = hit.getString("maxPartecipanti");
                        String mUrlImage = hit.getString("luogo");
                        String mScadenzaPrenotazioni = hit.getString("scadenzaPrenotazione");
                        String mLatitAppuntamento = hit.getString("latitAppuntamento");
                        String mLongAppuntamento = hit.getString("longAppuntamento");
                        String mOraAppuntamento = hit.getString("oraAppuntamento");
                        String mData = hit.getString("data");
                        String mCodPrenotazione= hit.getString("codPrenotazione");
                        String mNPartecipanti= hit.getString("nPartecipanti");
                        String mImporto= hit.getString("importo");
                        String mSurplus= hit.getString("surplus");
                        String mPresenzaGps= hit.getString("presenzaGps");
                        String mPresenzaQrCode= hit.getString("presenzaQrCode");
                        String mDataCancellazionePrenotazione= hit.getString("dataCancellazionePrenotazione");
                        String mDataCancellazioneAttivita= hit.getString("dataCancellazioneAttivita");
                        String mStato;

                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date(System.currentTimeMillis());
                        String dataCorrente = formatter.format(date).toString();

                        if(!mDataCancellazionePrenotazione.equals("null")) {
                            mStato="Prenotazione annullata il "+ DateConvertor.convertFormat(mDataCancellazionePrenotazione, "yyyy-MM-dd","dd/MM/yyyy");
                        } else if(!mDataCancellazioneAttivita.equals("null")) {
                            mStato="Attività annullata dal Cicerone il "+DateConvertor.convertFormat(mDataCancellazioneAttivita, "yyyy-MM-dd","dd/MM/yyyy");
                        } else if(mData.compareTo(dataCorrente) < 0 ) {
                            mStato="Attività conclusa";
                        } else {
                            mStato="Attività prenotata";
                        }
                        mPrenotazioniList.add(new Prenotazione (mNome, mCognome,  mCodCreatore,  mCodAttivita,  mTitolo,  mDescrizione,  mLingua,  mCitta,  mRegione,  mMaxPartecipanti,  mUrlImage,  mScadenzaPrenotazioni,  mLatitAppuntamento,  mLongAppuntamento,  mData,  mOraAppuntamento,  mCodPrenotazione,  mNPartecipanti,  mImporto,  mSurplus,  mPresenzaGps,  mPresenzaQrCode,  mDataCancellazionePrenotazione,  mDataCancellazioneAttivita,  mStato));
                    }


                    mPrenotazioneAdapter = new PrenotazioneAdapter(getActivity(), mPrenotazioniList);
                    mRecyclerView.setAdapter(mPrenotazioneAdapter);
                    mPrenotazioneAdapter.setOnClickListner(FragmentPrenotazioniEffettuate.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Non hai prenotato nessuna attività", Toast.LENGTH_LONG).show();
                }
            }
        }

        ListaPrenotazioni prenotazioni = new ListaPrenotazioni();
        prenotazioni.execute();
    }

    public interface OnFragmentPEInteractionListener {
        // TODO: Update argument type and name
        void onFragmentPEInteraction(Uri uri);
    }


}
