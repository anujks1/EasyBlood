package com.teachmate.teachmate;

/**
 * Created by NiRavishankar on 1/19/2015.
 */

import android.support.v4.app.Fragment;

import java.util.Stack;

/**
 * The purpose of this class is to avoid DB hits all the time in the app.
 */
public class TempDataClass {

    public static String deviceRegId;

    public static String serverUserId;
    public static String userProfession;
    public static String userName;
    public static String emailId;

    public static String currentLattitude;
    public static String currentLongitude;

    public static Stack<Fragment> fragmentStack = new Stack<Fragment>();

    public static Stack<Fragment> signUpStack = new Stack<>();

    public static boolean isThroughSplash = false;

    public static String profilePhotoLocalPath = "";
    public static String profilePhotoServerPath = "";
    public static boolean alreadyAdded = false;

    public static boolean newSignUpProfilePhotoReturn = false;
}
