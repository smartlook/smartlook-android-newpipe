package com.smartlook.consentsdk

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.support.v4.app.FragmentActivity
import com.smartlook.consentsdk.data.Consent
import com.smartlook.consentsdk.helpers.SharedPreferencesHelper
import com.smartlook.consentsdk.listeners.ConsentListener
import com.smartlook.consentsdk.ui.consent.activity.ConsentActivity
import com.smartlook.consentsdk.ui.consent.dialog.ConsentDialog
import com.smartlook.consentsdk.ui.consent.dialog.ConsentDialogFragment

class ConsentApi(context: Context) : ContextWrapper(context) {

    private val sharedPreferences = SharedPreferencesHelper(this)

    fun loadConsent(key: String) = sharedPreferences.loadBoolean(key)

    fun saveConsent(key: String, grantResult: Boolean) = sharedPreferences.saveBoolean(key, grantResult)

    fun showConsentDialog(consent: Consent, consentListener: ConsentListener) =
            ConsentDialog(this, consent, consentListener).show()


    fun showConsentDialogFragment(activity: FragmentActivity, consent: Consent) {
        ConsentDialogFragment.show(activity, consent)
    }

    fun startConsentActivity(activity: Activity, consent: Consent, requestCode: Int) =
            ConsentActivity.start(activity, consent, requestCode)

}