package com.smartlook.consentapi.data

import java.io.Serializable

data class ConsentItem(val key: String,
                       val required: Boolean,
                       val text: String,
                       val link: String?) : Serializable