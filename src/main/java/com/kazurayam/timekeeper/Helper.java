package com.kazurayam.timekeeper;

public class Helper {

    public static String getClassName() {
        return Thread.currentThread().getStackTrace()[2].getClassName();
    }

}
