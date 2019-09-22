package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
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
import com.madminds.madminds.R;
import com.madminds.madminds.Recensione;
import com.madminds.madminds.RecensioneAdapter;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.madminds.madminds.VistaGlobetrotter.Navigazione.ID;


public class FragmentRecensioniScritte extends Fragment implements RecensioneAdapter.OnRecensioneClickListener{

    private static final String URL = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreVisualizzaRecensioniGlobetrotter.php";

    public static final String STATO = "stato";

    private String id;
    private RecyclerView mRecyclerView;
    private RecensioneAdapter mRecensioneAdapter;
    private ArrayList<Recensione> mRecensioniList;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;

    private OnFragmentPEInteractionListener mListener;

    public FragmentRecensioniScritte() {
        // Required empty public constructor
    }


    public static FragmentRecensioniScritte newInstance(String id) {
        FragmentRecensioniScritte fragment = new FragmentRecensioniScritte();
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
        View view = inflater.inflate(R.layout.fragment_recensioni_scritte, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_recensioni);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        mRecensioniList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());

        //Metodo di carica
        caricaRecensioni();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefreshRecensioni);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRecensioniList.clear();
                //METODO
                caricaRecensioni();
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

    @Override
    public void onRecensioneClick(int position) {

    }


    public void caricaRecensioni() {

        class ListaRecensioni extends AsyncTask<Void, Void, String> {

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

                params.put("idGlobetrotter", id);

                //returing the response
                return requestHandler.sendPostRequest(URL, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject obj = new JSONObject(s);

                    String num = obj.getString("numRecensioni");
                    int numRecensioni = Integer.parseInt(num);


                    for (int i = 0; i < numRecensioni; i++) {

                        String nomeRecensore = obj.getString("nome"+i);
                        String cognomeRecensore = obj.getString("cognome"+i);
                        String votoRecensione = obj.getString("voto"+i);
                        String descrizioneRecensione = obj.getString("descrizione"+i);

                        mRecensioniList.add(new Recensione(nomeRecensore,cognomeRecensore,votoRecensione,descrizioneRecensione));
                    }


                    mRecensioneAdapter = new RecensioneAdapter(getActivity(), mRecensioniList);
                    mRecyclerView.setAdapter(mRecensioneAdapter);
                    mRecensioneAdapter.setOnClickListner(FragmentRecensioniScritte.this);


                    /*
                    RecensioneAdapter adapter = new RecensioneAdapter(mRecensioniList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(adapter);
                    */


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Non hai scritto alcuna recensione", Toast.LENGTH_LONG).show();
                }
            }
        }

        ListaRecensioni res = new ListaRecensioni();
        res.execute();
    }



    public interface OnFragmentPEInteractionListener {
        // TODO: Update argument type and name
        void onFragmentPEInteraction(Uri uri);
    }


}