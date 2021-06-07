package com.example.anabada.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anabada.databinding.FragmentTempBinding
import com.example.anabada.util.BaseViewBindingFragment
import com.example.anabada.databinding.LayoutLoadingBinding

class ChatFragment: BaseViewBindingFragment<FragmentTempBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentTempBinding {
        return FragmentTempBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isAdded) {
            return
        }
    }

}