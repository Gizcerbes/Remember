package com.uogames.remembercards.ui.phrase.phrasesFragment

import android.content.Context
import android.view.ViewGroup
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.uogames.dto.global.GlobalPhraseView
import com.uogames.dto.local.LocalPhraseView
import com.uogames.remembercards.R
import com.uogames.remembercards.models.GlobalPhraseModel
import com.uogames.remembercards.models.LocalPhraseModel
import com.uogames.remembercards.ui.dialogs.ShareAttentionDialog
import com.uogames.remembercards.ui.views.PhraseView
import com.uogames.remembercards.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.util.*

class PhraseAdapter(
	private val vm: Model
) : ClosableAdapter() {

	interface Model {

		val size: Flow<Int>

		suspend fun getLocal(position: Int): LocalPhraseModel?

		suspend fun getGlobal(position: Int): GlobalPhraseModel?

		fun getPicasso(context: Context): Picasso

		fun onEdit(v: LocalPhraseView)

		fun getShareAction(v: LocalPhraseView): Flow<Boolean>

		fun isChangedFlow(v: LocalPhraseView): Flow<Boolean?>

		suspend fun isChanged(v: LocalPhraseView): Boolean

		fun isUploadNoticed(): Boolean

		fun setUploadNotice(b: Boolean)

		fun setUpload(v: LocalPhraseView)

		fun onReportAction(v: GlobalPhraseView)

		fun onSave(v: GlobalPhraseView)

		fun isCloud(): Boolean

		fun isExistsFlow(v: GlobalPhraseView): Flow<Boolean>

	}

	private val recyclerScope = CoroutineScope(Dispatchers.Main)
	private val auth = Firebase.auth
	private var size = 0

	init {
		vm.size.observe(recyclerScope) {
			size = it
			notifyDataSetChanged()
		}
	}

	inner class LocalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

		override fun show() {
			view.reset()
			observer = recyclerScope.safeLaunch {
				val phraseView = vm.getLocal(adapterPosition).ifNull { return@safeLaunch }
				val phrase = phraseView.phrase
				view.phrase = phrase.phrase
				view.definition = phrase.definition.orEmpty()
				phrase.image?.imgUri?.let { uri ->
					vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
					view.showImage = true
				}.ifNull { view.showImage = false }
				phrase.pronounce?.audioUri?.let {
					view.setOnClickButtonSound { v ->
						launch { phraseView.playPhrase(v.background.asAnimationDrawable()) }
					}
				}.ifNull { view.setOnClickButtonSound(false, null) }
				view.language = Locale.forLanguageTag(phraseView.phrase.lang)
				// view.setOnClickListenerButtonEdit { editCall?.let { it1 -> it1(phrase.id) } }
				view.setOnClickListenerButtonEdit { vm.onEdit(phrase) }

				setShareAction(phrase)

				vm.getShareAction(phrase).observeLaunching(this) {
					runCatching {
						view.showProgressLoading = it
						view.showButtonShare = !it && isAvailableToShare(phrase, vm.isChanged(phrase))
						view.showButtonEdit = !it
					}
				}
				vm.isChangedFlow(phrase).observeLaunching(this) {
					runCatching { view.showButtonShare = isAvailableToShare(phrase, it == true) }
				}
			}
		}

		private fun isAvailableToShare(phrase: LocalPhraseView, changed: Boolean): Boolean {
			if (!changed) return false
			if (auth.currentUser == null) return false
			if (phrase.globalOwner != null && phrase.globalOwner != auth.currentUser?.uid) return false
			return true
		}

		private fun setShareAction(phrase: LocalPhraseView) {
			if (!isAvailableToShare(phrase, phrase.changed)) return
			view.setOnClickButtonShare {
				if (vm.isUploadNoticed()) {
					vm.setUpload(phrase)
				} else {
					ShareAttentionDialog.show(itemView.context) {
						vm.setUpload(phrase)
						it.ifTrue { vm.setUploadNotice(false) }
					}
				}
			}
		}

		override fun onDestroy() {
			super.onDestroy()
			view.reset()
		}

	}

	inner class GlobalPhraseHolder(val view: PhraseView) : ClosableHolder(view) {

		override fun show() {
			view.reset()
			observer = recyclerScope.launch {
				val phraseView = vm.getGlobal(adapterPosition).ifNull { return@launch }
				val phrase = phraseView.phrase
				view.phrase = phrase.phrase
				view.definition = phrase.definition.orEmpty()

				phrase.image?.imageUri?.let { uri ->
					vm.getPicasso(itemView.context).load(uri).placeholder(R.drawable.noise).into(view.getImageView())
					view.showImage = true
				}.ifNull { view.showImage = false }
				phrase.pronounce?.let {
					view.setOnClickButtonSound { v ->
						launch { phraseView.playPhrase(v.background.asAnimationDrawable()) }
					}
				}.ifNull { view.setOnClickButtonSound(false, null) }
				view.language = Locale.forLanguageTag(phrase.lang)

				view.setOnClickButtonReport(auth.currentUser != null) { vm.onReportAction(phrase) }

				view.setOnClickButtonDownload {
					vm.onSave(phrase)
				}

				vm.isExistsFlow(phrase).observe(this) { view.showButtonDownload = !it }
			}
		}

		override fun onDestroy() {
			super.onDestroy()
			view.reset()
		}

	}

	override fun getItemViewType(position: Int): Int {
		return if (vm.isCloud()) 1 else 0
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClosableHolder {
		return when (viewType) {
			0 -> LocalPhraseHolder(PhraseView(parent.context))
			else -> GlobalPhraseHolder(PhraseView(parent.context))
		}
	}

	override fun onBindViewHolder(holder: ClosableHolder, position: Int) {
		holder.show()
	}

	override fun getItemCount() = size

	override fun onViewRecycled(holder: ClosableHolder) {
		super.onViewRecycled(holder)
		holder.onDestroy()
	}

	override fun close() {
		recyclerScope.cancel()
	}


}