package org.schabi.newpipe.smartlook;

import android.content.Context;
import android.content.SharedPreferences;

import com.smartlook.sdk.smartlook.api.anotations.SmartlookServer;

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

    // Run in experimental mode
    private static final String SMARTLOOK_RUN_IN_EXPERIMENTAL_MODE = "smartlook_run_in_experimental_mode";

    public static int loadServerSelection(Context context) {
        return getSharedPreferences(context).getInt(SMARTLOOK_SERVER_PREFERENCE, SmartlookServer.PRODUCTION);
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
            case SmartlookServer.ALFA:
                return SMARTLOOK_ALFA_API_KEY;
            case SmartlookServer.BETA:
                return SMARTLOOK_BETA_API_KEY;
            case SmartlookServer.PRODUCTION:
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

    public static void storeRunInExperimentalMode(Context context, boolean runInExperimentalMode) {
        getSharedPreferences(context)
                .edit()
                .putBoolean(SMARTLOOK_RUN_IN_EXPERIMENTAL_MODE, runInExperimentalMode)
                .apply();

    }

    public static boolean loadRunInExperimentalMode(Context context) {
        return getSharedPreferences(context).getBoolean(SMARTLOOK_RUN_IN_EXPERIMENTAL_MODE, false);
    }

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SMARTLOOK_SAMPLE_APP_PREFERENCES, Context.MODE_PRIVATE);
    }

    private static String getServerApiKeyPreferenceKey(int server) {
        switch (server) {
            case SmartlookServer.ALFA:
                return SMARTLOOK_ALFA_API_KEY_PREFERENCE;
            case SmartlookServer.BETA:
                return SMARTLOOK_BETA_API_KEY_PREFERENCE;
            case SmartlookServer.PRODUCTION:
                return SMARTLOOK_PRODUCTION_API_KEY_PREFERENCE;
        }

        return null;
    }
}
