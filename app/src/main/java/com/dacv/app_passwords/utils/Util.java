package com.dacv.app_passwords.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import androidx.core.content.ContextCompat;

import com.dacv.app_passwords.R;

public class Util {

    public static final String USERS = "users";
    public static final String ACCOUNTS = "accounts";
    public static final String ACCOUNT = "account";
    public static final String ID = "id";
    public static final String UID = "uid";

    public static final String KEY = "key";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";


    public static Drawable getLogo(String name, Activity activity){
        String name_logo = name;
        switch (name_logo){
            case "google":
                return ContextCompat.getDrawable(activity, R.drawable.ic_google);
            case "facebook":
                return ContextCompat.getDrawable(activity,R.drawable.ic_facebook);
            case "mega":
                return ContextCompat.getDrawable(activity,R.drawable.ic_mega);
            case "twitter":
                return ContextCompat.getDrawable(activity,R.drawable.ic_twitter);
            case "instagram":
                return ContextCompat.getDrawable(activity,R.drawable.ic_instagram);
            case "platzi":
                return ContextCompat.getDrawable(activity,R.drawable.ic_platzi);
            default:
                return ContextCompat.getDrawable(activity,R.drawable.ic_unknown);
        }
    }
}
