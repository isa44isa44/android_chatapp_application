package com.example.chatapp;

public class Messages
{
        private String message,type;
        private long time;
        private String from;


        public Messages()
        {

        }

    public Messages(String message, String type, long time, String from) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
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

    public String from() {
        return from;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
