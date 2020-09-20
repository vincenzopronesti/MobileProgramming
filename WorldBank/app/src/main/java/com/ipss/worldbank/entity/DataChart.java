package com.ipss.worldbank.entity;

import java.io.Serializable;
import java.util.List;

public class DataChart implements Serializable {
    private String country;
    private String indicator;
    private List<Point> pointList;
    private String unit;

    public DataChart(String country, String indicator, List<Point> pointList, String unit) {
        this.country = country;
        this.indicator = indicator;
        this.pointList = pointList;
        this.unit = unit;
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

    public List<Point> getPointList() {
        return pointList;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPointList(List<Point> pointList) {
        this.pointList = pointList;
    }
}
