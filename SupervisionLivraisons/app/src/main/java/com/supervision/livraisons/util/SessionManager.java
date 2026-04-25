package com.supervision.livraisons.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public final class SessionManager {

    private SessionManager() {
    }

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
    }

    public static void saveSession(Context context, String token, String userId, String name, String role) {
        prefs(context).edit()
                .putString(Constants.KEY_TOKEN, token)
                .putString(Constants.KEY_USER_ID, userId)
                .putString(Constants.KEY_USER_NAME, name)
                .putString(Constants.KEY_USER_ROLE, role)
                .apply();
    }

    public static String getToken(Context context) {
        return prefs(context).getString(Constants.KEY_TOKEN, null);
    }

    public static String getUserId(Context context) {
        return prefs(context).getString(Constants.KEY_USER_ID, null);
    }

    public static String getUserName(Context context) {
        return prefs(context).getString(Constants.KEY_USER_NAME, null);
    }

    public static String getUserRole(Context context) {
        return prefs(context).getString(Constants.KEY_USER_ROLE, null);
    }

    public static boolean isLoggedIn(Context context) {
        String token = getToken(context);
        return !TextUtils.isEmpty(token);
    }

    public static void clearSession(Context context) {
        prefs(context).edit().clear().apply();
    }
}
