package com.uogames.remembercards.ui.gameYesOrNo

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.local.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentYesOrNotGameBinding
import com.uogames.remembercards.utils.*
import com.uogames.repository.DataProvider.Companion.toPhrase
import com.uogames.repository.DataProvider.Companion.toPronounce
import com.uogames.repository.DataProvider.Companion.toTranslate
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class GameYesOrNotFragment : DaggerFragment() {

    companion object {
        const val MODULE_ID = "GameYesOrNotFragment_MODULE_ID"
    }

    @Inject
    lateinit var gameModel: GameYesOrNotViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentYesOrNotGameBinding? = null
    private val bind get() = _bind!!

    private var reactionJob: Job? = null

    private var allAnswerObserver: Job? = null
    private var timeObserver: Job? = null
    private var startObserver: Job? = null

    private val goodReactions = listOf(R.drawable.ic_good_vibes, R.drawable.ic_super, R.drawable.ic_wow)
    private val badReactions = listOf(R.drawable.ic_crash, R.drawable.ic_boom, R.drawable.ic_wtf)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_bind == null) _bind = FragmentYesOrNotGameBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue {
            gameModel.reset()
        }

        val id = arguments?.getInt(MODULE_ID)

        gameModel.module.value = id

        allAnswerObserver = createAllAnswersObserver()

        timeObserver = createTimeObserver()

        startObserver = createStartObserver()

        gameModel.getRandomAnswerCard { setData(it) }

        bind.btnClose.setOnClickListener {
            requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
        }

        bind.btnPause.setOnClickListener { play(!gameModel.isStarted.value) }

        play(true)
    }

    private fun createAllAnswersObserver() = gameModel.allAnswers.observeWhenStarted(lifecycleScope) {
        val thrAns = gameModel.trueAnswers.value
        bind.txtCountAnswers.text = "$thrAns/$it"
        try {
            bind.answersProgress.progress = thrAns * 100 / it
        } catch (ex: Exception) {
            bind.answersProgress.progress = 0
        }
    }

    private fun createTimeObserver() = gameModel.time.observeWhenStarted(lifecycleScope) {
        bind.txtTime.text = "${it / 1000}s"
        bind.timeProgress.progress = it * 100 / GameYesOrNotViewModel.MAX_TIME
    }

    private fun createStartObserver() = gameModel.isStarted.observeWhenStarted(lifecycleScope) {
        bind.imgBtnPause.setImageResource(
            if (it) {
                R.drawable.ic_baseline_pause_circle_outline_24
            } else {
                R.drawable.ic_baseline_play_circle_outline_24
            }
        )
        bind.btnYes.visibility = if (it) View.VISIBLE else View.GONE
        bind.btnNo.visibility = if (it) View.VISIBLE else View.GONE

        if (it) {
            requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun setData(card: GameYesOrNotViewModel.AnswersCard) {
        lifecycleScope.launchWhenStarted {
            val firstCard = card.firs
            val firstPhrase = firstCard.toPhrase().ifNull { return@launchWhenStarted }
            val firstTranslate = firstCard.toTranslate().ifNull { return@launchWhenStarted }
            val secondCard = card.second
            val secondTranslate = secondCard.toTranslate().ifNull { return@launchWhenStarted }

            bind.txtReason.text = firstCard.reason

            setData(firstPhrase, bind.txtLangFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.mcvFirst)

            if ((Math.random() * Int.MAX_VALUE).toInt() % 2 == 0) {
                setData(firstTranslate, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond)
            } else {
                setData(secondTranslate, bind.txtLangSecond, bind.txtPhraseSecond, bind.imgSoundSecond, bind.mcvSecond)
            }

            bind.btnYes.setOnClickListener {
                card.truth = bind.txtPhraseSecond.text == firstTranslate.phrase
                gameModel.addAnswer(card)
                gameModel.getRandomAnswerCard { setData(it) }
                reaction(card.truth)
            }

            bind.btnNo.setOnClickListener {
                card.truth = bind.txtPhraseSecond.text == secondTranslate.phrase
                gameModel.addAnswer(card)
                gameModel.getRandomAnswerCard { setData(it) }
                reaction(card.truth)
            }
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

    private suspend fun setData(
        phrase: Phrase,
        langView: TextView,
        phraseView: TextView,
        soundImg: ImageView,
        button: MaterialCardView
    ) {
        langView.text = showLang(phrase)
        phraseView.text = phrase.phrase
        phrase.toPronounce()?.let { audio ->
            soundImg.visibility = View.VISIBLE
            button.setOnClickListener {
                player.play(
                    requireContext(),
                    audio.audioUri.toUri(),
                    soundImg.background.asAnimationDrawable()
                )
            }
        }.ifNull {
            soundImg.visibility = View.GONE
        }
    }

    private fun showLang(phrase: Phrase): String {
        return safely {
            val data = phrase.lang.split("-")
            Locale(data[0]).displayLanguage
        }.ifNull { "English" }
    }

    private fun play(boolean: Boolean) {
        if (boolean) {
            gameModel.start {
                requireActivity().findNavController(R.id.nav_host_fragment).navigate(
                    R.id.resultYesOrNotFragment,
                    null,
                    NavOptions.Builder().setPopUpTo(R.id.mainNaviFragment, false).build()
                )
            }
        } else {
            gameModel.stop()
        }
    }

    override fun onStop() {
        super.onStop()
        play(false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        allAnswerObserver?.cancel()
        timeObserver?.cancel()
        startObserver?.cancel()
        _bind = null
    }
}
