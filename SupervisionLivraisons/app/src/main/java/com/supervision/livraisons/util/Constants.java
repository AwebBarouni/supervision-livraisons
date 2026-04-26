package com.supervision.livraisons.util;

public final class Constants {

    public static final String BASE_URL = "http://172.20.10.2:8081/";
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

    private Constants() {
    }
}
