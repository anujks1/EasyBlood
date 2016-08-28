
package com.teachmate.teachmate.Chat;

public class Message {

    String message;

    boolean isMine;

    String time;

    public Message(String message, boolean isMine, String time) {
        super();
        this.message = message;
        this.isMine = isMine;
        this.time = time;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isMine() {
        return isMine;
    }

    public void setMine(boolean isMine) {
        this.isMine = isMine;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

}
