package com.talhakilic.ustadloginmobileapp.ui.fragment


import android.content.res.ColorStateList
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.talhakilic.ustadloginmobileapp.databinding.FragmentCompanySelectionBinding
import com.talhakilic.ustadloginmobileapp.ui.viewmodel.LoginViewModel
import kotlin.getValue
import com.talhakilic.ustadloginmobileapp.R


class CompanySelectionFragment : Fragment() {

    private var _binding: FragmentCompanySelectionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanySelectionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.companies.observe(viewLifecycleOwner) { companyList ->
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, companyList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.companyDropdown.setAdapter(adapter)
        }

        binding.companyDropdown.setOnItemClickListener { _, _, position, _ ->
            val selected = viewModel.companies.value?.get(position)
            viewModel.selectCompany(selected)
        }

        viewModel.selectedCompany.observe(viewLifecycleOwner) { selected ->
            val isEnabled = !selected.isNullOrEmpty()
            binding.continueButton.isEnabled = isEnabled
            binding.continueButton.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(),
                    if (isEnabled) R.color.orange_active else R.color.orange_disabled
                )
            )
        }

        binding.continueButton.setOnClickListener {
            viewModel.selectedCompany.value?.let { company ->
                val action = CompanySelectionFragmentDirections.actionCompanySelectionFragmentToHomePageFragment(company)
                findNavController().navigate(action)
            }
        }

        binding.logoutButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}