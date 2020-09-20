package com.ipss.worldbank.entity;

public class Country {
    private String id;
    private String name;
    private String iso2code;

    public Country() {

    }

    public Country(String id, String iso2code, String name) {
        this.id = id;
        this.iso2code = iso2code;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIso2code() {
        return iso2code;
    }

    public void setIso2code(String iso2code) {
        this.iso2code = iso2code;
    }
}
