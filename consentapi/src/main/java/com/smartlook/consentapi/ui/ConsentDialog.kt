package com.smartlook.consentapi.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Window
import android.view.WindowManager
import com.smartlook.consentapi.ConsentApi
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.Consent
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.listeners.ConsentItemListener
import com.smartlook.consentapi.listeners.ConsentListener
import kotlinx.android.synthetic.main.consent_dialog.*

//todo make all text Spannable so they can be formated
class ConsentDialog(context: Context,
                    val consentApi: ConsentApi,
                    val title: String,
                    val text: String,
                    val confirmButtonText: String,
                    val consentItems: Array<ConsentItem>?,
                    val consentListener: ConsentListener) : Dialog(context) {

    private val consents: Array<Consent> by lazy {
        obtainConsents(consentItems ?: arrayOf())
    }

    private val linearLayoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.consent_dialog)

        displayConsentItems()
    }

    private fun displayConsentItems() {
        consentItems ?: return

        with(consent_dialog_recycler_view) {
            hasFixedSize()
            layoutManager = linearLayoutManager
            adapter = ConsentItemAdapter(context, consents, consentItems, createConsentItemListener())
        }
    }

    private fun obtainConsents(consentItems: Array<ConsentItem>): Array<Consent> =
            consentItems.map { consentApi.getConsent(it.key) }.toTypedArray()

    private fun createConsentItemListener() = object : ConsentItemListener {
        override fun onConsentChange(itemIndex: Int, consent: Boolean) {
            consents[itemIndex].given = consent
        }
    }

    override fun show() {
        super.show()

        with(window ?: return) {
            setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT)
        }
    }

}
