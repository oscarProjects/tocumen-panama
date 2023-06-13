package com.lisnrapp.model;

public class CuponesModel {
    private int idCupon;
    private String descriptionCupon;
    private int imageCupon;

    public CuponesModel(int idCupon, String descriptionCupon, int imageCupon) {
        this.idCupon = idCupon;
        this.descriptionCupon = descriptionCupon;
        this.imageCupon = imageCupon;
    }

    public int getIdCupon() {
        return idCupon;
    }

    public void setIdCupon(int idCupon) {
        this.idCupon = idCupon;
    }

    public String getDescriptionCupon() {
        return descriptionCupon;
    }

    public void setDescriptionCupon(String descriptionCupon) {
        this.descriptionCupon = descriptionCupon;
    }

    public int getImageCupon() {
        return imageCupon;
    }

    public void setImageCupon(int imageCupon) {
        this.imageCupon = imageCupon;
    }
}
