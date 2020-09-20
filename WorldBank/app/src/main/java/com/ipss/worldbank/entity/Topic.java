package com.ipss.worldbank.entity;

public class Topic {
    private String id;
    private String value;
    private String sourceNote;

    public Topic(String value, String sourceNote) {
        this.value = value;
        this.sourceNote = sourceNote;
    }

    public Topic() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String name) {
        this.value = name;
    }

    public String getSourceNote() {
        return sourceNote;
    }

    public void setSourceNote(String sourceNote) {
        this.sourceNote = sourceNote;
    }
}
