package com.uogames.remembercards.ui.personFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.uogames.flags.Countries
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.databinding.FragmentPersonBinding
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

class PersonFragment : DaggerFragment() {

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    private var _bind: FragmentPersonBinding? = null
    private val bind get() = _bind!!

    private var countPhrasesObserver: Job? = null
    private var countCardsObserver: Job? = null
    private var countModulesObserver: Job? = null
    private var gameYesOrNotCountObserver: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_bind == null) _bind = FragmentPersonBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            bind.txtPersonName.text = globalViewModel.getUserName().first().orEmpty()
            globalViewModel.getUserNativeCountry().first()?.let {
                bind.imgFlag.setImageResource(Countries.valueOf(it).res)
            }
        }

        countPhrasesObserver = globalViewModel.getCountPhrases().observeWhenStarted(lifecycleScope) {
            bind.txtPhrasesCount.text = it.toString()
        }

        countCardsObserver = globalViewModel.getCountCards().observeWhenStarted(lifecycleScope) {
            bind.txtCardsCount.text = it.toString()
        }

        countModulesObserver = globalViewModel.getCountModules().observeWhenStarted(lifecycleScope) {
            bind.txtModulesCount.text = it.toString()
        }

        gameYesOrNotCountObserver = globalViewModel.getGameYesOrNotGameCount().observeWhenStarted(lifecycleScope) {
            bind.txtYesOrNoCount.text = it.ifNullOrEmpty { "0" }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        countPhrasesObserver?.cancel()
        countCardsObserver?.cancel()
        countModulesObserver?.cancel()
        gameYesOrNotCountObserver?.cancel()
        _bind = null
    }

}