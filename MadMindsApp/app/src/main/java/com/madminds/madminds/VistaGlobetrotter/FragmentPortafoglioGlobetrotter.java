package com.madminds.madminds.VistaGlobetrotter;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import com.braintreepayments.cardform.view.CardForm;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import java.util.HashMap;





public class FragmentPortafoglioGlobetrotter extends Fragment {

    private static final String URL = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreVisualizzaPortafoglio.php";
    private static final String URL_RICARICA ="https://madminds.altervista.org/GestoreGlobetrotter/GestoreRicaricaPortafoglio.php";
    private static final String ID = "id";
    private String id;
    private TextView saldo;
    private CardForm cardForm;
    private EditText ricaricaText;

    private OnFragmentPInteractionListener mListener;

    public FragmentPortafoglioGlobetrotter() {
        // Required empty public constructor
    }


    public static FragmentPortafoglioGlobetrotter newInstance(String id) {
        FragmentPortafoglioGlobetrotter fragment = new FragmentPortafoglioGlobetrotter();
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
        View view =inflater.inflate(R.layout.fragment_portafoglio_globetrotter, container, false);
        Button ricarica = view.findViewById(R.id.btnRicaricaPortafoglio);
        saldo =view.findViewById(R.id.saldo_globetrotter);
        ricarica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // custom dialog
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.pagamento);
                cardForm = dialog.findViewById(R.id.card_form);
                Button ricaricaBtn = dialog.findViewById(R.id.btnRicaricaP);
                ricaricaText=dialog.findViewById(R.id.importo_pagamento);
                cardForm.cardRequired(true)
                        .expirationRequired(true)
                        .cvvRequired(true)
                        .cardholderName(CardForm.FIELD_REQUIRED)
                        .setup(getActivity());
                cardForm.getCvvEditText().setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                dialog.setTitle("Ricarica");

                ricaricaBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (cardForm.isValid() && !ricaricaText.getText().toString().isEmpty()) {
                            AlertDialog.Builder alertBuilder;
                            alertBuilder = new AlertDialog.Builder(getActivity());
                            alertBuilder.setTitle("Conferma ricarica");
                            alertBuilder.setMessage("Numero carta: " + cardForm.getCardNumber() + "\n" +
                                    "Scadenza carta: " + cardForm.getExpirationDateEditText().getText().toString() + "\n" +
                                    "CVV: " + cardForm.getCvv() + "\n" +
                                    "Importo: " + ricaricaText.getText().toString() + " €\n" +
                                    "Proprietario carta: " + cardForm.getCardholderName());
                            alertBuilder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    dialog.dismiss();
                                    ricaricaPortafoglio(ricaricaText.getText().toString());
                                }
                            });
                            alertBuilder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertDialog alertDialog = alertBuilder.create();
                            alertDialog.show();

                        } else {
                            Toast.makeText(getActivity(), "Completa tutti i dati", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                dialog.show();

            }
        });
        visualizzaPortafoglio();

        final SwipeRefreshLayout pullToRefresh = view.findViewById(R.id.pullToRefreshPortafoglioGlob);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                visualizzaPortafoglio();
                pullToRefresh.setRefreshing(false);
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentPInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentPInteractionListener) {
            mListener = (OnFragmentPInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentPInteractionListener {
        // TODO: Update argument type and name
        void onFragmentPInteraction(Uri uri);
    }

    public void visualizzaPortafoglio() {

        class Portafoglio extends AsyncTask<Void, Void, String> {

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
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        String portafoglio = obj.getString("portafoglio");
                        saldo.setText(portafoglio+" €");
                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        Portafoglio portafoglio = new Portafoglio();
        portafoglio.execute();
    }

    public void ricaricaPortafoglio(final String importo) {

        class Portafoglio extends AsyncTask<Void, Void, String> {

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
                params.put("importo", importo);

                //returing the response
                return requestHandler.sendPostRequest(URL_RICARICA, params);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                pdLoading.dismiss();

                try {
                    //converting response to json object
                    JSONObject obj = new JSONObject(s);
                    //if no error in response
                    if (!obj.getBoolean("error")) {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        visualizzaPortafoglio();
                    } else {
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                }
            }
        }

        Portafoglio portafoglio = new Portafoglio();
        portafoglio.execute();
    }
}
