package com.smartlook.consentapi.helpers

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
                                  orientation: Int = DividerItemDecoration.HORIZONTAL) {

        DividerItemDecoration(recyclerView.context, orientation).let {
            recyclerView.addItemDecoration(it)
        }
    }

}