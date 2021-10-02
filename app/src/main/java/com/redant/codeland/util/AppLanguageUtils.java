package com.redant.codeland.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import com.redant.codeland.app.MyApplication;

import java.util.Locale;


/**
 * Created by pjh on 2018/7/16.
 */

public class AppLanguageUtils {
    public static Context attachBaseContext(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context);
        } else {
            return context;
        }
    }


    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context) {
        Resources resources = context.getResources();
        Configuration configuration = resources.getConfiguration();
        if (MyApplication.languageFlag==0){
            configuration.locale = Locale.ENGLISH;
        }
        else {
            configuration.locale = Locale.CHINESE;
        }
        return context.createConfigurationContext(configuration);
    }
}
