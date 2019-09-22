package com.madminds.madminds;

public class DatiPubblicaAttivita {
    private String titolo, descrizione, orarioApp, lingua, citta, regione, scadenzaPreotazioni,
        dataSvolgimento, img;
    private int nMaxPartecipanti;
    private double lati, longi;


    public DatiPubblicaAttivita(String titolo, String descrizione, String orarioApp,
                                String lingua, String citta, String regione,
                                String scadenzaPreotazioni, String dataSvolgimento,
                                int nMaxPartecipanti, double lati, double longi, String img) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.orarioApp = orarioApp;
        this.lingua = lingua;
        this.citta = citta;
        this.regione = regione;
        this.scadenzaPreotazioni = scadenzaPreotazioni;
        this.dataSvolgimento = dataSvolgimento;
        this.nMaxPartecipanti = nMaxPartecipanti;
        this.lati = lati;
        this.longi = longi;
        this.img = img;
    }

    public String getTitolo() {
        return titolo;
    }


    public String getDescrizioneDatiPubblica() {
        return descrizione;
    }


    public String getOrarioApp() {
        return orarioApp;
    }


    public String getLingua() {
        return lingua;
    }


    public String getCittaDatiPubblica() {
        return citta;
    }


    public String getRegione() {
        return regione;
    }


    public String getScadenzaPreotazioni() {
        return scadenzaPreotazioni;
    }


    public String getDataSvolgimento() {
        return dataSvolgimento;
    }


    public int getnMaxPartecipanti() {
        return nMaxPartecipanti;
    }


    public double getLati() {
        return lati;
    }

    public double getLongi() {
        return longi;
    }

}
