package com.madminds.madminds.VistaCicerone;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.madminds.madminds.FasciaPrezzo;
import com.madminds.madminds.FasciaPrezzoAdapter;
import com.madminds.madminds.R;

import java.util.ArrayList;

public class AggiungiFascePrezzo extends AppCompatActivity {

    //SharedPreferences sharedPreferences;
    private ArrayList<FasciaPrezzo> fasce;
    private Button addFascia, avanti;
    private EditText numMax, prezzoEdit;
    private TextView minValue;
    private int nMaxPartecipati, numMaxEditText;
    private double prezzo;
    private RecyclerView mRecyclerView;
    private int cout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_aggiungi_fascia_attivita);

        /*
        String jsonMyObject = null;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            jsonMyObject = extras.getString("myObject");
        }
        DatiPubblicaAttivita myObject = new Gson().fromJson(jsonMyObject, DatiPubblicaAttivita.class);*/
        fasce = new ArrayList<>();
        addFascia = findViewById(R.id.addFasciaButton);
        avanti = findViewById(R.id.avantiFasciaButton);
        minValue = findViewById(R.id.textView3);
        numMax = findViewById(R.id.numMaxUtentiFascia);
        mRecyclerView = findViewById(R.id.recycler_view_attivita);
        prezzoEdit = findViewById(R.id.prezzoAttivitaPubblica);

        cout = 0;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        nMaxPartecipati = extras.getInt("nMax");
        //prezzo = 1.0;


        addFascia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    numMaxEditText = Integer.parseInt(numMax.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(AggiungiFascePrezzo.this, "Riempi tutti i campi!", Toast.LENGTH_SHORT).show();
                }


                if(numMaxEditText <= nMaxPartecipati && numMaxEditText > Integer.parseInt(minValue.getText().toString())
                    && !prezzoEdit.getText().toString().isEmpty() && Integer.parseInt(minValue.getText().toString()) != nMaxPartecipati) {

                    cout++;

                    prezzo = Double.parseDouble(prezzoEdit.getText().toString());
                    fasce.add(new FasciaPrezzo(minValue.getText().toString(),
                            String.valueOf(numMaxEditText), Double.toString(prezzo)));
                    if(numMaxEditText == nMaxPartecipati) {
                        minValue.setText(Integer.toString(nMaxPartecipati));
                        Toast.makeText(AggiungiFascePrezzo.this, "Hai raggiunto il numro massimo di fasce", Toast.LENGTH_SHORT).show();
                    } else {
                        minValue.setText(Integer.toString(numMaxEditText+1));
                    }

                    FasciaPrezzoAdapter adapter = new FasciaPrezzoAdapter(fasce);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    mRecyclerView.setLayoutManager(mLayoutManager);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mRecyclerView.setAdapter(adapter);

                } else {
                    Toast.makeText(AggiungiFascePrezzo.this, "Valore non valido o limite raggiunto!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cout > 0) {

                    Intent intent = new Intent(AggiungiFascePrezzo.this, AggiungiAttivita.class);
                    Bundle args = new Bundle();
                    args.putInt("nMax", nMaxPartecipati);
                    args.putSerializable("mylistFascie", fasce);
                    intent.putExtra("BUNDLE",args);
                    startActivity(intent);
                } else {
                    Toast.makeText(AggiungiFascePrezzo.this, "Inserisci almeno una fascia di prezzo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TextView textView = findViewById(R.id.asd);
        //sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);


        //textView.setText(sharedPreferences.getString(ID, "0"));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }


}
