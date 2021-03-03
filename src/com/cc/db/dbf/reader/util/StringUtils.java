package com.cc.db.dbf.reader.util;

public class StringUtils {
    public static String rightPad(String str,
                                  int size,
                                  char padChar) {
        if (str.length() >= size)
            return str;
        StringBuilder sb = new StringBuilder(size
                + 1).append(str);
        while (sb.length() < size) {
            sb.append(padChar);
        }
        return sb.toString();
    }
}
