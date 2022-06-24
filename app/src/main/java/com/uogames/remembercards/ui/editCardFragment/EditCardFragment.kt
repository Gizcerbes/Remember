package com.uogames.remembercards.ui.editCardFragment

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.card.MaterialCardView
import com.uogames.dto.Phrase
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentEditCardBinding
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.utils.asAnimationDrawable
import com.uogames.remembercards.utils.ifNull
import com.uogames.remembercards.utils.ifNullOrEmpty
import com.uogames.remembercards.utils.observeWhenStarted
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class EditCardFragment : DaggerFragment() {

	companion object {
		private const val FIRST_PHRASE = "EditCardFragment_FIRST_PHRASE"
		private const val SECOND_PHRASE = "EditCardFragment_SECOND_PHRASE"
	}

	@Inject
	lateinit var editCardViewModel: EditCardViewModel

	@Inject
	lateinit var globalViewModel: GlobalViewModel

	private lateinit var bind: FragmentEditCardBinding

	private var player: MediaPlayer? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		bind = FragmentEditCardBinding.inflate(inflater, container, false)
		return bind.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		bind.btnBack.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnSave.setOnClickListener {
			findNavController().popBackStack()
		}

		bind.btnDelete.setOnClickListener {

		}

		bind.btnEditFist.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(
				R.id.choicePhraseDialog,
				Bundle().apply {
					putString(ChoicePhraseFragment.TAG, FIRST_PHRASE)
				}
			)
		}

		bind.btnEditSecond.setOnClickListener {
			requireActivity().findNavController(R.id.nav_host_fragment).navigate(
				R.id.choicePhraseDialog,
				Bundle().apply {
					putString(ChoicePhraseFragment.TAG, SECOND_PHRASE)
				}
			)
		}

		bind.btnEditImage.setOnClickListener {

		}

		bind.btnEditReason.setOnClickListener {

		}

		globalViewModel.getFlow(FIRST_PHRASE).observeWhenStarted(lifecycleScope) {
			it?.let {
				try {
					editCardViewModel.selectFirstPhrase(it.toInt())
					globalViewModel.saveData(FIRST_PHRASE, null)
				} catch (e: Exception) {
					Toast.makeText(requireContext(), "Error firs id", Toast.LENGTH_SHORT).show()
				}
			}
		}

		globalViewModel.getFlow(SECOND_PHRASE).observeWhenStarted(lifecycleScope) {
			it?.let {
				try {
					editCardViewModel.selectSecondPhrase(it.toInt())
					globalViewModel.saveData(SECOND_PHRASE, null)
				} catch (e: Exception) {
					Toast.makeText(requireContext(), "Error second id", Toast.LENGTH_SHORT).show()
				}
			}
		}

		editCardViewModel.firstPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				//bind.mcvFirst.visibility = View.VISIBLE
				setButtonData(phrase, bind.mcvFirst, bind.txtPhraseFirst, bind.imgSoundFirst, bind.txtLangFirst)
			}.ifNull {
				bind.imgSoundFirst.visibility = View.GONE
				bind.txtPhraseFirst.text = requireContext().getString(R.string.phrase)
				bind.txtLangFirst.text = Locale.getDefault().displayLanguage
			}
		}

		editCardViewModel.secondPhrase.observeWhenStarted(lifecycleScope) {
			it?.let { phrase ->
				setButtonData(phrase, bind.mcvSecond, bind.txtPhraseSecond, bind.imgSoundSecond,bind.txtLangSecond)
			}.ifNull {
				bind.imgSoundSecond.visibility = View.GONE
				bind.txtPhraseSecond.text = requireContext().getString(R.string.phrase)
				bind.txtLangSecond.text = Locale.getDefault().displayLanguage
			}
		}
	}

	private fun setButtonData(
		phrase: Phrase,
		container: MaterialCardView,
		txtPhrase: TextView,
		imgSound: ImageView,
		lang: TextView
	) {
		txtPhrase.text = phrase.phrase.ifNullOrEmpty {
			requireContext().getString(R.string.phrase)
		}
		if (phrase.idPronounce != null) {
			imgSound.visibility = View.VISIBLE
			Log.e("TAG", "setButtonData: ${phrase.lang}", )
			lang.text = showLang(phrase).orEmpty()
			container.setOnClickListener {
				lifecycleScope.launch(Dispatchers.IO) {
					val audio = editCardViewModel.getAudio(phrase).first()
					player?.stop()
					val player = MediaPlayer()
					player.setDataSource(requireContext(), audio)
					try {
						player.prepare()
					} catch (e: Exception) {
						Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
					}
					player.start()
					this@EditCardFragment.player = player
					launch(Dispatchers.Main) {
						imgSound.background.asAnimationDrawable().start()
						while (player.isPlaying) delay(100)
						imgSound.background.asAnimationDrawable().stop()
						imgSound.background.asAnimationDrawable().selectDrawable(0)
					}
				}
			}
		} else {
			container.setOnClickListener { player?.stop() }
			imgSound.visibility = View.GONE
		}
	}

	private fun showLang(phrase: Phrase): String? {
		return phrase.lang?.let {
			val data = it.split("-")
			if (data.isNotEmpty()) try {
				Locale(data[0]).displayLanguage
			} catch (e: Exception) {
				null
			} else null
		}
	}


}