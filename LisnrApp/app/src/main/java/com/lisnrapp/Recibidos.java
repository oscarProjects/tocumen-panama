package com.lisnrapp;

public class Recibidos {

    private String id;
    private String url;
    private String titulo;
    private String hour;

    public Recibidos(String id, String url, String titulo, String hour) {
        this.id = id;
        this.url = url;
        this.titulo = titulo;
        this.hour = hour;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
