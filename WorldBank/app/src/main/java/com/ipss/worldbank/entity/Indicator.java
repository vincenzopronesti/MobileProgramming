package com.ipss.worldbank.entity;

public class Indicator {
    private String id;
    private String name;
    private String source;
    private String organizationName;
    private String sourceDescription;

    public Indicator(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Indicator(String name, String source, String organizationName, String sourceDescription) {
        this.name = name;
        this.source = source;
        this.organizationName = organizationName;
        this.sourceDescription = sourceDescription;
    }

    public Indicator() {

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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSourceDescription() {
        return sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }
}
