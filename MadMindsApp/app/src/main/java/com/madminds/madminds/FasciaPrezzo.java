package com.madminds.madminds;

import java.io.Serializable;

public class FasciaPrezzo implements Serializable {
    private String min;
    private String max;
    private String prezzo;

    public FasciaPrezzo(String min, String max, String prezzo) {
        this.min = min;
        this.max = max;
        this.prezzo = prezzo;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public String getPrezzo() {
        return prezzo;
    }
}
