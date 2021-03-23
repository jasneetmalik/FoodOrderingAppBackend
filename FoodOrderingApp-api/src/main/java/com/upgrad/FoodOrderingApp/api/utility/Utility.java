package com.upgrad.FoodOrderingApp.api.utility;

public class Utility {

    public static boolean isNullOrEmpty(Object obj) {
        if(obj == null || obj.toString().equals("")) {
            return true;
        }
        return false;
    }
}
