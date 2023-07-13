package com.anmol.essence.model;

public class batch_model {
    String duration,title,time,size,occupied,path,img;
    public batch_model(){}
    public batch_model(String duration,String img, String title,String time,String size,String occupied,String path){
        this.duration=duration;
        this.title=title;
        this.time=time;
        this.size=size;
        this.occupied=occupied;
        this.path=path;
        this.img=img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getOccupied() {
        return occupied;
    }

    public void setOccupied(String occupied) {
        this.occupied = occupied;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
