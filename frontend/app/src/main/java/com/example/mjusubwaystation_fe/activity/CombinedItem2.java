package com.example.mjusubwaystation_fe.activity;

class CombinedItem2 {
    private int imageResource;
    private int imageResource2;
    private String text;

    public CombinedItem2(int imageResource2, int imageResource, String text) {
        this.imageResource = imageResource;
        this.imageResource2 = imageResource2;
        this.text = text;
    }

    public int getImageResource() {
        return imageResource;
    }
    public int getImageResource2() {
        return imageResource2;
    }
    public String getText() {return text;}
}