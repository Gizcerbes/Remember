package com.uogames.remembercards.ui.gamesFragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentGamesBinding
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotViewModel
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class GamesFragment: DaggerFragment() {

    @Inject
    lateinit var gameYesOrNotViewModel: GameYesOrNotViewModel

    private lateinit var bind: FragmentGamesBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentGamesBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val nav = requireActivity().findNavController(R.id.nav_host_fragment)
        bind.gameYesOrNot.setOnClickListener {
            gameYesOrNotViewModel.reset()
            nav.navigate(R.id.gameYesOrNotFragment)
        }
    }


}