package com.talhakilic.ustadloginmobileapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.talhakilic.ustadloginmobileapp.R
import androidx.navigation.fragment.navArgs
import com.talhakilic.ustadloginmobileapp.databinding.FragmentHomePageBinding
import kotlin.getValue



class HomePageFragment : Fragment() {

    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    private val args: HomePageFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val companyName = args.companyName
        val companyNameLogoRes = when (companyName) {
            "Ustad" -> R.drawable.ustad_logo
            "Turkcell" -> R.drawable.turkcell_logo
            "Aselsan" -> R.drawable.aselsan_logo
            else -> R.drawable.ustad_logo
        }
        binding.companyNameImage.setImageResource(companyNameLogoRes)

        val companyDescription = when (companyName) {
            "Ustad" -> getString(R.string.desc_ustad)
            "Turkcell" -> getString(R.string.desc_turkcell)
            "Aselsan" -> getString(R.string.desc_aselsan)
            else -> "!!!" }

        binding.textDescription.text = companyDescription

        binding.exitButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}