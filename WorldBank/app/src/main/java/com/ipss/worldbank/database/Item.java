package com.ipss.worldbank.database;
public class Item {
    private String country;
    private String indicator;

    public Item(String country, String indicator) {
        this.country = country;
        this.indicator = indicator;
    }

    @Override
    public String toString() {
        return country + " - " + indicator;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getIndicator() {
        return indicator;
    }

    public void setIndicator(String indicator) {
        this.indicator = indicator;
    }
}
