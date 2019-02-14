package com.smartlook.consentapi

import android.content.Context
import android.content.ContextWrapper
import com.smartlook.consentapi.data.Consent
import com.smartlook.consentapi.helpers.SharedPreferencesHelper

class ConsentApi(context: Context) : ContextWrapper(context) {

    private val sharedPreferences = SharedPreferencesHelper(this)

    fun getConsent(key: String) = Consent(key, sharedPreferences.loadBoolean(key))

}