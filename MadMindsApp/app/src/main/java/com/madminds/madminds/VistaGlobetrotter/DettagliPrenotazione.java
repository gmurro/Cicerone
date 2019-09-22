package com.madminds.madminds.VistaGlobetrotter;

        import android.Manifest;
        import android.app.Dialog;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.content.pm.PackageManager;
        import android.graphics.Bitmap;
        import android.graphics.drawable.ColorDrawable;
        import android.location.Criteria;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.net.Uri;
        import android.os.AsyncTask;
        import android.support.v4.app.ActivityCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.view.Window;
        import android.widget.Button;
        import android.widget.ImageView;
        import android.widget.LinearLayout;
        import android.widget.RelativeLayout;
        import android.widget.TextView;
        import android.widget.Toast;


        import com.madminds.madminds.DateConvertor;
        import com.madminds.madminds.R;
        import com.madminds.madminds.RequestHandler;
        import com.madminds.madminds.VistaCicerone.ProfiloCicerone;
        import com.squareup.picasso.Picasso;

        import org.json.JSONException;
        import org.json.JSONObject;

        import net.glxn.qrgen.android.QRCode;

        import java.math.BigDecimal;
        import java.text.SimpleDateFormat;
        import java.util.*;
        import static com.madminds.madminds.VistaGlobetrotter.FragmentPrenotazioniEffettuate.*;

public class DettagliPrenotazione extends AppCompatActivity implements LocationListener {

    //attributi per gps
    public  static final int RequestPermissionCode  = 1 ;
    Location location;
    LocationManager locationManager ;
    boolean GpsStatus = false ;
    Criteria criteria ;
    String Holder;
    ProgressDialog pdLoading;
    boolean gpsClicked = false;

    private Button annulla_prenotazione, conferma_presenza;
    private ImageView posizione, qrcode;
    private TextView textViewNomeCicerone;
    private SharedPreferences sharedPreferences;
    private String codAttivita;
    private String codPrenotazione, latit, longit, latitGPS, longitGPS, data, ora;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    private static final String URL_CONFERMA_PRES="https://madminds.altervista.org/GestoreAttivita/GestorePresenzaGps.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dettagli_prenotazione);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        Intent intent = getIntent();
        String imageUrl = intent.getStringExtra(IMG);
        String titolo = intent.getStringExtra(TITOLO);
        String descrizione = intent.getStringExtra(DESCRIZIONE);
        String nomeCicerone = intent.getStringExtra(NOME_CICERONE);
        final String idCicerone = intent.getStringExtra(ID_CICERONE);
        final String scadenza = intent.getStringExtra(SCADENZA);
        codAttivita = intent.getStringExtra(COD_ATTIVITA);
        String lingua = intent.getStringExtra(LINGUA);
        String citta = intent.getStringExtra(CITTA);
        String maxPartecipanti = intent.getStringExtra(MAXPART);
        String regione = intent.getStringExtra(REGIONE);
        latit = intent.getStringExtra(LATIT);
        longit = intent.getStringExtra(LONG);
        data = intent.getStringExtra(DATA);
        ora = intent.getStringExtra(ORA);
        codPrenotazione= intent.getStringExtra(COD_PRENOTAZIONE);
        String nPartecipanti= intent.getStringExtra(N_PARTECIPANTI);
        final String importo=intent.getStringExtra(IMPORTO);
        final String surplus= intent.getStringExtra(SURPLUS);
        final String presenzaGps= intent.getStringExtra(PRESENZA_GPS);
        String presenzaQrCode= intent.getStringExtra(PRESENZA_QRCODE);
        final String dataCancellazionePrenotazione= intent.getStringExtra(CANC_PRENOTAZIONE);
        final String dataCancellazioneAttivita= intent.getStringExtra(CANC_ATTIVITA);
        String stato=intent.getStringExtra(STATO);

        ImageView imageView = findViewById(R.id.prenotazione_dettaglio_foto);
        TextView textViewTitolo = findViewById(R.id.attivita_dettaglio_titolo);
        TextView textViewDescrizione = findViewById(R.id.attivita_dett_descrizione);
        textViewNomeCicerone = findViewById(R.id.attivita_dett_nome_cicerone);
        TextView textViewScadenza = findViewById(R.id.attivita_dett_scadenza);
        TextView textViewData = findViewById(R.id.data);
        TextView textViewOra = findViewById(R.id.ora);
        TextView textViewCitta = findViewById(R.id.citta);
        TextView textViewMaxPartecipanti = findViewById(R.id.maxpartecipanti);
        TextView textViewImporto = findViewById(R.id.importo_prenotazione);
        TextView textViewServizio = findViewById(R.id.servizio_prenotazione);
        TextView textViewTotale = findViewById(R.id.totale_prenotazione);
        TextView textViewStato = findViewById(R.id.stato_prenotazione);
        TextView textViewQrCode = findViewById(R.id.qrcode_conf_prenotazione);
        TextView textViewGPS = findViewById(R.id.gps_conf_prenotazione);
        LinearLayout blocco_cicerone = findViewById(R.id.blocco_dati_cicerone);

