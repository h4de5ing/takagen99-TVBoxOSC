package com.github.tvbox.osc.util;

public class HistoryHelper {

    private static Integer[] hisNumArray = {20, 40, 60, 80, 100};

    public static String getHomeRecName(int index) {
        Integer value = getHisNum(index);
        return value + "条";
    }

    public static int getHisNum(int index) {
        Integer value;
        if (index >= 0 && index < hisNumArray.length) {
            value = hisNumArray[index];
        } else {
            value = hisNumArray[0];
        }
        return value;
    }
}