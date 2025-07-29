package com.talhakilic.ustadloginmobileapp.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.talhakilic.ustadloginmobileapp.R
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.talhakilic.ustadloginmobileapp.data.repository.UserRepository


class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepository(application.applicationContext)

    private val _showOtpScreen = MutableLiveData<Boolean>()
    val showOtpScreen: LiveData<Boolean> get() = _showOtpScreen

    private val _showCompanySelectionScreen = MutableLiveData<Boolean>()
    val showCompanySelectionScreen: LiveData<Boolean> get() = _showCompanySelectionScreen

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _countryCode = MutableLiveData<String>()
    val countryCode: LiveData<String> get() = _countryCode

    private val _nationalPhoneFormatted = MutableLiveData<String>()
    val nationalPhoneFormatted: LiveData<String> get() = _nationalPhoneFormatted

    var storedVerificationId: String? = null


    private val _companies = MutableLiveData<List<String>>().apply {
        value = listOf("Ustad", "Aselsan", "Turkcell")
    }
    val companies: LiveData<List<String>> get() = _companies

    private val _selectedCompany = MutableLiveData<String?>()
    val selectedCompany: LiveData<String?> get() = _selectedCompany

    fun selectCompany(company: String?) {
        _selectedCompany.value = company
    }

    fun sendVerificationCode(activity: Activity, phoneNumber: String) {
        val cleanedPhoneNumber = phoneNumber.replace("[^+\\d]".toRegex(), "")
        val nationalNumberPart = cleanedPhoneNumber.takeLast(10)
        val countryCodePart = cleanedPhoneNumber.removeSuffix(nationalNumberPart)

        _countryCode.value = countryCodePart
        _nationalPhoneFormatted.value = formatNationalNumber(nationalNumberPart)
        _errorMessage.value = null


        userRepository.sendVerificationCode(activity, cleanedPhoneNumber,
            onSuccess = { verificationId ->
                storedVerificationId = verificationId
                _showOtpScreen.value = true
            },
            onFailure = { exception ->
                _errorMessage.value = mapErrorMessage(activity,exception)
            }
        )
    }

    fun verifyCode(context: Context, code: String) {
        val verificationId = storedVerificationId
        if (verificationId == null) {
            _errorMessage.value = context.getString(R.string.text_error_id_missing)
            return
        }

        userRepository.verifyOtpCode(verificationId,code,
            onSuccess = {
                _showCompanySelectionScreen.value = true
            },
            onError = { exception ->
                _errorMessage.value = mapErrorMessage(context,exception)
            }
        )
    }

    fun clearNavigationFlags() {
        _showOtpScreen.value = false
        _showCompanySelectionScreen.value = false
    }

    private fun formatNationalNumber(number: String): String {
        return if (number.length == 10) {
            val part1 = number.substring(0, 3)
            val part2 = number.substring(3, 6)
            val part3 = number.substring(6, 8)
            val part4 = number.substring(8)
            "$part1 $part2 $part3 $part4"
        } else number
    }

    private fun mapErrorMessage(context: Context, exception: Exception): String {
        return when {
            exception.message?.contains("BILLING_NOT_ENABLED") == true -> {
                context.getString(R.string.text_error_billing_not_enabled)
            }
            exception is FirebaseAuthInvalidCredentialsException -> {
                context.getString(R.string.text_error_invalid_code)
            }

            exception is FirebaseTooManyRequestsException -> {
                context.getString(R.string.text_error_many_requests)
            }
            else -> {
                val message = exception.localizedMessage ?: "Bilinmeten bri hata"
                context.getString(R.string.text_error_unknown, message)
            }
        }
    }

}