        annulla_prenotazione = findViewById(R.id.button_annulla_prenotazione);
        conferma_presenza = findViewById(R.id.button_conferma_presenza);
        posizione = findViewById(R.id.posizione);
        qrcode = findViewById(R.id.qrcode);


        Picasso.get().load(imageUrl).fit().centerInside().into(imageView);
        textViewTitolo.setText(titolo);
        textViewDescrizione.setText("Descrizione:\n" + descrizione);
        textViewNomeCicerone.setText("Attivita di: " + nomeCicerone);
        textViewScadenza.setText("Data scadenza prenotazioni: " + DateConvertor.convertFormat(scadenza, "yyyy-MM-dd", "dd/MM/yyyy"));

        String newDateString = DateConvertor.convertFormat(data, "yyyy-MM-dd", "dd/MM/yyyy");
        textViewData.setText(newDateString);
        textViewOra.setText(ora);
        textViewCitta.setText(citta+", "+regione+", Lingua "+lingua );
        textViewMaxPartecipanti.setText("Posti prenotati: "+nPartecipanti+" (max."+maxPartecipanti+")");
        double totale = Double.parseDouble(importo);
        BigDecimal servizio = new BigDecimal(Double.toString(totale*Double.parseDouble(surplus)/100));
        double parziale = totale-servizio.doubleValue();
        textViewImporto.setText(String.format("%.2f", parziale) +  " €");
        textViewServizio.setText(String.format("%.2f", servizio)+ " € ("+surplus+"%)");
        textViewTotale.setText(String.format("%.2f", totale)+ " €");
        textViewStato.setText(stato);

        if(presenzaQrCode.equals("1")){
            textViewQrCode.setText("Confermata");
        } else {
            textViewQrCode.setText("Non confermata");
        }

        if(presenzaGps.equals("1")){
            textViewGPS.setText("Confermata");
        } else {
            textViewGPS.setText("Non confermata");
        }

