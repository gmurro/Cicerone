package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;


import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    public static final String URL_LOGIN = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreLogin.php";
    EditText ed_email, ed_password;
    SharedPreferences sharedPreferences;
    public static final String MY_PREFERENCES = "MyPrefs";
    public static final String EMAIL = "email";
    public static final String LOGGED = "logged";
    public static final String CICERONE = "cicerone";
    public static final String ID = "id";
    private boolean logged;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ed_email = findViewById(R.id.email);
        ed_password = findViewById(R.id.password);

        sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);

        logged = sharedPreferences.getBoolean(LOGGED, false);

        if (logged){
            finish();
            Intent intent = new Intent(LoginActivity.this, Navigazione.class);
            startActivity(intent);
        }
    }

    public void login(View view){
        final String email = ed_email.getText().toString();
        final String password = ed_password.getText().toString();

        if(email.isEmpty()|| password.isEmpty()){
            Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
        }

        else {
            class Login extends AsyncTask<Void, Void, String> {
                ProgressDialog pdLoading = new ProgressDialog(LoginActivity.this);

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();

                    //this method will be running on UI thread
                    pdLoading.setMessage("\tLoading...");
                    pdLoading.setCancelable(false);
                    pdLoading.show();
                }

                @Override
                protected String doInBackground(Void... voids) {
                    //creating request handler object
                    RequestHandler requestHandler = new RequestHandler();

                    //creating request parameters
                    HashMap<String, String> params = new HashMap<>();
                    params.put("email", email);
                    params.put("password", password);

                    //returing the response
                    return requestHandler.sendPostRequest(URL_LOGIN, params);
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
                            String id = obj.getString("id");
                            boolean cicerone = (Boolean) obj.get("cicerone");

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(ID, id);
                            editor.putString(EMAIL, email);
                            editor.putBoolean(LOGGED, true);
                            editor.putBoolean(CICERONE, cicerone);

                            editor.apply();

                            finish();
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(LoginActivity.this, Navigazione.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();
                    }
                }
            }

            Login login = new Login();
            login.execute();
        }
    }

    public void registrazione(View view){
        finish();
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}