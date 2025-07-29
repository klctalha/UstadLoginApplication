package com.talhakilic.ustadloginmobileapp.data.repository

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import kotlin.text.replace
import kotlin.text.toRegex


class UserRepository(private val context: Context) {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendVerificationCode(
        activity: Activity,
        phoneNumber: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val cleanedPhoneNumber = phoneNumber.replace("[^+\\d]".toRegex(), "")

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            }
            override fun onVerificationFailed(e: FirebaseException) {
                onFailure(e)
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                Log.d("FIREBASE", "${context.getString(com.talhakilic.ustadloginmobileapp.R.string.text_log_code_sent)}: $verificationId")
                onSuccess(verificationId)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(cleanedPhoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtpCode(verificationId: String, code: String, onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                }
                else {
                    val errorMessage = context.getString(com.talhakilic.ustadloginmobileapp.R.string.text_code_verification_error)
                    onError(task.exception ?: Exception(errorMessage))
                }
            }
    }
}
