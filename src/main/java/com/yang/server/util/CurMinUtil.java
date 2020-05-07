package com.yang.server.util;

public class CurMinUtil {

    public static int getCurrentMinuteTime() {
        long curSec = System.currentTimeMillis();
        curSec -= curSec % (1000 * 60);
        return  (int) (curSec / 1000);
    }
}
