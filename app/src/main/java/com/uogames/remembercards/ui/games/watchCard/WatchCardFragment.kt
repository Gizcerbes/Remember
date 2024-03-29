package com.uogames.remembercards.ui.games.watchCard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.R
import com.uogames.remembercards.databinding.FragmentGameWarchCardBinding
import com.uogames.remembercards.ui.animation.Animations
import com.uogames.remembercards.utils.*
import dagger.android.support.DaggerFragment
import kotlinx.coroutines.Job
import java.util.*
import javax.inject.Inject

class WatchCardFragment : DaggerFragment() {

    companion object {
        const val MODULE_ID = "WatchCardFragment_MODULE_ID"
    }

    @Inject
    lateinit var model: WatchCardViewModel

    @Inject
    lateinit var globalViewModel: GlobalViewModel

    @Inject
    lateinit var player: ObservableMediaPlayer

    private var _bind: FragmentGameWarchCardBinding? = null
    private val bind get() = _bind!!

    private var observer: Job? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _bind = FragmentGameWarchCardBinding.inflate(inflater, container, false)
        return _bind?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        globalViewModel.shouldReset.ifTrue { model.reset() }

        model.moduleID.value = arguments?.getInt(MODULE_ID)

        model.update()

        val scale = requireContext().resources.displayMetrics.density
        val cameraDist = 8000 * scale
        bind.phraseView.cameraDistance = cameraDist

        bind.btnClose.setOnClickListener { findNavController().popBackStack() }

        bind.btnRevert.setOnClickListener { revert() }

        bind.btnPrevious.setOnClickListener { previousSlide() }

        bind.btnNext.setOnClickListener { nextSlide() }

        observer = lifecycleScope.launchWhenStarted {
            model.card.observe(this) { card ->
                bind.tvClue.text = card?.reason.orEmpty()
            }
            model.phrase.observe(this) { phrase ->
                phrase?.let {
                    bind.phraseView.phrase = phrase.phrase
                    bind.phraseView.definition = phrase.definition.orEmpty()
                    phrase.image?.imgUri?.let { uri ->
                        model.getPicasso(requireContext()).load(uri).placeholder(R.drawable.noise).into(bind.phraseView.getImageView())
                        bind.phraseView.showImage = true
                    }.ifNull { bind.phraseView.showImage = false }
                    phrase.pronounce?.audioUri?.let { uri ->
                        bind.phraseView.setOnClickButtonSound {
                            player.play(requireContext(), uri.toUri(), it.background.asAnimationDrawable())
                        }
                    }.ifNull { bind.phraseView.setOnClickButtonSound(false, null) }
                    bind.phraseView.language = Locale.forLanguageTag(phrase.lang)
                }
            }
            model.position.observe(this) {
                bind.tvCount.text = "$it/${model.count.value}"
            }
            model.count.observe(this) {
                bind.tvCount.text = "${model.position.value}/$it"
            }
        }

    }

    private fun nextSlide() {
        if (model.position.value == model.count.value) return
        Animations.toRightAndFromLeft(bind.phraseView){ model.next() }
    }

    private fun previousSlide() {
        if (model.position.value == 1) return
        Animations.toLeftAndFromRight(bind.phraseView){ model.previous()}
    }

    private fun revert() {
        Animations.rotate360(bind.ivRevert)
        Animations.flipOver(bind.phraseView){ model.backSize.setOpposite() }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        observer?.cancel()
        player.stop()
    }

}