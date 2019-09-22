package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.madminds.madminds.CircleTrasformation;
import com.madminds.madminds.R;
import com.madminds.madminds.VistaCicerone.ProfiloCicerone;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.madminds.madminds.VistaGlobetrotter.Navigazione.MY_PREFERENCES;

public class ModificaFotoProfilo extends AppCompatActivity {

    private String id, fotoprofilo ;
    private ImageView immagineprofilo;
    private Button invia;
    private Bitmap bitmap;
    private ProgressDialog pDialog;
    SharedPreferences sharedPreferences;
    public static final String CICERONE = "cicerone";

    private static final int RESULT_LOAD_IMAGE = 1;


    public static final String URL_MODIFIED = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreModificaFoto.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_foto_profilo);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        id = extras.getString("id");
        fotoprofilo = extras.getString("fotoprofilo");

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        immagineprofilo = findViewById(R.id.immagineProfilo);
        invia = findViewById(R.id.btnmodificafoto);

        if(fotoprofilo != null) {
            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/"+fotoprofilo).fit().networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_STORE).centerInside().transform(new CircleTrasformation()).into(immagineprofilo);

        }else{
            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/img/profilo_default.jpg").fit().networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(immagineprofilo);
        }


        immagineprofilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });

        invia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLoader();
                modificaFotoProfilo();
            }
        });

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Toast.makeText(this, ""+bitmap, Toast.LENGTH_SHORT).show();
                Picasso.get().load(filePath).fit().centerInside().transform(new CircleTrasformation()).into(immagineprofilo);
                //Picasso.load(bitmap).fit().centerInside().transform(new CircleTrasformation()).into(immagineprofilo);
                //immagineprofilo.setImageBitmap(bitmap);
            } catch (IOException e) {
                Toast.makeText(ModificaFotoProfilo.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(ModificaFotoProfilo.this);
        pDialog.setMessage("Caricamento...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

    }

    public String getStringImage(Bitmap bitmap){
        Log.i("MyHitesh",""+bitmap);
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,40, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);


        return temp;
    }

    private void modificaFotoProfilo() {
        RequestQueue requestQueue = Volley.newRequestQueue(ModificaFotoProfilo.this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_MODIFIED, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                pDialog.dismiss();
                Log.i("Myresponse",""+response);
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    boolean error = jsonObject.getBoolean("error");

                    if (!error) {
                        Toast.makeText(ModificaFotoProfilo.this, "Modifica avvenuta con successo", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ModificaFotoProfilo.this, ProfiloGlobetrotter.class);
                        startActivity(intent);
                        //settaNavInfo();
                        finish();
                    } else {
                        Toast.makeText(ModificaFotoProfilo.this, "Modifiche NON avvenute con successo, riprovare", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Mysmart",""+error);
                Toast.makeText(ModificaFotoProfilo.this, "Errore durante il caricamento, Riprovare", Toast.LENGTH_SHORT).show();
                pDialog.dismiss();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();

                String images = getStringImage(bitmap);
                Log.i("Mynewsam",""+images);
                param.put("idGlobetrotter",id);
                param.put("img",images);
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

        if (sharedPreferences.getBoolean(CICERONE, false)) {
            startActivity(new Intent(ModificaFotoProfilo.this, ProfiloCicerone.class));
        }else{
            startActivity(new Intent(ModificaFotoProfilo.this, ProfiloGlobetrotter.class));
            }
    }

}
