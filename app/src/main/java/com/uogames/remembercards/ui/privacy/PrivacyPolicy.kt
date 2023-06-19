package com.uogames.remembercards.ui.privacy

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentPrivacyPolicyBinding

class PrivacyPolicy : Fragment() {

    private var _bind: FragmentPrivacyPolicyBinding? = null
    private val bind get() = _bind!!

    private val tListener = createTabSelectedListener()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentPrivacyPolicyBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.tlPrivacyPolicy.addOnTabSelectedListener(tListener)
        bind.tvPolicy.text = requireContext().assets.open(requireContext().getString(R.string.file_policy)).bufferedReader().let {
            val txt = it.readText()
            it.close()
            txt
        }
        bind.tvTerms.text = requireContext().assets.open(requireContext().getString(R.string.file_terms)).bufferedReader().let {
            val txt = it.readText()
            it.close()
            txt
        }
        bind.tvPolicy.movementMethod = LinkMovementMethod.getInstance()
        bind.tvTerms.movementMethod = LinkMovementMethod.getInstance()

        bind.btnBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun createTabSelectedListener() = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            val position = tab?.position
            bind.tvPolicy.visibility = if (position == 0) View.VISIBLE else View.GONE
            bind.tvTerms.visibility = if (position == 1) View.VISIBLE else View.GONE
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {
        }

        override fun onTabReselected(tab: TabLayout.Tab?) {
        }
    }


    override fun onDestroyView() {
        bind.tlPrivacyPolicy.removeOnTabSelectedListener(tListener)
        super.onDestroyView()
    }


}