package com.github.tvbox.osc.util;

import java.util.List;

public class CollUtil {

    public static int findIndex(List<Integer> list, Integer str) {
        int index = -1;
        if (list == null || list.isEmpty() || str == null) {
            return index;
        }

        for (int i = 0; i < list.size(); i++) {
            if (str.equals(list.get(i))) {
                index = i;
                break;
            }
        }
        return index;
    }
}