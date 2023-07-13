package com.anmol.essence.model;

public class doubt_model {
    String uid,sub,des,ans,qus,root;
    public doubt_model(){
    }
    public doubt_model(String uid, String sub, String des, String ans, String qus,String root){
        this.uid=uid;
        this.sub=sub;
        this.des=des;
        this.ans=ans;
        this.qus=qus;
        this.root=root;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getAns() {
        return ans;
    }

    public void setAns(String ans) {
        this.ans = ans;
    }

    public String getQus() {
        return qus;
    }

    public void setQus(String qus) {
        this.qus = qus;
    }
}
