package com.smartlook.consentapi.listeners

import com.smartlook.consentapi.data.Consent

interface ConsentListener {

    fun onConsentResult(consents: Array<Consent>, consentGiven: Array<Boolean>)

}