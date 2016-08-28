package com.teachmate.teachmate.models;

public class Answer_Model {
    public String actualanswer;
    public String answeredby;
    public String answer_id;
    public String question_id;
    public String answeredtime;


    public String getAnswer_id() {
        return answer_id;
    }

    public void setAnswer_id(String answer_id) {
        this.answer_id = answer_id;
    }

    public String getQuestion_id() {
        return question_id;
    }

    public void setQuestion_id(String question_id) {
        this.question_id = question_id;
    }

    public String getActualanswer() {
        return actualanswer;
    }

    public void setActualanswer(String actualanswer) {
        this.actualanswer = actualanswer;
    }

    public String getAnsweredby() {
        return answeredby;
    }

    public void setAnsweredby(String answeredby) {
        this.answeredby = answeredby;
    }

    public String getAnsweredtime() {
        return answeredtime;
    }

    public void setAnsweredtime(String answeredtime) {
        this.answeredtime = answeredtime;
    }
}
