package com.madminds.madminds.VistaCicerone;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.Attivita;
import com.madminds.madminds.AttivitaPubblicateAdapter;
import com.madminds.madminds.NumeroMaxAttivita;
import com.madminds.madminds.DateConvertor;
import com.madminds.madminds.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.COD_ATTIVITA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_CITTA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_DATA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_DESCRIZIONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_ID_CICERONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LATIT;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LINGUA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_LONG;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_MAXPART;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_NOME_CICERONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_ORA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_REGIONE;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_SCADENZA;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_TITOLO;
import static com.madminds.madminds.VistaGlobetrotter.FragmentHome.EXTRA_URL;


public class Fragment_attivitaPubblicate extends Fragment implements AttivitaPubblicateAdapter.OnAttivitaClickListener{
    private static final String ARG_PARAM1 = "id";
    private static final String URL_ATTIVITA = "https://www.madminds.altervista.org/GestoreAttivita/GestoreAttivitaPubblicate.php";

    private String id;
    private RecyclerView mRecyclerView;
    private AttivitaPubblicateAdapter mAttivitaAdapter;
    private ArrayList<Attivita> mAttivitaList;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;
    private OnFragmentInteractionListener mListener;

    public Fragment_attivitaPubblicate() {
        // Required empty public constructor
    }


