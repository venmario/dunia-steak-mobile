package com.example.restoapp.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.android.material.datepicker.CalendarConstraints
import java.util.Calendar
import java.util.TimeZone

class DsDateValidator() : CalendarConstraints.DateValidator {
    constructor(parcel: Parcel) : this() {
    }

    override fun describeContents(): Int {
        return 0;
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
    }
    override fun isValid(date: Long): Boolean {
        val currentCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val currentDate = currentCalendar.get(Calendar.DAY_OF_MONTH) - 1
        currentCalendar.set(Calendar.DAY_OF_MONTH, currentDate)
        val nextWeekCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val nextWeekDate = nextWeekCalendar.get(Calendar.DAY_OF_MONTH) - 1
        nextWeekCalendar.set(Calendar.DAY_OF_MONTH, nextWeekDate)
        nextWeekCalendar.add(Calendar.DAY_OF_YEAR, 7)

        val today = currentCalendar.timeInMillis
        val nextWeek = nextWeekCalendar.timeInMillis

        return date in today..nextWeek
    }

    companion object CREATOR : Parcelable.Creator<DsDateValidator> {
        override fun createFromParcel(parcel: Parcel): DsDateValidator {
            return DsDateValidator(parcel)
        }

        override fun newArray(size: Int): Array<DsDateValidator?> {
            return arrayOfNulls(size)
        }
    }
}