package com.uogames.remembercards.ui.gameYesOrNo

import android.annotation.SuppressLint
import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentYesOrNotGameBinding
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

class GameYesOrNotFragment : DaggerFragment() {

    @Inject
    lateinit var gameModel: GameYesOrNotViewModel

    private lateinit var bind: FragmentYesOrNotGameBinding

    private var reactionJob: Job? = null

    private val goodReactions = listOf(R.drawable.ic_good_vibes, R.drawable.ic_super, R.drawable.ic_wow)
    private val badReactions = listOf(R.drawable.ic_crash, R.drawable.ic_boom, R.drawable.ic_wtf)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bind = FragmentYesOrNotGameBinding.inflate(inflater, container, false)
        return bind.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        gameModel.allAnswers.onEach {
            val thrAns = gameModel.trueAnswers.value
            bind.txtCountAnswers.text = "$thrAns/$it"
            try {
                bind.answersProgress.progress = thrAns * 100 / it
            } catch (ex: Exception) {
                bind.answersProgress.progress = 0
            }

        }.launchIn(lifecycleScope)

        gameModel.time.onEach {
            bind.txtTime.text = "${it}s"
            bind.timeProgress.progress = 100 - it * 100 / GameYesOrNotViewModel.MAX_TIME
        }.launchIn(lifecycleScope)
        gameModel.getRandomAnswerCard { setData(it) }


    }

    private fun setData(card: GameYesOrNotViewModel.AnswersCard) {
        bind.txtPhrase.text = card.firs.phrase
        if ((Math.random() * Int.MAX_VALUE).toInt() % 2 == 0) {
            bind.txtTranslate.text = card.firs.translate
        } else {
            bind.txtTranslate.text = card.second.translate
        }
        bind.btnYes.setOnClickListener {
            card.truth = bind.txtTranslate.text == card.firs.translate
            gameModel.setAnswer(card)
            gameModel.getRandomAnswerCard { setData(it) }
            reaction(card.truth)
        }
        bind.btnNo.setOnClickListener {
            card.truth = bind.txtTranslate.text == card.second.translate
            gameModel.setAnswer(card)
            gameModel.getRandomAnswerCard { setData(it) }
            reaction(card.truth)
        }
    }

    private fun reaction(boolean: Boolean) {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (requireActivity().getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        if (boolean) {
            bind.reaction.setImageResource(R.drawable.ic_good_vibes)
        } else {
            bind.reaction.setImageResource(R.drawable.ic_crash)
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 200), -1))
        }
        setReactionImage(boolean)
    }

    private fun setReactionImage(boolean: Boolean) {
        reactionJob?.cancel()
        reactionJob = lifecycleScope.launch {
        bind.reaction.visibility = View.VISIBLE
        if (boolean) {
            bind.reaction.setImageResource(goodReactions[(Math.random() * goodReactions.size).toInt()])
        } else {
            bind.reaction.setImageResource(badReactions[(Math.random() * badReactions.size).toInt()])
        }
        launch(Dispatchers.IO) {
            delay(1000)
            lifecycleScope.launch {
                bind.reaction.visibility = View.GONE
            }
        }
    }

}

override fun onStart() {
    super.onStart()
    gameModel.start {
        requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
    }
}

override fun onStop() {
    super.onStop()
    gameModel.stop()
}

}