package com.example.mjusubwaystation_fe.activity;

class CombinedItem {
    private int imageResource;
    private String text;

    public CombinedItem(int imageResource, String text) {
        this.imageResource = imageResource;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getText() {return text;}
}