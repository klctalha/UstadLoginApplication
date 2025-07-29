package com.talhakilic.ustadloginmobileapp.utils

import kotlin.text.matches
import kotlin.text.startsWith

object Validation {

    fun isStrictlyValidPhoneNumber(phone: String): Boolean {
        return when {
            phone.startsWith("+90") -> phone.matches(Regex("^\\+90\\d{10}$"))
            phone.startsWith("+1") -> phone.matches(Regex("^\\+1\\d{10}$"))
            phone.startsWith("+34") -> phone.matches(Regex("^\\+34\\d{9}$"))
            phone.startsWith("+44") -> phone.matches(Regex("^\\+44\\d{10}$"))
            phone.startsWith("+49") -> phone.matches(Regex("^\\+49\\d{10}$"))
            phone.startsWith("+33") -> phone.matches(Regex("^\\+33\\d{9}$"))
            phone.startsWith("+81") -> phone.matches(Regex("^\\+81\\d{10}$"))
            else -> false
        }
    }
}