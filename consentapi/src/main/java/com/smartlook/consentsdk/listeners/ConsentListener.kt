package com.smartlook.consentsdk.listeners

interface ConsentListener {
    fun onConsentResult(consentKeys: Array<String>, grantResults: BooleanArray)
}