package com.smartlook.consentapi.data

import android.os.Parcel
import android.os.Parcelable

data class ConsentItem(val key: String,
                       val required: Boolean,
                       val text: String,
                       val link: String?) : Parcelable {

    companion object {

        @JvmField
        val CREATOR = object : Parcelable.Creator<ConsentItem> {
            override fun createFromParcel(parcel: Parcel): ConsentItem {
                return ConsentItem(parcel)
            }

            override fun newArray(size: Int): Array<ConsentItem?> {
                return arrayOfNulls(size)
            }
        }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readString() ?: "",
            parcelIn.readInt() == 1,
            parcelIn.readString() ?: "",
            parcelIn.readString())

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        with(dest ?: return) {
            writeString(key)
            writeInt(if (required) 1 else 0)
            writeString(text)
            writeString(link)
        }
    }

    override fun describeContents() = 0

}