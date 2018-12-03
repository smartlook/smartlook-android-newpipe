package org.schabi.newpipe.smartlook;

import android.content.Context;
import android.content.SharedPreferences;

import com.smartlook.sdk.smartlook.api.client.Server;

public class SmartlookPreferences {

    // Default api keys
    private static final String SMARTLOOK_ALFA_API_KEY = "dce4cb5b71c0c45aed88ad89a11cfe8977807b45";
    private static final String SMARTLOOK_BETA_API_KEY = "c8d68fc8cfc145993b983d4404f85f8d4ff59773";
    private static final String SMARTLOOK_PRODUCTION_API_KEY = "85d3eb951c75ff51df3ca8c24cc4eb51efd68822";

    // Api preference keys
    private static final String SMARTLOOK_ALFA_API_KEY_PREFERENCE = "smartlook_alfa_api_key_preference";
    private static final String SMARTLOOK_BETA_API_KEY_PREFERENCE = "smartlook_beta_api_key_preference";
    private static final String SMARTLOOK_PRODUCTION_API_KEY_PREFERENCE = "smartlook_production_api_key_preference";

    // Server preference key
    private static final String SMARTLOOK_SERVER_PREFERENCE = "smartlook_server_preference";

    // Debug selectors
    private static final String SMARTLOOK_DEBUG_SELECTORS_PREFERENCE = "smartlook_debug_selectors_preference";

    // Private preference key
    private static final String SMARTLOOK_SAMPLE_APP_PREFERENCES = "smartlook_sample_app_preferences";

    public static int loadServerSelection(Context context) {
        return getSharedPreferences(context).getInt(SMARTLOOK_SERVER_PREFERENCE, Server.PRODUCTION);
    }

    public static void storeServerSelection(Context context, int server) {
        getSharedPreferences(context)
                .edit()
                .putInt(SMARTLOOK_SERVER_PREFERENCE, server)
                .apply();
    }

    public static String loadApiKey(Context context, int server) {
        return getSharedPreferences(context).getString(
                getServerApiKeyPreferenceKey(server),
                getDefaultApiKey(server));
    }

    public static void storeApiKey(Context context, String apiKey, int server) {
        getSharedPreferences(context)
                .edit()
                .putString(getServerApiKeyPreferenceKey(server), apiKey)
                .apply();
    }

    public static String getDefaultApiKey(int server) {
        switch (server) {
            case Server.ALFA:
                return SMARTLOOK_ALFA_API_KEY;
            case Server.BETA:
                return SMARTLOOK_BETA_API_KEY;
            case Server.PRODUCTION:
                return SMARTLOOK_PRODUCTION_API_KEY;
        }

        return null;
    }

    public static boolean loadDebugSelectors(Context context) {
        return getSharedPreferences(context).getBoolean(SMARTLOOK_DEBUG_SELECTORS_PREFERENCE, true);
    }

    public static void storeDebugSelectors(Context context, boolean debugSelectors) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(SMARTLOOK_DEBUG_SELECTORS_PREFERENCE, debugSelectors)
                .apply();
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SMARTLOOK_SAMPLE_APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static String getServerApiKeyPreferenceKey(int server) {
        switch (server) {
            case Server.ALFA:
                return SMARTLOOK_ALFA_API_KEY_PREFERENCE;
            case Server.BETA:
                return SMARTLOOK_BETA_API_KEY_PREFERENCE;
            case Server.PRODUCTION:
                return SMARTLOOK_PRODUCTION_API_KEY_PREFERENCE;
        }

        return null;
    }

}
