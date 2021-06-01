package com.example.anabada.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.anabada.BaseViewBindingFragment
import com.example.anabada.databinding.LayoutLoadingBinding

class ChatFragment: BaseViewBindingFragment<LayoutLoadingBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): LayoutLoadingBinding {
        return LayoutLoadingBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        if (isAdded) {
            return
        }
    }

}