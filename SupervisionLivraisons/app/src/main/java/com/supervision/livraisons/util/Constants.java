package com.supervision.livraisons.util;

import android.content.Context;
import android.os.Build;

import com.supervision.livraisons.BuildConfig;

public final class Constants {

    public static final String HOST_DEVICE_USB = "192.168.1.40";
    public static final String HOST_EMULATOR = "10.0.2.2";
    public static final String PREF_NAME = "SupervisionPrefs";
    public static final String KEY_TOKEN = "jwt_token";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_USER_ROLE = "user_role";

    public static final String ROLE_LIVREUR = "LIVREUR";
    public static final String ROLE_CONTROLEUR = "CONTROLEUR";

    public static final String STATUS_EN_ATTENTE = "EN_ATTENTE";
    public static final String STATUS_EN_COURS = "EN_COURS";
    public static final String STATUS_LIVRE = "LIVRE";
    public static final String STATUS_ECHOUE = "ECHOUE";

    public static final String EXTRA_DELIVERY_ID = "delivery_id";
    public static final String EXTRA_USER_ID = "partner_user_id";
    public static final String EXTRA_USER_NAME = "partner_user_name";

    public static String getBaseUrl(Context context) {
        return "http://" + getBackendHost(context) + ":" + BuildConfig.BACKEND_PORT + "/api/";
    }

    public static String getWebSocketEmergencyUrl(Context context) {
        return "ws://" + getBackendHost(context) + ":" + BuildConfig.BACKEND_PORT + "/ws/emergency";
    }

    public static String getBackendHost(Context context) {
        if (!BuildConfig.BACKEND_HOST.isEmpty()) {
            return BuildConfig.BACKEND_HOST;
        }
        return isEmulator() ? HOST_EMULATOR : HOST_DEVICE_USB;
    }

    private static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
                || "google_sdk".equals(Build.PRODUCT);
    }

    private Constants() {
    }
}
