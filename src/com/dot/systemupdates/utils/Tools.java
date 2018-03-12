package com.dot.systemupdates.utils;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import java.util.Objects;

public class Tools {
    public static Boolean doesPropExist(String propName) {
        return !Objects.equals(SystemProperties.get(propName), "");
    }
    public static String getProp(String propName) {
        return SystemProperties.get(propName);
    }
    public static boolean recovery(Context context) {
        return rebootPhone(context, "recovery");
    }

    private static boolean rebootPhone(Context context, String type) {
        boolean success = true;
        try {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            powerManager.reboot("recovery");
        } catch (Exception e) {
            success = false;
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {
            }
            Log.e("Tools", "reboot '"+type+"' error: "+e.getMessage());
        }
        return success;
    }
}
