package com.smartlook.consentapi.ui.consent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Window
import android.view.WindowManager
import com.smartlook.consentapi.ConsentApi
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.listeners.ConsentItemListener
import com.smartlook.consentapi.listeners.ConsentListener
import com.smartlook.consentapi.ui.consent.ConsentItemAdapter
import kotlinx.android.synthetic.main.consent_dialog.*

//todo make all text Spannable so they can be formated
class ConsentDialog(context: Context,
                    private val consentApi: ConsentApi,
                    private val title: String,
                    private val text: String,
                    private val confirmButtonText: String,
                    private val consentItems: Array<ConsentItem>?,
                    private val consentListener: ConsentListener) : Dialog(context) {

    private val consentKeys = obtainConsentKeys(consentItems ?: arrayOf())
    private val grantResults = obtainGrantResults(consentItems ?: arrayOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.consent_dialog)

        displayDialogData()
        displayConsentItems()

        updateConfirmButton()
        handleConfirmButton()
    }

    override fun show() {
        setCancelable(false)
        super.show()

        with(window ?: return) {
            setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT)
        }
    }

    private fun displayDialogData() {
        consent_dialog_title_text.text = title
        consent_dialog_text.text = text
        consent_dialog_confirm_button.text = confirmButtonText
    }

    // recycler view should have nested scroll
    private fun displayConsentItems() {
        consentItems ?: return

        with(consent_dialog_recycler_view) {
            hasFixedSize()
            layoutManager = LinearLayoutManager(context)
            adapter = ConsentItemAdapter(context, grantResults, consentItems, createConsentItemListener())
        }
    }

    private fun updateConfirmButton() {
        var enable = true

        consentItems?.forEachIndexed { index, item ->
            if (item.required && !grantResults[index]) {
                enable = false
            }
        }

        consent_dialog_confirm_button.isEnabled = enable
    }

    private fun handleConfirmButton() {
        consent_dialog_confirm_button.setOnClickListener {
            consentListener.onConsentResult(consentKeys, grantResults)
            dismiss()
        }
    }

    private fun obtainGrantResults(consentItems: Array<ConsentItem>) =
            consentItems.map { consentApi.getConsent(it.key) }.toBooleanArray()

    private fun obtainConsentKeys(consentItems: Array<ConsentItem>) =
            consentItems.map { it.key }.toTypedArray()

    private fun createConsentItemListener() = object : ConsentItemListener {
        override fun onConsentChange(itemIndex: Int, consent: Boolean) {
            grantResults[itemIndex] = consent
            updateConfirmButton()
        }
    }

}
