package bharatia.com.bharatia.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;

import java.util.Locale;

import bharatia.com.bharatia.R;

/**
 * Created by SAMSUNG on 2/5/2018.
 */

public class ChangeLanguage {

    private static String lang = "";

    public static String getLang(Context context) {
        if(lang.equals("")) {

            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.pref_file_key),Context.MODE_PRIVATE);
            String ln = sharedPref.getString(context.getString(R.string.locale), "en");

            lang = ln;
            return lang;
        }else {
            return lang;
        }
    }

    public static void  setLang(String s) {
        lang = s;
    }

    public static ContextWrapper changeLang(Context context, String lang_code){
        Locale sysLocale;

        Resources rs = context.getResources();
        Configuration config = rs.getConfiguration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }
        //if (!lang_code.equals("") && !sysLocale.getLanguage().equals(lang_code)) {
            Locale locale = new Locale(lang_code);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                context = context.createConfigurationContext(config);
            } else {
                context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
            }
        //}

        return new ContextWrapper(context);
    }
}
