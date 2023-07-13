package com.anmol.essence.model;

public class video_model {
    String img,title,duration,url;
    public video_model(){
    }
    public video_model(String img, String title,String duration, String url){
        this.img=img;
        this.title= title;
        this.duration=duration;
        this.url=url;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
