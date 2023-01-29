package com.github.catvod.crawler;

import android.util.Log;

public class SpiderDebug {
    public static void log(Throwable th) {
        try {
            Log.d("SpiderLog", th.getMessage(), th);
        } catch (Throwable ignored) {

        }
    }

    public static void log(String msg) {
        try {
            Log.d("SpiderLog", msg);
        } catch (Throwable ignored) {

        }
    }

    public static String ec(int i) {
        return "";
    }
}
