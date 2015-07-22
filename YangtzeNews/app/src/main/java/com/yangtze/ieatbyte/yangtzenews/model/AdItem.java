package com.yangtze.ieatbyte.yangtzenews.model;

import android.graphics.Bitmap;

public class AdItem extends Item{
    private String adOwner;

    private Bitmap adImage; // for test we just use debug bitmap
                                // Actually, it should also be a url

    public AdItem(int id, String title, String adOwner, Bitmap adImage) {
        super(id, title);
        this.adOwner = adOwner;
        this.adImage = adImage;
    }

    public String getAdOwner() {
        return adOwner;
    }

    public void setAdOwner(String adOwner) {
        this.adOwner = adOwner;
    }

    public Bitmap getAdImage() {
        return adImage;
    }

    public void setAdImage(Bitmap adImage) {
        this.adImage = adImage;
    }

    @Override
    public String toString() {
        return String.format("AdItem{%s, adOwner:%s}", super.toString(), adOwner );
    }
}
