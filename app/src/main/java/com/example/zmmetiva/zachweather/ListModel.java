package com.example.zmmetiva.zachweather;

/**
 * Created by zmmetiva on 12/11/15.
 */
public class ListModel {

    private  String high = "";
    private  String low = "";
    private  String date = "";
    private  int image;

    public void setHigh(String high)
    {
        this.high = high;
    }

    public void setLow(String low)
    {
        this.low = low;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getHigh()
    {
        return this.high;
    }

    public String getLow()
    {
        return this.low;
    }

    public String getDate()
    {
        return this.date;
    }

    public int getImage() {
        return image;
    }
}