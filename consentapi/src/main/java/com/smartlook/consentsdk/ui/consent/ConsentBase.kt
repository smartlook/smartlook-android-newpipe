package com.smartlook.consentsdk.ui.consent

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.smartlook.consentsdk.ConsentSDK
import com.smartlook.consentsdk.R
import com.smartlook.consentsdk.data.Consent
import com.smartlook.consentsdk.data.ConsentItem
import com.smartlook.consentsdk.helpers.UtilsHelper
import com.smartlook.consentsdk.listeners.ConsentItemListener

class ConsentBase(private val consent: Consent,
                  rootView: View,
                  private val resultListener: ResultListener,
                  grantResults: BooleanArray? = null) {

    private val consentApi = ConsentSDK(rootView.context)
    private lateinit var consentItemAdapter: ConsentItemAdapter

    var consentKeys: Array<String>
    var grantResults: BooleanArray

    // We are doing it oldschool because it works for both dialog and activity
    private val tvTitle = rootView.findViewById<TextView>(R.id.consent_title)
    private val tvDescription = rootView.findViewById<TextView>(R.id.consent_description)
    private val rvConsentItems = rootView.findViewById<RecyclerView>(R.id.consent_recycler_view)
    private val bConfirm = rootView.findViewById<Button>(R.id.consent_confirm_button)

    init {
        this.consentKeys = obtainConsentKeys(consent.consentItems)
        this.grantResults = grantResults ?: obtainGrantResults(consent.consentItems)
    }

    fun displayConsent() {
        displayTexts()
        displayConsentItems()

        updateConfirmButton()
        handleConfirmButton()
    }

    fun displayTexts() {
        with(consent) {
            tvTitle.text = titleText
            tvDescription.text = descriptionText
            bConfirm.text = confirmButtonText
        }
    }

    // recycler view should have nested scroll
    fun displayConsentItems() {
        with(rvConsentItems) {
            consentItemAdapter = ConsentItemAdapter(context, grantResults, consent.consentItems, createConsentItemListener())
            UtilsHelper.addDividersToRecyclerView(this)
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = consentItemAdapter
        }
    }

    fun updateConfirmButton() {
        var enable = true

        consent.consentItems.forEachIndexed { index, item ->
            if (item.required && !grantResults[index]) {
                enable = false
            }
        }

        bConfirm.isEnabled = enable
    }

    fun handleConfirmButton() {
        bConfirm.setOnClickListener {
            storeGrantResults()
            resultListener.onResult(consentKeys, grantResults)
        }
    }

    private fun storeGrantResults() {
        consentKeys.forEachIndexed { index, key ->
            consentApi.saveConsent(key, grantResults[index])
        }
    }

    private fun obtainGrantResults(consentItems: Array<ConsentItem>) =
            consentItems.map { consentApi.loadConsent(it.key) }.toBooleanArray()

    private fun obtainConsentKeys(consentItems: Array<ConsentItem>) =
            consentItems.map { it.key }.toTypedArray()

    private fun createConsentItemListener() = object : ConsentItemListener {
        override fun onConsentChange(itemIndex: Int, consent: Boolean) {
            grantResults[itemIndex] = consent
            updateConfirmButton()
        }
    }

    interface ResultListener {
        fun onResult(consentKeys: Array<String>, grantResults: BooleanArray)
    }

}