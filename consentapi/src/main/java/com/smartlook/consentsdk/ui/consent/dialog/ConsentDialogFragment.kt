package com.smartlook.consentsdk.ui.consent.dialog

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentActivity
import com.smartlook.consentsdk.data.Consent
import com.smartlook.consentsdk.listeners.ConsentListener
import java.security.InvalidParameterException

class ConsentDialogFragment : DialogFragment() {

    companion object {
        private const val CONSENT_DIALOG_FRAGMENT_TAG = "consent_dialog_fragment"

        fun show(activity: FragmentActivity, consent: Consent) {
            val consentDialogFragment = ConsentDialogFragment().apply {
                arguments = consent.createBundle()
            }

            consentDialogFragment.show(activity.supportFragmentManager, CONSENT_DIALOG_FRAGMENT_TAG)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val consent = Consent.constructFromBundle(arguments) ?: throw InvalidParameterException()

        return ConsentDialog(context!!, consent, object : ConsentListener {
            override fun onConsentResult(consentKeys: Array<String>, grantResults: BooleanArray) {
                //todo return result here
            }
        })
    }

}
