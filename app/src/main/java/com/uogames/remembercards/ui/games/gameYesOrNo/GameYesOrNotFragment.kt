package com.uogames.remembercards.ui.games.gameYesOrNo

import android.content.Context
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentYesOrNotGameBinding
import com.uogames.remembercards.utils.*
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

    private var moduleId: Int? = null

    private var reactionJob: Job? = null

    private var allAnswerObserver: Job? = null
    private var timeObserver: Job? = null
    private var startObserver: Job? = null

    private var observers: Job? = null

    private val goodReactions = listOf(R.drawable.ic_good_vibes, R.drawable.ic_super, R.drawable.ic_wow)
    private val badReactions = listOf(R.drawable.ic_crash, R.drawable.ic_boom, R.drawable.ic_wtf)
    private val iconsBtnStart = listOf(R.drawable.ic_baseline_pause_circle_outline_24, R.drawable.ic_baseline_play_circle_outline_24)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentYesOrNotGameBinding.inflate(inflater, container, false)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        globalViewModel.shouldReset.ifTrue { gameModel.reset() }

        moduleId = arguments?.getInt(MODULE_ID)

        gameModel.module.value = moduleId

        gameModel.newAnswer()


        bind.btnClose.setOnClickListener { findNavController().popBackStack() }

        bind.btnPause.setOnClickListener { play(!gameModel.isStarted.value) }

        bind.btnYes.setOnClickListener { gameModel.answerCard.value?.let { it1 -> gameModel.check(it1,true)?.let { reaction(it) } } }

        bind.btnNo.setOnClickListener { gameModel.answerCard.value?.let { it1 -> gameModel.check(it1,false)?.let { reaction(it) } } }

        play(true)

        bind.cvInfo.reset()

        observers = lifecycleScope.launchWhenStarted {
            gameModel.allAnswers.observe(this) {
                val thrAns = gameModel.trueAnswers.value
                bind.txtCountAnswers.text = "$thrAns/$it"
                try {
                    bind.answersProgress.progress = thrAns * 100 / it
                } catch (ex: Exception) {
                    bind.answersProgress.progress = 0
                }
            }
            gameModel.time.observe(this) {
                bind.txtTime.text = "${it / 1000}s"
                bind.timeProgress.progress = it * 100 / GameYesOrNotViewModel.MAX_TIME
            }
            gameModel.isStarted.observe(this) {
                bind.imgBtnPause.setImageResource(iconsBtnStart[if (it) 0 else 1])
                bind.btnYes.visibility = if (it) View.VISIBLE else View.GONE
                bind.btnNo.visibility = if (it) View.VISIBLE else View.GONE
                if (it) requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
                else requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            }
            gameModel.answerCard.observe(this) { it?.let { it1 -> setData(it1) } }

        }

    }

    private fun setData(card: GameYesOrNotViewModel.AnswerCards2) {
        val view = bind.cvInfo
        val cardView = card.first
        view.clue = cardView.card.reason
        cardView.card.phrase.let { phrase ->
            view.languageTagFirst = Locale.forLanguageTag(phrase.lang)
            view.phraseFirst = phrase.phrase
            phrase.image?.let { image ->
                Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getFirstImageView())
                view.showImageFirst = true
            }.ifNull { view.showImageFirst = false }
            phrase.pronounce?.let {
                view.showAudioFirst = true
                view.setOnClickButtonCardFirst {
                    lifecycleScope.launch { cardView.playPhrase(it.background.asAnimationDrawable()) }
                }
            }.ifNull { view.showAudioFirst = false }
            view.definitionFirst = phrase.definition.orEmpty()
        }
        val cardView2 = card.second
        cardView2.card.translate.let { translate ->
            view.languageTagSecond = Locale.forLanguageTag(translate.lang)
            view.phraseSecond = translate.phrase
            translate.image?.let { image ->
                view.showImageSecond = true
                Picasso.get().load(image.imgUri.toUri()).placeholder(R.drawable.noise).into(view.getSecondImageView())
            }.ifNull { view.showImageSecond = false }
            translate.pronounce?.let {
                view.showAudioSecond = true
                view.setOnClickButtonCardSecond {
                    lifecycleScope.launch { cardView2.playTranslate(it.background.asAnimationDrawable()) }
                }
            }.ifNull { view.showAudioSecond = false }
            view.definitionSecond = translate.definition.orEmpty()
        }
        view.showButtons = false
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

    private fun play(boolean: Boolean) {
        if (boolean) {
            gameModel.start {
                if (gameModel.allAnswers.value == 0) findNavController().popBackStack()
                else requireActivity().findNavController(R.id.nav_host_fragment).navigate(
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
        observers?.cancel()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        _bind = null
    }
}
