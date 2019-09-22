package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.CircleTrasformation;
import com.madminds.madminds.R;
import com.madminds.madminds.VistaCicerone.FragmentConvalidaPresenze;
import com.madminds.madminds.VistaCicerone.FragmentPortafoglioCicerone;
import com.madminds.madminds.VistaCicerone.Fragment_attivitaPubblicate;
import com.madminds.madminds.VistaCicerone.ProfiloCicerone;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Navigazione extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DiventaCicerone.OnFragmentDCInteractionListener, FragmentHome.OnFragmentInteractionListener,
        Fragment_attivitaPubblicate.OnFragmentInteractionListener, FragmentPrenotazioniEffettuate.OnFragmentPEInteractionListener,
        FragmentRecensioniScritte.OnFragmentPEInteractionListener, FragmentConvalidaPresenze.OnFragmentCPInteractionListener, FragmentPortafoglioGlobetrotter.OnFragmentPInteractionListener, FragmentPortafoglioCicerone.OnFragmentPCInteractionListener{


    private SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String ID = "id";
    public static final String CICERONE = "cicerone";
    private static NavigationView navigationView;
    private TextView nomeCognome;
    private int GPS_PERMISSION_CODE = 1;

    private ProgressDialog pDialog;


    private String url = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreCaricaProfilo.php";
    private String urlEliminaProfilo = "https://madminds.altervista.org/GestoreCicerone/GestoreEliminaProfiloCicerone.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigazione);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        //nomeCognome = findViewById(R.id.nomeCognome);

        if (!checkConnessione()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Navigazione.this);
            builder.setCancelable(true);
            builder.setTitle("Connessione assente");
            builder.setMessage("Si prega di assicurarsi che la connessione a Internet sia disponibile");
            builder.setPositiveButton("Riprova",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {

            //settaNavInfo(headerView);

            //Snackbar.make(findViewById(R.id.layout_home), "Login avvenuto con successo!", Snackbar.LENGTH_LONG).show();
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
                //nomeCognome.setText("ciao");
                navigationView.setCheckedItem(R.id.nav_home);
            }
        }


        LinearLayout info = headerView.findViewById(R.id.infoView);
        sharedPreferences = (SharedPreferences) getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sharedPreferences.getBoolean(CICERONE, false)){
                    Intent intent = new Intent(Navigazione.this, ProfiloCicerone.class);
                    startActivity(intent);

                }else{
                    Intent intent = new Intent(Navigazione.this, ProfiloGlobetrotter.class);
                    startActivity(intent);
                }

                //visualizzaProfilo();
                //Snackbar.make(findViewById(R.id.layout_home), "Login avvenuto con successo!", Snackbar.LENGTH_LONG).show();
            }
        });
        settaNavInfo();
        //FrameLayout frameLayout = findViewById(R.id.fragment_container);
    }

    public final void aggiornamentoNavView(){
        settaNavInfo();
    }


    private void settaNavInfo() {
        View headerView = navigationView.getHeaderView(0);
        final TextView navUsername = headerView.findViewById(R.id.nomeCognome);
        TextView navStatus = headerView.findViewById(R.id.stato_utente);
        final ImageView fotoProfilo = headerView.findViewById(R.id.fotoProfilo_info);
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        //boolean cicerone = ;

        if(sharedPreferences.getBoolean(CICERONE, false)) {
            navStatus.setText("Cicerone & Globetrotter");
        } else {
            navStatus.setText("Globetrotter");
        }

        //navUsername.setText("Alessio Tartarelli");


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {
                                //Toast.makeText(Navigazione.this, response, Toast.LENGTH_LONG).show();
                                String text;
                                text = jsonObject.getString("nome");
                                text += " ";
                                text += jsonObject.getString("cognome");
                                navUsername.setText(text);
                                String fotopercorso = jsonObject.getString("foto");
                                if(!fotopercorso.equals("null")) {
                                    Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotopercorso).fit().networkPolicy(NetworkPolicy.NO_CACHE)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(fotoProfilo);

                                }else{
                                    Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/img/profilo_default.jpg").fit().networkPolicy(NetworkPolicy.NO_CACHE)
                                            .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(fotoProfilo);
                                }
                                
                                //Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotopercorso).fit().networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_STORE).centerInside().transform(new CircleTrasformation()).into(fotoProfilo);

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(Navigazione.this, error.toString(), Toast.LENGTH_LONG).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("idGlobetrotter", sharedPreferences.getString(ID, ""));

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(Navigazione.this);
        //stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    private boolean checkConnessione() {
        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        //we are connected to a network
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;

        return connected;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(Navigazione.this);
            builder.setCancelable(true);
            builder.setTitle("Attenzione");
            builder.setMessage("Vuoi davvero uscire dall'applicazione?");
            builder.setPositiveButton("Conferma",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            System.exit(0);
                        }
                    });
            builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            //super.onBackPressed();
        }
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigazione, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //Snackbar.make(findViewById(R.id.layout_home), "tasto menu!", Snackbar.LENGTH_LONG).show();
            //Intent intent = new Intent(Navigazione.this, ModificaProfiloGlobetrotter.class);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        //settaNavInfo();
        int id = item.getItemId();
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
        settaNavInfo();

        if (id == R.id.nav_home) {

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();

            /*
            FragmentManager fragmentManager  = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_rigth, R.anim.exit_from_rigth,
                    R.anim.enter_from_rigth, R.anim.exit_from_rigth);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.replace(R.id.fragment_container, new FragmentHome()).commit();*/

        } else if (id == R.id.nav_recensioni) {
            FragmentRecensioniScritte recensioniScritte = FragmentRecensioniScritte.newInstance(sharedPreferences.getString(ID, ""));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recensioniScritte).commit();
            /*
            Intent test = new Intent(this, ActivityTest.class);
            startActivity(test);*/

        } else if (id == R.id.nav_prenotazioni) {
            FragmentPrenotazioniEffettuate prenotazioniEffettuate = FragmentPrenotazioniEffettuate.newInstance(sharedPreferences.getString(ID, ""));
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, prenotazioniEffettuate).commit();

        } else if (id == R.id.nav_portafoglio) {
            if (!sharedPreferences.getBoolean(CICERONE, false)) {
                FragmentPortafoglioGlobetrotter portafoglio = FragmentPortafoglioGlobetrotter.newInstance(sharedPreferences.getString(ID, ""));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, portafoglio).commit();
            } else {
                FragmentPortafoglioCicerone portafoglio = FragmentPortafoglioCicerone.newInstance(sharedPreferences.getString(ID, ""));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, portafoglio).commit();
            }

        } else if (id == R.id.nav_div_cicerone) {
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DiventaCicerone()).commit();

            if (!sharedPreferences.getBoolean(CICERONE, false)) {
                DiventaCicerone diventaCicerone = DiventaCicerone.newInstance(sharedPreferences.getString(ID, ""));
                //FragmentManager fragmentManager  = getSupportFragmentManager();

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, diventaCicerone).commit();

                /*
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_rigth, R.anim.exit_from_rigth,
                        R.anim.enter_from_rigth, R.anim.exit_from_rigth);

                fragmentTransaction.disallowAddToBackStack();
                fragmentTransaction.replace(R.id.fragment_container, diventaCicerone).commit();*/
            } else {
                Snackbar.make(findViewById(R.id.layout_home), "Sei gia cicerone", Snackbar.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_attivita) {

            if (sharedPreferences.getBoolean(CICERONE, false)) {

                Fragment_attivitaPubblicate attivitaPubblicate = Fragment_attivitaPubblicate.newInstance(sharedPreferences.getString(ID, ""));

                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, attivitaPubblicate).commit();

            } else {
                Snackbar.make(findViewById(R.id.layout_home), "Funzionalita solo per Ciceroni", Snackbar.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_convalidaPresenze) {

            if (sharedPreferences.getBoolean(CICERONE, false)) {
                FragmentConvalidaPresenze convalidaPresenze = FragmentConvalidaPresenze.newInstance(sharedPreferences.getString(ID, ""));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, convalidaPresenze).commit();
            } else {
                Snackbar.make(findViewById(R.id.layout_home), "Funzionalita solo per Ciceroni", Snackbar.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_eliminaProfilo) {

            if (sharedPreferences.getBoolean(CICERONE, false)) {
                displayLoader();
                eliminaprofiloCicerone();
            } else {
                Snackbar.make(findViewById(R.id.layout_home), "Funzionalita solo per Ciceroni", Snackbar.LENGTH_LONG).show();
            }

        } else if (id == R.id.nav_logout) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            finish();
            Intent intent = new Intent(Navigazione.this, LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    private void visualizzaProfilo() {
        if(sharedPreferences.getBoolean(CICERONE, false)){
            //visualizza profilo globetrotter

        } else {
            //visualizza profilo cicerone
            //Snackbar.make(findViewById(R.id.layout_home), "Funzionalita solo per Ciceroni", Snackbar.LENGTH_LONG).show();
        }
    }*/

    private void displayLoader() {
        pDialog = new ProgressDialog(Navigazione.this);
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    private void eliminaprofiloCicerone() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlEliminaProfilo,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.dismiss();
                        Toast.makeText(Navigazione.this, error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

                params.put("codCicerone", sharedPreferences.getString(ID, null));

                return params;
            }

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void parseData(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);

            boolean error = jsonObject.getBoolean("error");
            boolean attivitaDaSvolgere = jsonObject.getBoolean("attivitaDaSvolgere");
            pDialog.dismiss();
            if (!error) {

                if(attivitaDaSvolgere) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(Navigazione.this);
                    builder.setCancelable(true);
                    builder.setTitle("Attenzione");
                    builder.setMessage("Pare che tu abbia ancora delle attività da svolgere, potrai eliminare il tuo account " +
                            "da Cicerone solo dopo averle svolte!");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    Snackbar.make(findViewById(R.id.layout_home), "Non sei più Cicerone", Snackbar.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(CICERONE, false);
                    editor.apply();
                    settaNavInfo();
                }

            } else {
                Toast.makeText(Navigazione.this, "Errore, riprovare", Toast.LENGTH_LONG).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void popup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Navigazione.this);
        builder.setCancelable(true);
        builder.setTitle("Eri già Cicerone");
        builder.setMessage("Hai riattivato il tuo account, vai nel tuo profilo per modificare le tue informazioni");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
        navigationView.setCheckedItem(R.id.nav_home);
        Snackbar.make(findViewById(R.id.layout_home), "Sei diventato cicerone!", Snackbar.LENGTH_LONG).show();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CICERONE, true);
        editor.apply();
        settaNavInfo();
    }




    @Override
    public void onFragmentDCInteraction(boolean isGiaCicerone) {
        if(isGiaCicerone) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CICERONE, true);
            editor.apply();
            settaNavInfo();
            popup();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FragmentHome()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
            Snackbar.make(findViewById(R.id.layout_home), "Sei diventato cicerone!", Snackbar.LENGTH_LONG).show();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(CICERONE, true);
            editor.apply();
            settaNavInfo();
        }
    }

    @Override
    public void onFragmentAttivitaPubblicate(Uri uri) {

    }

    @Override
    public void onFragmentPEInteraction(Uri uri) {

    }

    @Override
    public void onFragmentCPInteraction(Uri uri) {

    }

    @Override
    public void onFragmentPInteraction(Uri uri) {

    }

    @Override
    public void onFragmentPCInteraction(Uri uri) {

    }
}
