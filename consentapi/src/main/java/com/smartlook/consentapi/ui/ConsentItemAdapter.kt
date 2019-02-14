package com.smartlook.consentapi.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.smartlook.consentapi.R
import com.smartlook.consentapi.data.Consent
import com.smartlook.consentapi.data.ConsentItem
import com.smartlook.consentapi.helpers.UtilsHelper
import com.smartlook.consentapi.listeners.ConsentItemListener
import kotlinx.android.synthetic.main.consent_item.view.*

class ConsentItemAdapter(private val context: Context,
                         private val consents: Array<Consent>,
                         private val consentItems: Array<ConsentItem>,
                         private val consentItemListener: ConsentItemListener) : RecyclerView.Adapter<ConsentItemAdapter.ConsentItemVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ConsentItemVH(LayoutInflater
                    .from(context)
                    .inflate(R.layout.consent_item, parent, false))

    override fun onBindViewHolder(holder: ConsentItemVH, itemIndex: Int) {
        with(holder) {
            bind(consents[itemIndex], consentItems[itemIndex])
            registerListener(itemIndex, consentItemListener)
        }
    }

    override fun getItemCount() = consentItems.size

    inner class ConsentItemVH(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(consent: Consent, consentItem: ConsentItem) {
            with(itemView) {
                consent_item_text.text = consentItem.text
                consent_item_switch.isChecked = consent.isGiven()

                UtilsHelper.hideViewIfNull(consentItem.linkListener, consent_item_link)
                consent_item_link.setOnClickListener(consentItem.linkListener)
            }
        }

        fun registerListener(itemIndex: Int, consentItemListener: ConsentItemListener) {
            itemView.consent_item_switch.setOnCheckedChangeListener { _, isChecked ->
                consentItemListener.onConsentChange(itemIndex, isChecked)
            }
        }

    }
}