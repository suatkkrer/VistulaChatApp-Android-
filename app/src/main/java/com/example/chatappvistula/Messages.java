package com.example.chatappvistula;

public class Messages {

    private String from_whom,message,type;

    public Messages() {
    }

    public Messages(String from_whom, String message, String type) {
        this.from_whom = from_whom;
        this.message = message;
        this.type = type;
    }

   public String getFrom_whom() {
        return from_whom;
    }

    public void setFrom_whom(String from_whom) {
       this.from_whom = from_whom;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}


