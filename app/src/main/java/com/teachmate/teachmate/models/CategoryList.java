package com.teachmate.teachmate.models;

/**
 * Created by ARavi on 3/5/2015.
 */
public class CategoryList {
    public final String[] CATEGORIES = new String[] {
           "Literature","Science","Travel","Cuisine","Technology","Programming","Music","Movies","Sports","Politics","Other","Miscellaneous"
    };

   /* public static String[] getCategories() {
        return CATEGORIES;
    }*/
    public boolean checkinarray(String[] arr,String targetvalue){

        for(String check:arr){
            if(check.equals(targetvalue))
                return true;
        }
        return false;
    }
}
