package com.example.anabada.view

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.anabada.R
import com.example.anabada.databinding.FragmentMypageBinding
import com.example.anabada.util.BaseViewBindingFragment

class MyPageFragment : BaseViewBindingFragment<FragmentMypageBinding>() {

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMypageBinding {
        return FragmentMypageBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initToolbar()
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getView()?.findNavController()?.navigate(R.id.action_myPageFragment_to_homeFragment)
            }
        })
        if (isAdded) {
            return
        }
    }

    private fun initToolbar() {
        binding.appbarMypage.toolbarTitle.text = "My Page"
        binding.appbarMypage.toolbarBack.visibility = View.INVISIBLE
        binding.appbarMypage.toolbarMenu.visibility = View.VISIBLE
        binding.appbarMypage.toolbarMenu.setOnClickListener {
            //navigate to settings fragment
            it.findNavController().navigate(R.id.action_myPageFragment_to_settingsFragment)
        }
    }
}