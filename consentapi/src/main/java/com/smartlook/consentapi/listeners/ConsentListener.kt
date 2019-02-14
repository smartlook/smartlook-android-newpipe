package com.smartlook.consentapi.listeners

interface ConsentListener {
    fun onConsentResult(consentKeys: Array<String>, grantResults: BooleanArray)
}