package brainwaves.com.charu.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Pref {

    private static SharedPreferences sharedPreferences = null;

    private static void openPref(Context context) {
        sharedPreferences = context.getSharedPreferences(Const.PREF_FILE, MODE_PRIVATE);
    }


    /*Fore String Value Store*/
    public static String getStringValue(Context context, String key, String defaultValue) {
        Pref.openPref(context);
        String result = Pref.sharedPreferences.getString(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setStringValue(Context context, String key, String value) {
        Pref.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putString(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }


    /*For Integer Value*/

    public static Integer setIntValue(Context context, String key, int value) {
        Pref.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putInt(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
        return null;
    }

    public static int getIntValue(Context context, String key, int defaultValue) {
        Pref.openPref(context);
        int result = Pref.sharedPreferences.getInt(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }


    /*For boolean Value Store*/

    public static boolean getBooleanValue(Context context, String key, boolean defaultValue) {
        Pref.openPref(context);
        boolean result = Pref.sharedPreferences.getBoolean(key, defaultValue);
        Pref.sharedPreferences = null;
        return result;
    }

    public static void setBooleanValue(Context context, String key, boolean value) {
        Pref.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.putBoolean(key, value);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }


    public static void removeValue(Context context, String key) {
        Pref.openPref(context);
        SharedPreferences.Editor prefsPrivateEditor = Pref.sharedPreferences.edit();
        prefsPrivateEditor.remove(key);
        prefsPrivateEditor.commit();
        prefsPrivateEditor = null;
        Pref.sharedPreferences = null;
    }


}
