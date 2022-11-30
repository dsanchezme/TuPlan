package com.dadm.tuplan.models;

import java.io.Serializable;
import java.util.Map;

public class User implements Serializable {

    private String name;
    private String email;


    public User(Map <String,Object> map){
        this.name = (String) map.get("name");
        this.email = (String) map.get("email");
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
