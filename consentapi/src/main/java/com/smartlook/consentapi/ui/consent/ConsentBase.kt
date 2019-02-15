package com.smartlook.consentapi.ui.consent

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.smartlook.consentapi.ConsentApi
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.helpers.UtilsHelper
import com.smartlook.consentapi.listeners.ConsentItemListener

class ConsentBase(private val consentItems: Array<ConsentItem>?,
                  rootView: View,
                  private val resultListener: ResultListener,
                  consentKeys: Array<String>? = null,
                  grantResults: BooleanArray? = null) {

    private val consentApi = ConsentApi(rootView.context)
    private lateinit var consentItemAdapter: ConsentItemAdapter

    var consentKeys: Array<String>
    var grantResults: BooleanArray

    // We are doing it oldschool because it works for both dialog and activity
    private val tvTitle = rootView.findViewById<TextView>(R.id.consent_title_text)
    private val tvText = rootView.findViewById<TextView>(R.id.consent_text)
    private val rvConsentItems = rootView.findViewById<RecyclerView>(R.id.consent_recycler_view)
    private val bConfirm = rootView.findViewById<Button>(R.id.consent_confirm_button)

    init {
        this.consentKeys = consentKeys ?: obtainConsentKeys(consentItems ?: arrayOf())
        this.grantResults = grantResults ?: obtainGrantResults(consentItems ?: arrayOf())
    }

    fun displayTexts(title: String, text: String, confirmButton: String) {
        tvTitle.text = title
        tvText.text = text
        bConfirm.text = confirmButton
    }

    // recycler view should have nested scroll
    fun displayConsentItems() {
        consentItems ?: return

        with(rvConsentItems) {
            consentItemAdapter = ConsentItemAdapter(context, grantResults, consentItems, createConsentItemListener())
            UtilsHelper.addDividersToRecyclerView(this)
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = consentItemAdapter
        }
    }

    fun updateConfirmButton() {
        var enable = true

        consentItems?.forEachIndexed { index, item ->
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