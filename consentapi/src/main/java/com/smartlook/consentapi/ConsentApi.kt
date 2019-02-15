package com.smartlook.consentapi

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.helpers.SharedPreferencesHelper
import com.smartlook.consentapi.listeners.ConsentListener
import com.smartlook.consentapi.ui.consent.activity.ConsentActivity
import com.smartlook.consentapi.ui.consent.dialog.ConsentDialog

class ConsentApi(context: Context) : ContextWrapper(context) {

    private val sharedPreferences = SharedPreferencesHelper(this)

    fun loadConsent(key: String) = sharedPreferences.loadBoolean(key)

    fun saveConsent(key: String, grantResult: Boolean) = sharedPreferences.saveBoolean(key, grantResult)

    fun showConsentDialog() = ConsentDialog(
            this,
            "Consent api dialog",
            "Curabitur sagittis hendrerit ante. Aenean fermentum risus id tortor. Integer in sapien. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Aliquam ornare wisi eu metus. Etiam dui sem, fermentum vitae, sagittis id, malesuada in, quam.",
            "This is my will",
            arrayOf(
                    ConsentItem("AGE_CONSENT", true, "I certify that I'm over the age of fifteen, have read, understood adn accepted Privacy Policy.", null),
                    ConsentItem("SDK_CONSENT", false, "I agree to play for free and that my personal data is collected via the SDK tools build into the application.", "https://www.nplix.com/kotlin-parcelable-array-objects/")
            ),
            object : ConsentListener {
                override fun onConsentResult(consentKeys: Array<String>, grantResults: BooleanArray) {

                }
            }
    ).show()

    fun startConsentActivity(activity: Activity) {
        activity.startActivityForResult(
                Intent(activity, ConsentActivity::class.java).apply {
                    putExtra(ConsentActivity.TITLE_EXTRA, "Consent api activity")
                    putExtra(ConsentActivity.TEXT_EXTRA, "Curabitur sagittis hendrerit ante. Aenean fermentum risus id tortor. Integer in sapien. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos hymenaeos. Aliquam ornare wisi eu metus. Etiam dui sem, fermentum vitae, sagittis id, malesuada in, quam.")
                    putExtra(ConsentActivity.CONFIRM_BUTTON_TEXT_EXTRA, "This is my will")
                    putParcelableArrayListExtra(ConsentActivity.CONSENT_ITEMS_EXTRA, arrayListOf(
                            ConsentItem("AGE_CONSENT", true, "I certify that I'm over the age of fifteen, have read, understood adn accepted Privacy Policy.", null),
                            ConsentItem("SDK_CONSENT", false, "I agree to play for free and that my personal data is collected via the SDK tools build into the application.", "https://www.nplix.com/kotlin-parcelable-array-objects/")
                    ))
                }, 1000)
    }

}