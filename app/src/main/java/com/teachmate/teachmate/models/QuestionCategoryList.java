package com.teachmate.teachmate.models;

import com.teachmate.teachmate.questions.Ask_question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ARavi on 3/4/2015.
 */
public class QuestionCategoryList {

    private String physics="PHYSICS";

    private List<String> list;

    public void categories(){
        list=new ArrayList<String>();
        list.add(physics);
    }

    public List<String> getList() {
        return list;
    }
}
