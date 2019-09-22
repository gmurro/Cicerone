package com.madminds.madminds;

public class Recensione {
    private String nome;
    private String cognome;
    private String voto;
    private String descrizione;

    public Recensione(String nome, String cognome, String voto, String descrizione) {
        this.nome = nome;
        this.cognome = cognome;
        this.voto = voto;
        this.descrizione = descrizione;

    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getVoto() {
        return voto;
    }

    public String getDescrizione() {
        return descrizione;
    }


}