package com.invest.trade.util;

import android.os.Environment;
import android.util.Log;

import com.invest.trade.data.model.Active;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class Utils {
    private static final String TAG = "MyTag";

    public static void log(String str){
        Log.d(TAG,str);
    }

    public static Float getCurrentRate(Active active) {
        String currency = active.getCurrentRate();
        currency = currency.replace(",","");
        Float currentRate = Float.valueOf(currency);
        return currentRate;
    }

    public static Long getCurrentTime(Active active) {
        String timeStr = active.getTimestamp();

        Long timestamp = Long.valueOf(timeStr.substring(0,10));
        timestamp = (timestamp + 6*60*60)*1000;

//        Timestamp stamp = new Timestamp(timestamp);
//        Date date = new Date(stamp.getTime());

        return timestamp;
    }
}
