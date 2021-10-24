package com.example.mplayer.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mplayer.R
import com.example.mplayer.databinding.FragmentPlaylistBinding
import com.example.mplayer.databinding.FragmentTracksBinding

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(layoutInflater)

        return binding.root
    }
    fun getBinding()=binding

}