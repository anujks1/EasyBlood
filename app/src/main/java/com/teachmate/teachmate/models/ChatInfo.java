package com.teachmate.teachmate.models;

public class ChatInfo {
    public String message;
    public boolean sentBy;
    public String timeStamp;
    public String chatId;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSentBy() {
        return sentBy;
    }

    public void setSentBy(boolean sentBy) {
        this.sentBy = sentBy;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
