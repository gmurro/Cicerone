package com.madminds.madminds;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecensioneAdapter extends RecyclerView.Adapter<RecensioneAdapter.MyViewHolder> {

    private Context mContext;
    private List<Recensione> recensione;
    private OnRecensioneClickListener mListener;


    public interface OnRecensioneClickListener {
        void onRecensioneClick(int position);
    }

    public void setOnClickListner(OnRecensioneClickListener listner) {
        mListener = listner;
    }


    public RecensioneAdapter(Context context,List<Recensione> recensioni) {
        mContext = context;
        this.recensione = recensioni;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private  TextView textViewNomeRecensore, textViewCogomeRecensore, textViewVotoRecensione,textViewDescrizioneRecensione;

        public MyViewHolder(View view) {
            super(view);
            textViewNomeRecensore = (TextView) view.findViewById(R.id.nomeRecensore);
            textViewCogomeRecensore = view.findViewById(R.id.cognomeRecensore);
            textViewVotoRecensione = view.findViewById(R.id.votoRecensione);
            textViewDescrizioneRecensione = view.findViewById(R.id.descrizioneRecensione);
        }
    }



    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_recensione, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Recensione rec = recensione.get(position);
        holder.textViewNomeRecensore.setText(rec.getNome());
        holder.textViewCogomeRecensore.setText(rec.getCognome());
        holder.textViewVotoRecensione.setText(rec.getVoto()+"/5");
        holder.textViewDescrizioneRecensione.setText(rec.getDescrizione());

    }

    @Override
    public int getItemCount() {
        return recensione.size();
    }
}