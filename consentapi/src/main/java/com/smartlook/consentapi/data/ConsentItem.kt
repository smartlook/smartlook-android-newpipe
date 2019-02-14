package com.smartlook.consentapi.data

import android.view.View

data class ConsentItem(val key: String,
                       val required: Boolean,
                       val text: String,
                       val linkListener: View.OnClickListener?)