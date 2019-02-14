package com.smartlook.consentapi.data

data class Consent(private val key: String,
                   var given: Boolean) {

    fun isGiven() = given
}