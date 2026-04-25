package com.supervision.livraisons.util;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;

import com.supervision.livraisons.R;

public final class StatusColorUtil {

    private StatusColorUtil() {
    }

    public static int getColor(Context context, String status) {
        if (Constants.STATUS_EN_COURS.equals(status)) {
            return ContextCompat.getColor(context, R.color.colorStatusEnCours);
        }
        if (Constants.STATUS_LIVRE.equals(status)) {
            return ContextCompat.getColor(context, R.color.colorStatusLivre);
        }
        if (Constants.STATUS_ECHOUE.equals(status)) {
            return ContextCompat.getColor(context, R.color.colorStatusEchoue);
        }
        return ContextCompat.getColor(context, R.color.colorStatusEnAttente);
    }

    public static String getLabel(Context context, String status) {
        if (Constants.STATUS_EN_COURS.equals(status)) {
            return context.getString(R.string.status_en_cours);
        }
        if (Constants.STATUS_LIVRE.equals(status)) {
            return context.getString(R.string.status_livre);
        }
        if (Constants.STATUS_ECHOUE.equals(status)) {
            return context.getString(R.string.status_echoue);
        }
        return context.getString(R.string.status_en_attente);
    }

    public static int getBadgeTextColor(String status) {
        if (Constants.STATUS_LIVRE.equals(status) || Constants.STATUS_ECHOUE.equals(status)) {
            return Color.WHITE;
        }
        return Color.parseColor("#202124");
    }
}
