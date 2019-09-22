package com.madminds.madminds;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class AttivitaAdapter extends RecyclerView.Adapter<AttivitaAdapter.AttivitaViewHolder> {
    private Context mContext;
    private ArrayList<Attivita> mAttivitaList;
    private OnAttivitaClickListener mListener;

    public interface OnAttivitaClickListener {
        void onAttivitaClick(int position);
    }

    public void setOnClickListner(OnAttivitaClickListener listner) {
        mListener = listner;
    }

    public AttivitaAdapter(Context context, ArrayList<Attivita> attivitaList) {
        mContext = context;
        mAttivitaList = attivitaList;
    }

    @NonNull
    @Override
    public AttivitaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.card_attivita, viewGroup, false);
        return new AttivitaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AttivitaViewHolder prenotazioneViewHolder, int i) {
        Attivita currentAttivita = mAttivitaList.get(i);

        String imageUrl = currentAttivita.getmUrlImage();
        String titoloAttivita = currentAttivita.getmTitolo();
        String descrizioneAttivita = currentAttivita.getmDescrizione();

        prenotazioneViewHolder.mTextViewTitolo.setText(titoloAttivita);
        prenotazioneViewHolder.mTextViewDescrizione.setText(descrizioneAttivita);

        Picasso.get().load(imageUrl).fit().centerInside().transform(new CircleTrasformation()).into(prenotazioneViewHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mAttivitaList.size();
    }

    public class AttivitaViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImageView;
        private TextView mTextViewTitolo;
        private TextView mTextViewDescrizione;

        public AttivitaViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.attivita_foto);
            mTextViewTitolo = itemView.findViewById(R.id.attivita_titolo);
            mTextViewDescrizione = itemView.findViewById(R.id.attivita_descrizione);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            mListener.onAttivitaClick(position);
                        }
                    }
                }
            });
        }
    }
}
