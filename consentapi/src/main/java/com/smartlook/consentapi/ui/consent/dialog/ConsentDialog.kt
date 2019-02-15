package com.smartlook.consentapi.ui.consent.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.Consent
import com.smartlook.consentapi.listeners.ConsentListener
import com.smartlook.consentapi.ui.consent.ConsentBase
import kotlinx.android.synthetic.main.consent_dialog.*

//todo make all text Spannable so they can be formated
class ConsentDialog(context: Context,
                    private val consent: Consent,
                    private val consentListener: ConsentListener) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.consent_dialog)

        ConsentBase(consent, root, createResultListener()).displayConsent()
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

    private fun createResultListener(): ConsentBase.ResultListener {
        return object : ConsentBase.ResultListener {
            override fun onResult(consentKeys: Array<String>, grantResults: BooleanArray) {
                dismiss()
                consentListener.onConsentResult(consentKeys, grantResults)
            }
        }
    }
}
