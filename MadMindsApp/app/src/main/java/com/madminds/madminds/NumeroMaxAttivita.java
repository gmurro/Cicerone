package com.madminds.madminds;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.madminds.madminds.VistaCicerone.AggiungiFascePrezzo;

public class NumeroMaxAttivita extends AppCompatActivity {

    private Button avanti;
    private EditText nPart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numero_max_attivita);

        avanti = findViewById(R.id.buttonAvantiNPartepipanti);
        nPart = findViewById(R.id.nMaxPartAttvita);
        final String[] getN = new String[1];
        final int[] nParte = new int[1];

        avanti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getN[0] = nPart.getText().toString();

                try {
                    nParte[0] = Integer.parseInt(getN[0]);
                    if(nParte[0] <= 30 && nParte[0] >1 ) {
                        Intent intent = new Intent(NumeroMaxAttivita.this, AggiungiFascePrezzo.class);
                        intent.putExtra("nMax", nParte[0]);
                        startActivity(intent);
                    } else {
                        Toast.makeText(NumeroMaxAttivita.this, "Valore non valido", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {

                }

            }
        });
    }
}
