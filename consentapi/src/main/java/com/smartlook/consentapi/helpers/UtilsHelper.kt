package com.smartlook.consentapi.helpers

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View

object UtilsHelper {

    fun hideViewIfNull(nullableObject: Any?, view: View) {
        view.visibility = if (nullableObject == null) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    fun addDividersToRecyclerView(recyclerView: RecyclerView,
                                  orientation: Int = DividerItemDecoration.VERTICAL) {
        DividerItemDecoration(recyclerView.context, orientation).let {
            recyclerView.addItemDecoration(it)
        }
    }

    fun openLink(context: Context, link: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(link)))
    }

}