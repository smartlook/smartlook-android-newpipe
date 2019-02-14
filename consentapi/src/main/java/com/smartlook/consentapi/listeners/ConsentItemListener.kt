package com.smartlook.consentapi.listeners

interface ConsentItemListener {
    fun onConsentChange(itemIndex: Int, consent: Boolean)
}