        blocco_cicerone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(sharedPreferences.getString(ID, "0").equals(idCicerone)){
                    //Apertura del proprio profilo Cicerone

                    Intent intent = new Intent(DettagliPrenotazione.this, ProfiloCicerone.class);
                    intent.putExtra("id", sharedPreferences.getString(ID, "0"));
                    startActivity(intent);
                    finish();
                }else {
                    //Apertura del profilo Cicerone di qualcun'altro

                    Intent intent = new Intent(DettagliPrenotazione.this, ProfiloCiceroneEsterno.class);
                    intent.putExtra("id_cicerone_esterno", idCicerone );
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

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                visualizzaQRCode(codPrenotazione);
            }
        });

        annulla_prenotazione.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date(System.currentTimeMillis());
                String dataCorrente = formatter.format(date).toString();


                if(!dataCancellazionePrenotazione.equals("null")) {
                    Toast.makeText(DettagliPrenotazione.this, "La prenotazione è stata già annullata", Toast.LENGTH_LONG).show();
                } else if(!dataCancellazioneAttivita.equals("null")) {
                    Toast.makeText(DettagliPrenotazione.this, "L'attività è stata annullata dal Cicerone quindi la prenotazione non è più valida", Toast.LENGTH_LONG).show();
                } else if(data.compareTo(dataCorrente) < 0 ) {
                    Toast.makeText(DettagliPrenotazione.this, "L'attività è conclusa quindi non puoi più annullare la prenotazione", Toast.LENGTH_LONG).show();
                } else if(presenzaGps.equals("1") ) {
                    Toast.makeText(DettagliPrenotazione.this, "Non puoi annullare un'attività per cui hai confermato la presenza", Toast.LENGTH_LONG).show();
                } else {


                    Intent annulla_prenotazione = new Intent(DettagliPrenotazione.this, AnnullaPrenotazione.class);
                    annulla_prenotazione.putExtra(ID, sharedPreferences.getString(ID, ""));
                    annulla_prenotazione.putExtra(COD_ATTIVITA, codAttivita);
                    annulla_prenotazione.putExtra(IMPORTO, importo);
                    annulla_prenotazione.putExtra(SURPLUS, surplus);
                    annulla_prenotazione.putExtra(COD_PRENOTAZIONE, codPrenotazione);
                    annulla_prenotazione.putExtra(SCADENZA, scadenza);
                    annulla_prenotazione.putExtra(DATA, data);

                    startActivity(annulla_prenotazione);
                }
            }
        });

        conferma_presenza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!dataCancellazionePrenotazione.equals("null")) {
                    Toast.makeText(DettagliPrenotazione.this, "La prenotazione è stata annullata, non puoi confermare la presenza", Toast.LENGTH_LONG).show();
                } else if(!dataCancellazioneAttivita.equals("null")) {
                    Toast.makeText(DettagliPrenotazione.this, "L'attività è stata annullata dal Cicerone quindi la prenotazione non è più valida", Toast.LENGTH_LONG).show();
                }  else {

                    gpsClicked = true;
                    EnableRuntimePermission();
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    criteria = new Criteria();
                    Holder = locationManager.getBestProvider(criteria, false);
                    CheckGpsStatus();

                    if (GpsStatus == true) {
                        if (Holder != null) {
                            if (ActivityCompat.checkSelfPermission(
                                    DettagliPrenotazione.this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                                    &&
                                    ActivityCompat.checkSelfPermission(DettagliPrenotazione.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                                            != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            location = locationManager.getLastKnownLocation(Holder);
                            locationManager.requestLocationUpdates(Holder, 1200, 7, DettagliPrenotazione.this);


                            pdLoading = new ProgressDialog(DettagliPrenotazione.this);
                            pdLoading.setMessage("\tAcquisizione posizione in corso...");
                            pdLoading.setCancelable(true);
                            pdLoading.show();

                        }
                    } else {
                        Toast.makeText(DettagliPrenotazione.this, "Attiva la geolocalizzazione per confermare la presenza", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }


    public void visualizzaQRCode(String codPrenotazione) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        Bitmap myBitmap=QRCode.from(codPrenotazione).bitmap();

        ImageView imageView = new ImageView(this );
        imageView.setImageBitmap(myBitmap);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(1000, 1000));
        builder.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        if(gpsClicked) {
            pdLoading.dismiss();
            latitGPS = location.getLatitude()+"";
            longitGPS = location.getLongitude()+"";
            //Toast.makeText(DettagliPrenotazione.this, location.getLongitude() + ", " + location.getLatitude(), Toast.LENGTH_LONG).show();
            gpsClicked=false;

            class Conferma extends AsyncTask<Void, Void, String> {
                ProgressDialog pdLoading = new ProgressDialog(DettagliPrenotazione.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //this method will be running on UI thread
                    pdLoading.setMessage("\tConferma presenza...");
                    pdLoading.setCancelable(false);
                    pdLoading.show();
                }

                @Override
                protected String doInBackground(Void... voids) {

                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();

                    SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
                    Date date = new Date(System.currentTimeMillis());
                    String dataCorrente = formatter.format(date).toString();

                    Calendar now = Calendar.getInstance();
                    String oraCorrente = now.get(Calendar.HOUR_OF_DAY)+":"+now.get(Calendar.MINUTE)+":"+now.get(Calendar.SECOND);

                    params.put("codPrenotazione", codPrenotazione);
                    params.put("latitGPS", latitGPS);
                    params.put("longitGPS", longitGPS);
                    params.put("latitAttivita", latit);
                    params.put("longitAttivita", longit);
                    params.put("dataAttivita", data);
                    params.put("oraAppuntamento", ora);
                    params.put("dataCorrente", dataCorrente);
                    params.put("oraCorrente", oraCorrente);

                    //returing the response
                    return requestHandler.sendPostRequest(URL_CONFERMA_PRES, params);
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                    pdLoading.dismiss();

                    try {

                        JSONObject obj = new JSONObject(s);
                        boolean error = obj.getBoolean("error");

                        if (!error) {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(DettagliPrenotazione.this, Navigazione.class);
                            startActivity(intent);
                        }else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(DettagliPrenotazione.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                    }
                }
            }

            Conferma conferma = new Conferma();
            conferma.execute();
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String s) {
    }

    @Override
    public void onProviderDisabled(String s) {
    }

    public void CheckGpsStatus(){
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public void EnableRuntimePermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(DettagliPrenotazione.this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(DettagliPrenotazione.this,"ACCESS_FINE_LOCATION permission allows us to Access GPS in app", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(DettagliPrenotazione.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION}, RequestPermissionCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {
        switch (RC) {
            case RequestPermissionCode:
                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(DettagliPrenotazione.this,"Permission Granted, Now your application can access GPS.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(DettagliPrenotazione.this,"Permesso cancellato, ora la tua applicazione non può accedere al GPS.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


}
