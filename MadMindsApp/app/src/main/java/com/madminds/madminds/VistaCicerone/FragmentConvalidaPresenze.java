package com.madminds.madminds.VistaCicerone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.madminds.madminds.PointsOverlayView;
import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class FragmentConvalidaPresenze extends Fragment implements QRCodeReaderView.OnQRCodeReadListener {

    private static final String URL = "https://madminds.altervista.org/GestoreAttivita/GestorePresenzaQrCode.php";
    private static final String ID = "id";
    private String id;

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private View mainLayout;

    private TextView resultTextView;
    private QRCodeReaderView qrCodeReaderView;
    private ImageView flashlight;
    private PointsOverlayView pointsOverlayView;
    private boolean flashOn = false;

    private OnFragmentCPInteractionListener mListener;

    public FragmentConvalidaPresenze() {
        // Required empty public constructor
    }


    public static FragmentConvalidaPresenze newInstance(String id) {
        FragmentConvalidaPresenze fragment = new FragmentConvalidaPresenze();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_convalida_presenze, container, false);

        mainLayout = (ViewGroup) view.findViewById(R.id.main_layout);
        initQRCodeReaderView();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentCPInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCPInteractionListener) {
            mListener = (OnFragmentCPInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentCPInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



    public interface OnFragmentCPInteractionListener {
        // TODO: Update argument type and name
        void onFragmentCPInteraction(Uri uri);
    }


    @Override public void onResume() {
        super.onResume();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT)
                    .show();
        }
    }


    @Override public void onQRCodeRead(String text, PointF[] points) {
        convalidaPresenza(text);
        pointsOverlayView.setPoints(points);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
            Toast.makeText(getActivity(),"Camera access is required to display the camera preview.", Toast.LENGTH_LONG).show();
            /*Snackbar.make(mainLayout, "Camera access is required to display the camera preview.",
                    Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override public void onClick(View view) {
                    ActivityCompat.requestPermissions(getActivity(), new String[] {
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();*/
        } else {
            Toast.makeText(getActivity(),"Permission is not available. Requesting camera permission.", Toast.LENGTH_LONG).show();

            /*
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.",
                    Snackbar.LENGTH_SHORT).show();*/
            ActivityCompat.requestPermissions(getActivity(), new String[] {
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }

    private void initQRCodeReaderView() {
        //View content = getLayoutInflater().inflate(R.layout.fragment_convalida_presenze, mainLayout, true);
        View content = mainLayout;

        qrCodeReaderView = (QRCodeReaderView) content.findViewById(R.id.qrdecoderview);
        flashlight = (ImageView) content.findViewById(R.id.flashlight);

        pointsOverlayView = (PointsOverlayView) content.findViewById(R.id.points_overlay_view);

        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        flashlight.setOnClickListener( new View.OnClickListener() {
            public void onClick(View v) {
                if(flashOn) {
                    flashOn = false;
                    qrCodeReaderView.setTorchEnabled(false);
                    flashlight.setImageResource(R.drawable.flash_on);
                } else {
                    flashOn = true;
                    qrCodeReaderView.setTorchEnabled(true);
                    flashlight.setImageResource(R.drawable.flash_off);
                }
            }
        });

        qrCodeReaderView.startCamera();
    }


    public void convalidaPresenza(final String codPrenotazione) {

            class Prenota extends AsyncTask<Void, Void, String> {

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("codPrenotazione", codPrenotazione);
                    params.put("codCicerone", id);

                    //returing the response
                    return requestHandler.sendPostRequest(URL, params);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    try {
                        //converting response to json object
                        JSONObject obj = new JSONObject(s);
                        //if no error in response
                        Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                    }
                }
            }

            Prenota prenotazione = new Prenota();
            prenotazione.execute();


    }
}
