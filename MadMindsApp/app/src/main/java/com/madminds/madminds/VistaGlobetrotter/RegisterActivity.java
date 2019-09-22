package com.madminds.madminds.VistaGlobetrotter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import android.app.DatePickerDialog;
import android.widget.DatePicker;

import com.madminds.madminds.R;
import com.madminds.madminds.RequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    EditText nome, cognome, email, password, reinsPassword, dataNascita;
    public static final String URL_REGISTER = "https://madminds.altervista.org/GestoreGlobetrotter/GestoreRegistrazione.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nome = findViewById(R.id.titoloAttivita);
        cognome = findViewById(R.id.cognome);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        reinsPassword = findViewById(R.id.reinsPassword);
        dataNascita = findViewById(R.id.dataNascita);

        dataNascita.setKeyListener(null);

    }

    public void dataPicker(View view) {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dataNascita.setText(sdf.format(myCalendar.getTime()));
            }
        };

        new DatePickerDialog(RegisterActivity.this, date, myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void registrazione(View view){
        final String nome = this.nome.getText().toString();
        final String cognome = this.cognome.getText().toString();
        final String email = this.email.getText().toString();
        final String password = this.password.getText().toString();
        final String reinsPassword = this.reinsPassword.getText().toString();
        final String dataNascita = this.dataNascita.getText().toString();

        if(nome.isEmpty() || cognome.isEmpty() || email.isEmpty() || password.isEmpty() || reinsPassword.isEmpty() || dataNascita.isEmpty()){
            Toast.makeText(this, "Riempire tutti i campi", Toast.LENGTH_SHORT).show();
        } else if (!(password.equals(reinsPassword))){
            Toast.makeText(this, "Le password inserite non corrispondono", Toast.LENGTH_SHORT).show();
        }
        else {
            class Login extends AsyncTask<Void, Void, String> {
                ProgressDialog pdLoading = new ProgressDialog(RegisterActivity.this);

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
                    params.put("email", email);
                    params.put("password", password);
                    params.put("nome", nome);
                    params.put("cognome", cognome);
                    params.put("dataNascita", dataNascita);

                    //returing the response
                    return requestHandler.sendPostRequest(URL_REGISTER, params);
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

                            finish();
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(RegisterActivity.this, "Si è verificato un errore, probabilmente la connessione è assente o il server è irraggiungibile", Toast.LENGTH_LONG).show();

                    }
                }
            }

            Login login = new Login();
            login.execute();
        }
    }

    public void login(View view){
        finish();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}