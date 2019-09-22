package com.madminds.madminds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class PrenotazioneAdapter extends RecyclerView.Adapter<PrenotazioneAdapter.PrenotazioneViewHolder> {
    private Context mContext;
    private ArrayList<Prenotazione> mPrenotazioneList;
    private OnPrenotazioneClickListener mListener;

    public interface OnPrenotazioneClickListener {
        void onPrenotazioneClick(int position);
    }

    public void setOnClickListner(OnPrenotazioneClickListener listner) {
        mListener = listner;
    }

    public PrenotazioneAdapter(Context context, ArrayList<Prenotazione> prenotazioneList) {
        mContext = context;
        mPrenotazioneList = prenotazioneList;
    }

    @NonNull
    @Override
    public PrenotazioneViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_prenotazione, viewGroup, false);
        return new PrenotazioneViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PrenotazioneViewHolder prenotazioneViewHolder, int i) {
        Prenotazione currentPrenotazione = mPrenotazioneList.get(i);

        String imageUrl = currentPrenotazione.getmUrlImage();
        String titoloAttivita = currentPrenotazione.getmTitolo();
        String statoPrenotazione = currentPrenotazione.getmStato();

        prenotazioneViewHolder.mTextViewTitolo.setText(titoloAttivita);
        prenotazioneViewHolder.mTextViewStato.setText(statoPrenotazione);

        if(!imageUrl.equals("null")) {
            Picasso.get().load(imageUrl).fit().networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_STORE).centerInside().transform(new CircleTrasformation()).into(prenotazioneViewHolder.mImageView);

        }else{
            Picasso.get().load("https://madminds.altervista.org/GestoreGlobetrotter/img/profilo_default.jpg").fit().networkPolicy(NetworkPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_CACHE).centerInside().transform(new CircleTrasformation()).into(prenotazioneViewHolder.mImageView);
        }

        //Picasso.get().load(imageUrl).fit().centerInside().transform(new CircleTrasformation()).into(prenotazioneViewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mPrenotazioneList.size();
    }

    public class PrenotazioneViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextViewTitolo;
        private TextView mTextViewStato;

        public PrenotazioneViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.prenotazione_foto);
            mTextViewTitolo = itemView.findViewById(R.id.prenotazione_titolo);
            mTextViewStato = itemView.findViewById(R.id.prenotazione_stato);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            mListener.onPrenotazioneClick(position);
                        }
                    }
                }
            });
        }
    }
}

