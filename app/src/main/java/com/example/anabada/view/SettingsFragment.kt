package com.example.anabada.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import com.example.anabada.databinding.FragmentSettingsBinding
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.anabada.R
import com.example.anabada.util.BaseViewBindingFragment

class SettingsFragment : BaseViewBindingFragment<FragmentSettingsBinding>() {
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSettingsBinding {
        return FragmentSettingsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
//        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                activity?.onBackPressed()
//            }
//        })
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getView()?.findNavController()?.navigate(R.id.action_settingsFragment_to_myPageFragment)
            }
        })
        if (isAdded) {
            return
        }

    }

    private fun initToolbar() {
        binding.appbarSettings.toolbarBack.colorFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
        binding.appbarSettings.toolbarBack.setOnClickListener {
            getView()?.findNavController()?.navigate(R.id.action_settingsFragment_to_myPageFragment)
        }
        binding.appbarSettings.toolbarTitle.text = "Settings"
    }
}