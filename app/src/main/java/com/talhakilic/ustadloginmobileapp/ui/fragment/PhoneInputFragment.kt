package com.talhakilic.ustadloginmobileapp.ui.fragment


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.InputFilter
import android.text.InputType
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.talhakilic.ustadloginmobileapp.R
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import com.talhakilic.ustadloginmobileapp.databinding.FragmentPhoneInputBinding
import com.talhakilic.ustadloginmobileapp.ui.viewmodel.LoginViewModel
import com.talhakilic.ustadloginmobileapp.utils.Validation


class PhoneInputFragment : Fragment() {

    private var _binding: FragmentPhoneInputBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginViewModel: LoginViewModel

    private val countryCodes = mapOf(
        "🇹🇷 +90" to "+90",
        "🇺🇸 +1" to "+1",
        "🇪🇸 +34" to "+34",
        "🇬🇧 +44" to "+44",
        "🇩🇪 +49" to "+49",
        "🇫🇷 +33" to "+33",
        "🇮🇹 +39" to "+39",

        "🇷🇺 +7" to "+7",
        "🇯🇵 +81" to "+81"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPhoneInputBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loginViewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]

        updateStepIndicator(0)

        binding.countryCodeEditText.setText("+")

        binding.countryCodeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                val match = countryCodes.entries.find { it.value == input }
                if (match != null) {
                    binding.countryCodeEditText.setText(match.key)
                    binding.phoneEditText.requestFocus()
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.countryCodeEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_dropdown_item_1line,
                    countryCodes.keys.toList()
                )
                AutoCompleteTextView(requireContext()).apply {
                    setAdapter(adapter)
                    showDropDown()
                }
            }
        }

        binding.sendCodeButton.setOnClickListener {
            sendVerification()
        }

        observeViewModel()

    }

    private fun sendVerification() {
        clearErrors()

        val countryCodeInput = binding.countryCodeEditText.text.toString()
        val countryCode = countryCodes[countryCodeInput] ?: countryCodeInput
        val rawPhone = binding.phoneEditText.text.toString().filter { it.isDigit() }

        if (rawPhone.length != 10) {
            showCustomError(getString(R.string.text_error_phone_length))
            return
        }

        val fullPhoneNumber = countryCode + rawPhone
        if (!Validation.isStrictlyValidPhoneNumber(fullPhoneNumber)) {
            showCustomError(getString(R.string.text_error_invalid_phone))
            return
        }

        binding.errorTextView.visibility = View.GONE
        loginViewModel.sendVerificationCode(requireActivity(), fullPhoneNumber)
    }

    private fun observeViewModel() {
        loginViewModel.showOtpScreen.observe(viewLifecycleOwner) {
            if (it == true) {
                loginViewModel.clearNavigationFlags()
                findNavController().navigate(PhoneInputFragmentDirections.actionPhoneInputFragmentToOtpVerificationFragment())
            }
        }

        loginViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let{showCustomError(it)}
        }
    }

    private fun showCustomError(message: String) {
        binding.phoneInputLayout.error = null
        binding.countryCodeInputLayout.error = null
        binding.errorTextView.text = message
        binding.errorTextView.visibility = View.VISIBLE
    }

    private fun clearErrors() {
        binding.phoneInputLayout.error = null
        binding.countryCodeInputLayout.error = null
        binding.errorTextView.visibility = View.GONE
    }

    private fun updateStepIndicator(currentStep: Int) {
        val steps = listOf(binding.step1, binding.step2, binding.step3)

        steps.forEachIndexed { index, view ->
            view.setBackgroundResource(
                when {
                    index < currentStep -> R.drawable.bg_step_green
                    index == currentStep -> R.drawable.bg_step_orange
                    else -> R.drawable.bg_step_grey
                }
            )
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}