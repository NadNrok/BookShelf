package com.limethecoder.util;


import java.util.List;
import java.util.StringJoiner;

public class DisplayUtil {
    public final static String LIST_DELIMETER = ", ";

    public static String printList(List lst) {
        if(lst == null || lst.isEmpty()) {
            return "";
        }

        if(lst.size() == 1) {
            return lst.get(0).toString();
        }

        StringJoiner joiner = new StringJoiner(LIST_DELIMETER);
        lst.forEach((x)-> joiner.add(x.toString()));
        return joiner.toString();
    }
}
