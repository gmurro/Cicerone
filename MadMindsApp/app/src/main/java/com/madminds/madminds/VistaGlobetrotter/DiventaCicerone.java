package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.support.design.widget.Snackbar.make;


public class DiventaCicerone extends Fragment {

    private static final String ID = "id";
    //private static final String CICERONE = "cicerone";
    //private static final String ARG_PARAM2 = "param2";
    private ProgressDialog pDialog;
    private boolean giaCicerone;


    private String mId;
    //private boolean mCicerone;

    private EditText descrizione;
    private EditText citta;

    private OnFragmentDCInteractionListener mListener;

    public DiventaCicerone() {
    }

    public static DiventaCicerone newInstance(String id) {
        DiventaCicerone fragment = new DiventaCicerone();
        Bundle args = new Bundle();
        args.putString(ID, id);
        //args.putBoolean(CICERONE, cicerone);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mId = getArguments().getString(ID);
            //mCicerone = getArguments().getBoolean(CICERONE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_diventa_cicerone, container, false);

        descrizione = view.findViewById(R.id.dc_descrizione);
        citta = view.findViewById(R.id.dc_citta);
        Button invia = view.findViewById(R.id.dc_submit);
        /*
        descrizione.setText(mId);
        if(mCicerone) {
            citta.setText("è cicerone");
        } else {
            citta.setText("NON è cicerone");
        }*/

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diventaCicerone();
                //mListener.onFragmentDCInteraction(giaCicerone);
            }
        });

        return  view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        giaCicerone = false;
        if (context instanceof OnFragmentDCInteractionListener) {
            mListener = (OnFragmentDCInteractionListener) context;
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

    private void displayLoader() {
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    private void diventaCicerone() {
        //Toast.makeText(getActivity(), "ok", Toast.LENGTH_LONG).show();

        String url = "https://www.madminds.altervista.org/GestoreCicerone/GestoreDiventaCicerone.php";

        if(!citta.getText().toString().isEmpty() && !descrizione.getText().toString().isEmpty()) {
            displayLoader();
            RequestQueue queue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
            StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        boolean error = jsonObject.getBoolean("error");
                        boolean giaCic = jsonObject.getBoolean("giaCicerone");
                        if(!error) {

                            if (giaCic) {
                                //Toast.makeText(getActivity(), "gia cicerone", Toast.LENGTH_LONG).show();
                                giaCicerone = true;
                                passData(giaCicerone);
                            } else {
                                giaCicerone = false;
                                passData(giaCicerone);
                            }
                        } else {
                            Toast.makeText(getActivity(), "Errore, riprovare", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), "errore volley", Toast.LENGTH_LONG).show();
                }
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<>();

                    params.put("id", mId);
                    params.put("cittaEsercizio",citta.getText().toString());
                    params.put("descrizioneCicerone", descrizione.getText().toString());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;

                }
            };
            queue.add(sr);
            pDialog.dismiss();

        } else
            make(Objects.requireNonNull(getActivity()).findViewById(R.id.dc), "Completare tutti i campi", Snackbar.LENGTH_LONG).show();


    }


    public void passData(boolean data) {
        mListener.onFragmentDCInteraction(data);
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
    public interface OnFragmentDCInteractionListener {

        void onFragmentDCInteraction(boolean giaCicerone);
    }
}
