package com.dani.rally_fotografa;

public class Foto {
    private String url;
    private String uid;
    private String estado;
    private int votos; // ✅ nuevo campo

    public Foto() {
        // Constructor vacío necesario para Firebase
    }

    public Foto(String url, String uid, String estado, int votos) {
        this.url = url;
        this.uid = uid;
        this.estado = estado;
        this.votos = votos;
    }

    public String getUrl() {
        return url;
    }

    public String getUid() {
        return uid;
    }

    public String getEstado() {
        return estado;
    }

    public int getVotos() {
        return votos;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }
}
