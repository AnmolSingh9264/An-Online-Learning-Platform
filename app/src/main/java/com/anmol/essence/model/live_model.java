package com.anmol.essence.model;

public class live_model {
    String img,subject,chapter,time,video;
    public live_model(){
    }
    public live_model(String img,String subject, String chapter, String time,String video){
        this.chapter=chapter;
        this.img=img;
        this.subject=subject;
        this.time=time;
        this.video=video;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
