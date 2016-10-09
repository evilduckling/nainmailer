package com.evilduckling.nainmailer.helper;

import android.util.Log;

import com.evilduckling.nainmailer.interfaces.Const;

import java.io.UnsupportedEncodingException;

public class Misc {

    public static String[] explode(String values, String separator) {
        return values.split(separator, -1);
    }

    public static String isoToUtf8(String encodedString) {
        try {
            return new String(encodedString.getBytes("ISO-8859-1"));
        } catch (UnsupportedEncodingException e) {
            Log.e(Const.LOG_TAG, "Cannot convert to utf-8");
            return encodedString;
        }
    }

}
