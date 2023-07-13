package com.anmol.essence.model;

public class Class_model {
    String title,node;
    public Class_model(){

    }

    public Class_model(String title, String node) {
        this.title = title;
        this.node = node;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNode() {
        return node;
    }

    public void setNode(String node) {
        this.node = node;
    }
}
