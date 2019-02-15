package com.smartlook.consentapi.ui.consent.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.ui.consent.ConsentBase
import kotlinx.android.synthetic.main.consent_dialog.*
import java.security.InvalidParameterException

class ConsentActivity : AppCompatActivity() {

    companion object {
        const val TITLE_EXTRA = "TITLE_EXTRA"
        const val TEXT_EXTRA = "TEXT_EXTRA"
        const val CONFIRM_BUTTON_TEXT_EXTRA = "CONFIRM_BUTTON_TEXT_EXTRA"
        const val CONSENT_ITEMS_EXTRA = "CONSENT_ITEMS_EXTRA"

        const val CONSENT_KEYS_EXTRA = "CONSENT_KEYS_EXTRA"
        const val GRANT_RESULTS_EXTRA = "GRANT_RESULTS_EXTRA"
    }

    private lateinit var title: String
    private lateinit var text: String
    private lateinit var confirmButtonText: String

    private var consentItems: Array<ConsentItem>? = null
    private lateinit var consentBase: ConsentBase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.consent_activity)
        parseOutExtras(intent.extras)

        consentBase = ConsentBase(
                consentItems,
                root,
                createResultListener(),
                grantResults = restoreGrantResults(savedInstanceState))

        with(consentBase) {
            displayTexts(title, text, confirmButtonText)
            displayConsentItems()

            updateConfirmButton()
            handleConfirmButton()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putBooleanArray(GRANT_RESULTS_EXTRA, consentBase.grantResults)
    }

    private fun parseOutExtras(extras: Bundle?) {
        with(extras ?: throw InvalidParameterException()) {
            title = getString(TITLE_EXTRA) ?: ""
            text = getString(TEXT_EXTRA) ?: ""
            confirmButtonText = getString(CONFIRM_BUTTON_TEXT_EXTRA) ?: ""
            consentItems = getParcelableArrayList<ConsentItem>(CONSENT_ITEMS_EXTRA)?.toTypedArray()
        }
    }

    private fun restoreGrantResults(savedInstanceState: Bundle?): BooleanArray? {
        return if (savedInstanceState == null || !savedInstanceState.containsKey(GRANT_RESULTS_EXTRA)) {
            null
        } else {
            savedInstanceState.getBooleanArray(GRANT_RESULTS_EXTRA)
        }
    }

    private fun createResultListener(): ConsentBase.ResultListener {
        return object : ConsentBase.ResultListener {
            override fun onResult(consentKeys: Array<String>, grantResults: BooleanArray) {
                setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(CONSENT_KEYS_EXTRA, consentKeys)
                    putExtra(GRANT_RESULTS_EXTRA, consentKeys)
                })
                finish()
            }
        }
    }
}