package com.smartlook.consentsdk.data

import android.os.Bundle
import java.io.Serializable

data class Consent(val titleText: String,
                   val descriptionText: String,
                   val confirmButtonText: String,
                   val consentItems: Array<ConsentItem>) : Serializable {

    companion object {
        private const val CONSENT_EXTRA = "CONSENT_EXTRA"

        fun constructFromBundle(bundle: Bundle?): Consent? {
            return if (bundle != null && bundle.containsKey(CONSENT_EXTRA)) {
                bundle.getSerializable(CONSENT_EXTRA) as Consent
            } else {
                null
            }
        }
    }

    fun createBundle() = Bundle().apply { putSerializable(CONSENT_EXTRA, this@Consent) }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Consent

        if (titleText != other.titleText) return false
        if (descriptionText != other.descriptionText) return false
        if (confirmButtonText != other.confirmButtonText) return false
        if (!consentItems.contentEquals(other.consentItems)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = titleText.hashCode()
        result = 31 * result + descriptionText.hashCode()
        result = 31 * result + confirmButtonText.hashCode()
        result = 31 * result + consentItems.contentHashCode()
        return result
    }
}