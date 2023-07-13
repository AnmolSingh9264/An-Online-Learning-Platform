package com.anmol.essence.model;

public class games_model {
    String img,url;
    public games_model(){
    }
    public games_model(String img,String url){
        this.img=img;
        this.url=url;
    }
    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
