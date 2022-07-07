package com.uogames.remembercards.ui.gameYesOrNo

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.*
import android.util.Log
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
import androidx.navigation.navOptions
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Phrase
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentYesOrNotGameBinding
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.utils.asAnimationDrawable
import com.uogames.remembercards.utils.ifNull
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class GameYesOrNotFragment : DaggerFragment() {

	companion object{
		const val MODULE_ID = "GameYesOrNotFragment_MODULE_ID"
	}

	@Inject
	lateinit var gameModel: GameYesOrNotViewModel

	@Inject
	lateinit var player: ObservableMediaPlayer

	private lateinit var bind: FragmentYesOrNotGameBinding

	private var reactionJob: Job? = null

	private val goodReactions =
		listOf(R.drawable.ic_good_vibes, R.drawable.ic_super, R.drawable.ic_wow)
	private val badReactions = listOf(R.drawable.ic_crash, R.drawable.ic_boom, R.drawable.ic_wtf)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		bind = FragmentYesOrNotGameBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		val id = arguments?.getInt(MODULE_ID)

		gameModel.reset()

		gameModel.module.value = id

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
			bind.txtTime.text = "${it / 1000}s"
			bind.timeProgress.progress = it * 100 / GameYesOrNotViewModel.MAX_TIME
		}.launchIn(lifecycleScope)
		gameModel.getRandomAnswerCard { setData(it) }

		bind.btnClose.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).popBackStack()
		}

		bind.btnPause.setOnClickListener { play(!gameModel.isStarted.value) }

		gameModel.isStarted.onEach {

			bind.imgBtnPause.setImageResource(
				if (it) R.drawable.ic_baseline_pause_circle_outline_24
				else R.drawable.ic_baseline_play_circle_outline_24
			)
			bind.btnYes.visibility = if (it) View.VISIBLE else View.GONE
			bind.btnNo.visibility = if (it) View.VISIBLE else View.GONE

			if (it) requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
			else requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

		}.launchIn(lifecycleScope)
		play(true)
	}

	private fun setData(card: GameYesOrNotViewModel.AnswersCard) {
		lifecycleScope.launchWhenStarted {
			val firstCard = card.firs
			val firstPhrase = gameModel.getPhrase(firstCard.idPhrase).first().ifNull { return@launchWhenStarted }
			val firstTranslate = gameModel.getPhrase(firstCard.idTranslate).first().ifNull { return@launchWhenStarted }
			val secondCard = card.second
			val secondTranslate = gameModel.getPhrase(secondCard.idTranslate).first().ifNull { return@launchWhenStarted }

			bind.txtReason.text = firstCard.reason
			bind.txtPhraseFirst.text = firstPhrase.phrase
			bind.txtLangFirst.text = Locale.forLanguageTag(firstPhrase.lang?.split("_")?.get(0).ifNull { "eng" }).displayLanguage

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
		phrase.idPronounce?.let {
			soundImg.visibility = View.VISIBLE
			gameModel.getPronounce(phrase).first()?.let { audio ->
				button.setOnClickListener {
					playSound(soundImg, audio.audioUri.toUri())
				}
			}
		}.ifNull {
			soundImg.visibility = View.GONE
		}
	}

	private fun playSound(soundImg: ImageView, audioUri: Uri) {
		player.setStatListener {
			when (it) {
				ObservableMediaPlayer.Status.PLAY -> soundImg.background.asAnimationDrawable().start()
				else -> {
					soundImg.background.asAnimationDrawable().stop()
					soundImg.background.asAnimationDrawable().selectDrawable(0)
				}
			}
		}
		player.play(requireContext(), audioUri)
	}

	private fun showLang(phrase: Phrase): String {
		phrase.lang?.let {
			val data = it.split("-")
			if (data.isNotEmpty()) try {
				return Locale(data[0]).displayLanguage
			} catch (e: Exception) {
			}
		}
		return ""
	}

	private fun play(boolean: Boolean) {
		if (boolean) {
			gameModel.start {
				requireActivity().findNavController(R.id.nav_host_fragment).navigate(
					R.id.resultYesOrNotFragment,
					null, NavOptions.Builder().setPopUpTo(R.id.mainNaviFragment, false).build()
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

}