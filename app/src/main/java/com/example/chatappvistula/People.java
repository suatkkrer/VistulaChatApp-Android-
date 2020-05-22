package com.example.chatappvistula;

public class People {

            String name,status,Picture;

    public People() {
    }

    public People(String name, String status, String picture) {
        this.name = name;
        this.status = status;
        Picture = picture;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPicture() {
        return Picture;
    }

    public void setPicture(String picture) {
        Picture = picture;
    }
}
