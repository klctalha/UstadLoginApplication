package com.talhakilic.ustadloginmobileapp.ui.fragment


import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.talhakilic.ustadloginmobileapp.R
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.talhakilic.ustadloginmobileapp.databinding.FragmentOtpVerificationBinding
import com.talhakilic.ustadloginmobileapp.ui.viewmodel.LoginViewModel

import kotlin.getValue

class OtpVerificationFragment : Fragment() {

    private var _binding: FragmentOtpVerificationBinding? = null
    private val binding get() = _binding!!

    private val loginViewModel: LoginViewModel by activityViewModels()
    private var countDownTimer: CountDownTimer? = null
    private val totalTimeMillis = 90_000L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startCountdown()

        binding.verifyButton.setOnClickListener {
            val code = binding.otpEditText.text.toString().trim()
            if (code.length == 6 && code.all { it.isDigit() }) {
                binding.otpErrorTextView.visibility = View.GONE
                loginViewModel.verifyCode(requireContext(),code)
            }
            else {
                showOtpErrorUI(getString(R.string.text_error_invalid_otp))
            }
        }

        binding.resendCodeText.setOnClickListener {
            val phone = loginViewModel.countryCode.value + loginViewModel.nationalPhoneFormatted.value?.replace(" ", "")
            loginViewModel.sendVerificationCode(requireActivity(), phone ?: "")
            startCountdown()
            binding.verifyButton.isEnabled = true
            resetOtpFieldColors()
        }

        loginViewModel.showCompanySelectionScreen.observe(viewLifecycleOwner) { navigate ->
            if (navigate == true) {
                loginViewModel.clearNavigationFlags()
                findNavController().navigate(OtpVerificationFragmentDirections.actionOtpVerificationFragmentToCompanySelectionFragment())
            }
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            if (error.isNullOrEmpty()) {
                resetOtpFieldColors()
            } else {
                showOtpErrorUI(error)
            }
        }

        loginViewModel.countryCode.observe(viewLifecycleOwner) { code ->
            val number = loginViewModel.nationalPhoneFormatted.value ?: ""
            val message = getString(R.string.text_otp_instruction, code, number)
            binding.phoneInfoTextView.text = message
        }

        binding.otpEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val code = s.toString().trim()
                if (code.length == 6 && code.all { it.isDigit() }) {
                    binding.verifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_active))
                    binding.verifyButton.isEnabled = true
                    resetOtpFieldColors()
                }
                else {
                    binding.verifyButton.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.orange_disabled))
                    binding.verifyButton.isEnabled = false
                }
            }
        })
    }

    private fun startCountdown() {
        countDownTimer?.cancel()
        binding.verifyButton.isEnabled = true

        countDownTimer = object : CountDownTimer(totalTimeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val totalSeconds = millisUntilFinished / 1000
                val formattedTime = "${totalSeconds / 60}:${(totalSeconds % 60).toString().padStart(2, '0')}"
                binding.countdownTextView.text = formattedTime
            }

            override fun onFinish() {
                binding.countdownTextView.text = "00:00"
                binding.verifyButton.isEnabled = false
            }
        }.start()
    }

    private fun showOtpErrorUI(message: String) {
        binding.otpErrorTextView.visibility = View.VISIBLE
        binding.otpErrorTextView.text = message
        binding.otpEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.error_red))
        binding.otpEditText.setLineColor(ContextCompat.getColor(requireContext(), R.color.error_red))
    }

    private fun resetOtpFieldColors() {
        binding.otpEditText.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
        binding.otpEditText.setLineColor(ContextCompat.getColor(requireContext(), R.color.error_red))
        binding.otpErrorTextView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}
