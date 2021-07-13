package com.example.tambak_ssmt;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class User {
    String name;
    String uid;
    String pw;
    Boolean gateAccess;
    Boolean admin;

    public User(){

    }

    public User(String name, String uid, String pw, Boolean gateAccess, Boolean admin) {
        this.name = name;
        this.uid = uid;
        this.pw = pw;
        this.gateAccess = gateAccess;
        this.admin = admin;
    }

    //For Firebase RecyclerView
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("uid", uid);
        result.put("pw", pw);
        result.put("gateAccess", gateAccess);
        result.put("admin", admin);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public Boolean getGateAccess() {
        return gateAccess;
    }

    public void setGateAccess(Boolean gateAccess) {
        this.gateAccess = gateAccess;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

}
