package com.madminds.madminds;

public class Attivita {
    private String mNome;
    private String mCognome;
    private int mCodCreatore;
    private int mCodAttivita;
    private String mTitolo;
    private String mDescrizione;
    private String mLingua;
    private String mCitta;
    private String mRegione;
    private int mMaxPartecipanti;
    private String mUrlImage;
    private String mScadenzaPrenotazioni;
    private String mLatitAppuntamento;
    private String mLongAppuntamento;
    private String mData;
    private String mOraAppuntamento;
    private String mStato;
    private String mDataCancellazione;

    public Attivita(String mNome, String mCognome, int mCodCreatore, int mCodAttivita,
                    String mTitolo, String mDescrizione, String mLingua, String mCitta,
                    String mRegione, int mMaxPartecipanti, String mUrlImage,
                    String mScadenzaPrenotazioni, String latitAppuntamento,String longAppuntamento, String data, String oraAppuntamento) {
        this.mNome = mNome;
        this.mCognome = mCognome;
        this.mCodCreatore = mCodCreatore;
        this.mCodAttivita = mCodAttivita;
        this.mTitolo = mTitolo;
        this.mDescrizione = mDescrizione;
        this.mLingua = mLingua;
        this.mCitta = mCitta;
        this.mRegione = mRegione;
        this.mMaxPartecipanti = mMaxPartecipanti;
        this.mUrlImage = mUrlImage;
        this.mScadenzaPrenotazioni = mScadenzaPrenotazioni;
        this.mLatitAppuntamento = latitAppuntamento;
        this.mLongAppuntamento = longAppuntamento;
        this.mData = data;
        this.mOraAppuntamento = oraAppuntamento;
    }

    public void setmStatoCancellazione(String mStato, String mDataCancellazione) {
        this.mStato = mStato;
        this.mDataCancellazione =mDataCancellazione;
    }

    public String getmNome() {
        return mNome;
    }

    public String getmCognome() {
        return mCognome;
    }

    public int getmCodCreatore() {
        return mCodCreatore;
    }

    public int getmCodAttivita() {
        return mCodAttivita;
    }

    public String getmTitolo() {
        return mTitolo;
    }

    public String getmDescrizione() {
        return mDescrizione;
    }

    public String getmLingua() {
        return mLingua;
    }

    public String getmCitta() {
        return mCitta;
    }

    public String getmRegione() {
        return mRegione;
    }

    public int getmMaxPartecipanti() {
        return mMaxPartecipanti;
    }

    public String getmUrlImage() {
        return mUrlImage;
    }

    public String getmScadenzaPrenotazioni() {
        return mScadenzaPrenotazioni;
    }

    public String getmLatitAppuntamento() {
        return mLatitAppuntamento;
    }

    public String getmLongAppuntamento() {
        return mLongAppuntamento;
    }

    public String getmData() {
        return mData;
    }

    public String getmOraAppuntamento() {
        return mOraAppuntamento;
    }

    public String getmStato() {
        return mStato;
    }

    public String getmDataCancellazione() {
        return mDataCancellazione;
    }
}
