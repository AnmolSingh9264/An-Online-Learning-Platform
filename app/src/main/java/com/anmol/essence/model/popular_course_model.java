package com.anmol.essence.model;

public class popular_course_model {
    String title,rate,langauge,hours,image;
    public popular_course_model(){

    }
    public popular_course_model(String title,String  rate,String langauge,String hours,String image){
        this.title=title;
        this.rate=rate;
        this.langauge=langauge;
        this.hours=hours;
        this.image=image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getLangauge() {
        return langauge;
    }

    public void setLangauge(String langauge) {
        this.langauge = langauge;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
