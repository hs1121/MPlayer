package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mplayer.R
import com.example.mplayer.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private lateinit var binding:FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding= FragmentSettingsBinding.inflate(layoutInflater)


        return binding.root
    }


}