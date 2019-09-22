package com.madminds.madminds;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class FasciaPrezzoAdapter extends RecyclerView.Adapter<FasciaPrezzoAdapter.MyViewHolder> {

    private List<FasciaPrezzo> fasciePrezzi;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  TextView textViewMinPartecipanti, textViewMaxPartecipanti, textViewPrezzo;

        public MyViewHolder(View view) {
            super(view);
            textViewMinPartecipanti = (TextView) view.findViewById(R.id.recMinPartecipanti);
            textViewMaxPartecipanti = (TextView) view.findViewById(R.id.recMaxPartecipanti);
            textViewPrezzo = (TextView) view.findViewById(R.id.recPrezzo);
        }
    }


    public FasciaPrezzoAdapter(List<FasciaPrezzo> fasciePrezzi) {
        this.fasciePrezzi = fasciePrezzi;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_fascia_prezzo, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FasciaPrezzo fascia = fasciePrezzi.get(position);
        holder.textViewMinPartecipanti.setText(fascia.getMin());
        holder.textViewMaxPartecipanti.setText(fascia.getMax());
        holder.textViewPrezzo.setText(fascia.getPrezzo()+"€");
    }

    @Override
    public int getItemCount() {
        return fasciePrezzi.size();
    }
}