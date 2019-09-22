package com.madminds.madminds.VistaGlobetrotter;

import com.madminds.madminds.R;
import com.madminds.madminds.Attivita;
import com.madminds.madminds.AttivitaAdapter;
import com.madminds.madminds.RequestHandler;
import com.savvi.rangedatepicker.CalendarPickerView;

import android.app.Dialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentHome.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment  implements  AttivitaAdapter.OnAttivitaClickListener{

    public static final String EXTRA_URL = "imageUrl";
    public static final String EXTRA_TITOLO = "titolo";
    public static final String EXTRA_DESCRIZIONE = "descrizione";
    public static final String EXTRA_NOME_CICERONE = "nomeCicerone";
    public static final String EXTRA_ID_CICERONE = "idCicerone";
    public static final String EXTRA_SCADENZA = "scadenza";
    public static final String COD_ATTIVITA = "codAttivita";
    public static final String EXTRA_LINGUA = "lingua";
    public static final String EXTRA_CITTA = "citta";
    public static final String EXTRA_REGIONE = "regione";
    public static final String EXTRA_MAXPART =  "maxPartecipanti";
    public static final String EXTRA_LATIT = "latit";
    public static final String EXTRA_LONG = "long";
    public static final String EXTRA_DATA = "data";
    public static final String EXTRA_ORA = "ora";

    private static String URL_HOME = "https://www.madminds.altervista.org/GestoreAttivita/GestoreHomePage.php";
    private static String URL_RICERCA = "https://madminds.altervista.org/GestoreAttivita/GestoreRicercaAttivita.php";

    private View viewFragment;
    private RecyclerView mRecyclerView;
    private AttivitaAdapter mAttivitaAdapter;
    private ArrayList<Attivita> mAttivitaList;
    private RequestQueue mRequestQueue;
    private ProgressDialog pDialog;
    private CalendarPickerView calendar;

    private EditText nPartecipanti;
    private EditText search;
    private EditText data;

    private boolean searchBarAdded=false;

    private OnFragmentInteractionListener mListener;

    public FragmentHome() {
        // Required empty public constructor
    }


    public static FragmentHome newInstance() {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_fragment_home, container, false);
        //view.findViewById(R.id.recycler_view_attivita);
        mRecyclerView = viewFragment.findViewById(R.id.recycler_view_attivita);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(viewFragment.getContext()));
        mAttivitaList = new ArrayList<>();
        mRequestQueue = Volley.newRequestQueue(getActivity());
        displayLoader();
        visualizzaAttivita();

        final SwipeRefreshLayout pullToRefresh = viewFragment.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAttivitaList.clear();
                visualizzaAttivita();
                pullToRefresh.setRefreshing(false);
            }
        });

        search = viewFragment.findViewById(R.id.searchView);
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {

                LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View viewSearch = vi.inflate(R.layout.filtri_ricerca, null);
                final LinearLayout search_bar = viewFragment.findViewById(R.id.search_bar);


                if (hasFocus==true && searchBarAdded== false) {
                    searchBarAdded=true;
                    search.setHint("Città");
                    search_bar.addView(viewSearch);
                    //Toast.makeText(getContext(), "Got the focus", Toast.LENGTH_LONG).show();
                }


                nPartecipanti = viewFragment.findViewById(R.id.nPartecipanti_search);

                Button annulla = viewSearch.findViewById(R.id.button_annulla_cerca);
                annulla.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        searchBarAdded=false;
                        search.setHint("Cerca...");
                        search.setText("");
                        search.clearFocus();
                        search_bar.removeAllViews();

                        mAttivitaList.clear();
                        displayLoader();
                        visualizzaAttivita();
                    }
                });

                final Calendar nextYear = Calendar.getInstance();
                nextYear.add(Calendar.YEAR, 10);

                final Calendar lastYear = Calendar.getInstance();
                lastYear.add(Calendar.YEAR, - 10);

                data = viewFragment.findViewById(R.id.data_search);
                // Show a datepicker when the dateButton is clicked
                data.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // custom dialog

                        final Dialog dialog = new Dialog(getActivity());
                        dialog.setContentView(R.layout.scegli_data);
                        dialog.setTitle("Scegli date");

                        calendar = dialog.findViewById(R.id.calendar_view);

                        calendar.init(lastYear.getTime(), nextYear.getTime(), new SimpleDateFormat("MMMM, yyyy", Locale.getDefault())) //
                                .inMode(CalendarPickerView.SelectionMode.RANGE);

                        calendar.scrollToDate(new Date());

                        Button dialogButton = (Button) dialog.findViewById(R.id.button_seleziona_data);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String myFormat = "yyyy-MM-dd"; //In which you need put here
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                                try {
                                    data.setText(sdf.format(calendar.getSelectedDates().toArray()[0]) + " / " + sdf.format(calendar.getSelectedDates().toArray()[calendar.getSelectedDates().size() - 1]));
                                }catch (ArrayIndexOutOfBoundsException e) {
                                    Toast.makeText(getActivity(), "Selezionare almeno una data", Toast.LENGTH_LONG).show();
                                }
                                dialog.dismiss();
                            }
                        });

                        dialog.show();
                    }
                });

                Button cerca = viewFragment.findViewById(R.id.button_cerca);
                cerca.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(search.getText().toString().equals("") || nPartecipanti.getText().toString().equals("") || data.getText().toString().equals("")) {
                            //Toast.makeText(getActivity(), "Riempire tutti campi per filtrare i risultati "+search.getText().toString()+"/"+ nPartecipanti.getText().toString()+"/"+ data.getText().toString(), Toast.LENGTH_LONG).show();
                            Toast.makeText(getActivity(), "Riempire tutti campi per filtrare i risultati", Toast.LENGTH_LONG).show();
                        } else {
                            //Toast.makeText(getActivity(), "Ricerca"+search.getText()+"/"+ nPartecipanti.getText()+"/"+ data.getText(), Toast.LENGTH_LONG).show();
                            String[] date = data.getText().toString().split(" / ");
                            mAttivitaList.clear();
                            ricercaAttivita(search.getText().toString(),nPartecipanti.getText().toString(),date[0],date[1]);
                        }
                    }
                });
            }
        });

        return viewFragment;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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

    @Override
    public void onAttivitaClick(int position) {
        Intent dett_attivita = new Intent(getActivity(), DettagliAttivita.class);
        Attivita clickedAttivita = mAttivitaList.get(position);

        dett_attivita.putExtra(EXTRA_URL, clickedAttivita.getmUrlImage());
        String codAttivita = clickedAttivita.getmCodAttivita() + "";
        dett_attivita.putExtra(COD_ATTIVITA, codAttivita);
        dett_attivita.putExtra(EXTRA_TITOLO, clickedAttivita.getmTitolo());
        dett_attivita.putExtra(EXTRA_DESCRIZIONE, clickedAttivita.getmDescrizione());
        String nomeCicerone = clickedAttivita.getmNome() + " " + clickedAttivita.getmCognome();
        dett_attivita.putExtra(EXTRA_NOME_CICERONE, nomeCicerone);
        String idCicerone = clickedAttivita.getmCodCreatore() + "";
        dett_attivita.putExtra(EXTRA_ID_CICERONE, idCicerone);
        dett_attivita.putExtra(EXTRA_SCADENZA, clickedAttivita.getmScadenzaPrenotazioni());
        dett_attivita.putExtra(EXTRA_LINGUA, clickedAttivita.getmLingua());
        dett_attivita.putExtra(EXTRA_CITTA, clickedAttivita.getmCitta());
        dett_attivita.putExtra(EXTRA_MAXPART, clickedAttivita.getmMaxPartecipanti()+ "");
        dett_attivita.putExtra(EXTRA_REGIONE, clickedAttivita.getmRegione());
        dett_attivita.putExtra(EXTRA_LATIT, clickedAttivita.getmLatitAppuntamento());
        dett_attivita.putExtra(EXTRA_LONG, clickedAttivita.getmLongAppuntamento());
        dett_attivita.putExtra(EXTRA_DATA, clickedAttivita.getmData());
        dett_attivita.putExtra(EXTRA_ORA, clickedAttivita.getmOraAppuntamento());

        startActivity(dett_attivita);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {

        void onFragmentInteraction(Uri uri);
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void visualizzaAttivita() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_HOME, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.dismiss();
                        try {
                            //JSONObject jsonObject = response.getJSONObject()
                            JSONArray jsonArray = response.getJSONArray("attivita");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject hit = jsonArray.getJSONObject(i);

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

                                mAttivitaList.add(new Attivita(mNome, mCognome, mCodCreatore,
                                        mCodAttivita, mTitolo, mDescrizione, mLingua, mCitta,
                                        mRegione, mMaxPartecipanti, mUrlImage, mScadenzaPrenotazioni, mLatitAppuntamento, mLongAppuntamento, mData, mOraAppuntamento ));
                            }

                            mAttivitaAdapter = new AttivitaAdapter(getActivity(), mAttivitaList);
                            mRecyclerView.setAdapter(mAttivitaAdapter);
                            mAttivitaAdapter.setOnClickListner(FragmentHome.this);

                        } catch (JSONException e) {

                            Toast.makeText(getActivity(), "Errore nel caricamento della home page", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String message = "Errore connessione";
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        mRequestQueue.add(request);
    }

    public void ricercaAttivita(final String citta,final String nPartecipanti,final String dataMin,final String dataMax) {

        class Ricerca extends AsyncTask<Void, Void, String> {

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

                params.put("citta", citta);
                params.put("nPartecipanti", nPartecipanti);
                params.put("dataMin", dataMin);
                params.put("dataMax", dataMax);

                //returing the response
                return requestHandler.sendPostRequest(URL_RICERCA, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {

                    JSONObject response = new JSONObject(s);
                        JSONArray jsonArray = response.getJSONArray("attivita");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject hit = jsonArray.getJSONObject(i);

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

                            mAttivitaList.add(new Attivita(mNome, mCognome, mCodCreatore,
                                    mCodAttivita, mTitolo, mDescrizione, mLingua, mCitta,
                                    mRegione, mMaxPartecipanti, mUrlImage, mScadenzaPrenotazioni, mLatitAppuntamento, mLongAppuntamento, mData, mOraAppuntamento));
                        }


                        mAttivitaAdapter = new AttivitaAdapter(getActivity(), mAttivitaList);
                        mRecyclerView.setAdapter(mAttivitaAdapter);
                        mAttivitaAdapter.setOnClickListner(FragmentHome.this);

                } catch (JSONException e) {
                    e.printStackTrace();
                    mAttivitaAdapter = new AttivitaAdapter(getActivity(), mAttivitaList);
                    mRecyclerView.setAdapter(mAttivitaAdapter);
                    Toast.makeText(getActivity(), "Nessuna attività corrisponde ai parametri cercati", Toast.LENGTH_LONG).show();
                }
            }
        }

        Ricerca ricerca = new Ricerca();
        ricerca.execute();
    }


}
