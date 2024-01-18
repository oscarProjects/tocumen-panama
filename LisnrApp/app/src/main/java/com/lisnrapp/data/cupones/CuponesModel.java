package com.lisnrapp.data.cupones;

public class CuponesModel {
    private int idCupon;
    private String descriptionCupon;
    private int imageCupon;
    private boolean showImageCupon;
    private boolean showButton;
    private int imageLogo;
    private boolean showImageLogo;

    public CuponesModel(int idCupon, String descriptionCupon, int imageCupon, boolean showImageCupon, boolean showButton, int imageLogo, boolean showImageLogo) {
        this.idCupon = idCupon;
        this.descriptionCupon = descriptionCupon;
        this.imageCupon = imageCupon;
        this.showImageCupon = showImageCupon;
        this.showButton = showButton;
        this.imageLogo = imageLogo;
        this.showImageLogo = showImageLogo;
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

    public boolean isShowImageCupon() {
        return showImageCupon;
    }

    public void setShowImageCupon(boolean showImageCupon) {
        this.showImageCupon = showImageCupon;
    }

    public boolean isShowButton() {
        return showButton;
    }

    public void setShowButton(boolean showButton) {
        this.showButton = showButton;
    }

    public int getImageLogo() {
        return imageLogo;
    }

    public void setImageLogo(int imageLogo) {
        this.imageLogo = imageLogo;
    }

    public boolean isShowImageLogo() {
        return showImageLogo;
    }

    public void setShowImageLogo(boolean showImageLogo) {
        this.showImageLogo = showImageLogo;
    }
}