    public static Fragment_attivitaPubblicate newInstance(String id) {
        Fragment_attivitaPubblicate fragment = new Fragment_attivitaPubblicate();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, id);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragment_attivita_pubblicate, container, false);

        //view.findViewById(R.id.recycler_view_attivita);
        mRecyclerView = view.findViewById(R.id.recycler_view_attivitaPubblicate);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mAttivitaList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        displayLoader();
        caricaAttivita();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefresh1);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAttivitaList.clear();
                caricaAttivita();
                pullToRefresh.setRefreshing(false);
            }
        });

        FloatingActionButton fab = view.findViewById(R.id.inserisciAttivita);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NumeroMaxAttivita.class);
                startActivity(intent);
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
            }
        });

        //textView.setText(id);

        return view;
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentAttivitaPubblicate(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    private void caricaAttivita() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_ATTIVITA, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.i("Myresponse",""+response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray attivita = jsonObject.getJSONArray("attivita");

                    for (int i = 0; i < attivita.length(); i++) {
                        JSONObject hit = attivita.getJSONObject(i);

                        String mNome = hit.getString("nome");
                        String mCognome = hit.getString("cognome");
                        int mCodCreatore = hit.getInt("creatore");
                        int mCodAttivita = hit.getInt("codAttivita");
                        String mTitolo = hit.getString("titolo");
                        String mDescrizione = hit.getString("descrizione");
                        String mLingua = hit.getString("lingua");
                        String mCitta = hit.getString("citta");
                        String mRegione = hit.getString("regione");
                        int mMaxPartecipanti = hit.getInt("maxPartecipanti");
                        String mUrlImage = hit.getString("luogo");
                        String mScadenzaPrenotazioni = hit.getString("scadenzaPrenotazione");
                        String mLatitAppuntamento = hit.getString("latitAppuntamento");
                        String mLongAppuntamento = hit.getString("longAppuntamento");
                        String mOraAppuntamento = hit.getString("oraAppuntamento");
                        String mData = hit.getString("data");
                        String mDataCancellazione = hit.getString("dataCancellazione");

                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                        Date date = new Date(System.currentTimeMillis());
                        String dataCorrente = formatter.format(date).toString();

                        String mStato;
                        if(!mDataCancellazione.equals("null")) {
                            mStato="Attività annullata il "+ DateConvertor.convertFormat(mDataCancellazione, "yyyy-MM-dd","dd/MM/yyyy");
                        } else if(mData.compareTo(dataCorrente) < 0 ) {
                            mStato="Attività conclusa";
                        } else {
                            mStato="Attività pubblicata";
                        }


                        Attivita attivitaPubblicata = new Attivita(mNome, mCognome, mCodCreatore,
                                mCodAttivita, mTitolo, mDescrizione, mLingua, mCitta,
                                mRegione, mMaxPartecipanti, mUrlImage, mScadenzaPrenotazioni, mLatitAppuntamento, mLongAppuntamento, mData, mOraAppuntamento );
                        attivitaPubblicata.setmStatoCancellazione(mStato,mDataCancellazione);
                        mAttivitaList.add(attivitaPubblicata);
                    }

                    mAttivitaAdapter = new AttivitaPubblicateAdapter(getActivity(), mAttivitaList);
                    mRecyclerView.setAdapter(mAttivitaAdapter);
                    mAttivitaAdapter.setOnClickListner(Fragment_attivitaPubblicate.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart",""+error);
                Toast.makeText(getActivity(), "Errore durante il caricamento, Riprovare", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                param.put("codCicerone",id);

                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onAttivitaClick(int position) {
        Intent dett_Modattivita = new Intent(getActivity(), MostraDettModificaAttivita.class);
        Attivita clickedAttivita = mAttivitaList.get(position);

        dett_Modattivita.putExtra(EXTRA_URL, clickedAttivita.getmUrlImage());
        String codAttivita = clickedAttivita.getmCodAttivita() + "";
        dett_Modattivita.putExtra(COD_ATTIVITA, codAttivita);
        dett_Modattivita.putExtra(EXTRA_TITOLO, clickedAttivita.getmTitolo());
        dett_Modattivita.putExtra(EXTRA_DESCRIZIONE, clickedAttivita.getmDescrizione());
        String nomeCicerone = clickedAttivita.getmNome() + " " + clickedAttivita.getmCognome();
        dett_Modattivita.putExtra(EXTRA_NOME_CICERONE, nomeCicerone);
        String idCicerone = clickedAttivita.getmCodCreatore() + "";
        dett_Modattivita.putExtra(EXTRA_ID_CICERONE, idCicerone);
        dett_Modattivita.putExtra(EXTRA_SCADENZA, clickedAttivita.getmScadenzaPrenotazioni());
        dett_Modattivita.putExtra(EXTRA_LINGUA, clickedAttivita.getmLingua());
        dett_Modattivita.putExtra(EXTRA_CITTA, clickedAttivita.getmCitta());
        dett_Modattivita.putExtra(EXTRA_MAXPART, clickedAttivita.getmMaxPartecipanti()+ "");
        dett_Modattivita.putExtra(EXTRA_REGIONE, clickedAttivita.getmRegione());
        dett_Modattivita.putExtra(EXTRA_LATIT, clickedAttivita.getmLatitAppuntamento());
        dett_Modattivita.putExtra(EXTRA_LONG, clickedAttivita.getmLongAppuntamento());
        dett_Modattivita.putExtra(EXTRA_DATA, clickedAttivita.getmData());
        dett_Modattivita.putExtra(EXTRA_ORA, clickedAttivita.getmOraAppuntamento());
        dett_Modattivita.putExtra("dataCancellazione", clickedAttivita.getmDataCancellazione());
        dett_Modattivita.putExtra("stato", clickedAttivita.getmStato());

        startActivity(dett_Modattivita);
    }

    /*
    @Override
    public void onAttivitaClick(int position) {
        Intent modificaAttivita = new Intent(getActivity(), ModificaAttivita.class);
        Attivita clickedAttivita = mAttivitaList.get(position);

        String codAttivita = clickedAttivita.getmCodAttivita() + "";
        modificaAttivita.putExtra(COD_ATTIVITA, codAttivita);
        modificaAttivita.putExtra(EXTRA_DESCRIZIONE, clickedAttivita.getmDescrizione());
        modificaAttivita.putExtra(EXTRA_CITTA, clickedAttivita.getmCitta());
        modificaAttivita.putExtra(EXTRA_LATIT, clickedAttivita.getmLatitAppuntamento());
        modificaAttivita.putExtra(EXTRA_LONG, clickedAttivita.getmLongAppuntamento());
        modificaAttivita.putExtra(EXTRA_ORA, clickedAttivita.getmOraAppuntamento());

        startActivity(modificaAttivita);
    }*/

    public interface OnFragmentInteractionListener {
        void onFragmentAttivitaPubblicate(Uri uri);
    }
}
