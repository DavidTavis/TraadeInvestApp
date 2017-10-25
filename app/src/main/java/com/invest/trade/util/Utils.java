package com.invest.trade.util;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by TechnoA on 24.10.2017.
 */

public class Utils {
    private static final String TAG = "MyTag";

    public static void log(String str){
        Log.d(TAG,str);
    }

}
