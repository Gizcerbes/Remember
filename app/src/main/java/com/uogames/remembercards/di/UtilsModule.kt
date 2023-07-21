package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import androidx.work.WorkManager
import com.google.firebase.auth.FirebaseAuth
import com.uogames.remembercards.ui.card.cardFragment.CardFragment
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.ui.card.cardFragment.CardViewModel
import com.uogames.remembercards.ui.card.choiceCardFragment.ChoiceCardFragment
import com.uogames.remembercards.ui.card.choiceCardFragment.ChoiceCardViewModel
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.ui.card.editCardFragment.EditCardViewModel
import com.uogames.remembercards.ui.module.editModuleFragment.EditModuleViewModel
import com.uogames.remembercards.ui.phrase.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.ui.games.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.games.notification.NotificationViewModel
import com.uogames.remembercards.ui.games.watchCard.WatchCardViewModel
import com.uogames.remembercards.ui.gamesFragment.GamesViewModel
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
import com.uogames.remembercards.ui.module.choiceModuleDialog.ChoiceModuleViewModel
import com.uogames.remembercards.ui.module.watch.WatchModuleViewModel
import com.uogames.remembercards.ui.phrase.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.phrase.choicePhraseFragment.ChoicePhraseViewModel
import com.uogames.remembercards.ui.phrase.phrasesFragment.PhraseFragment
import com.uogames.remembercards.ui.phrase.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.ui.registerFragment.RegisterViewModel
import com.uogames.remembercards.ui.reportFragment.ReportViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.viewmodel.CViewModel
import com.uogames.remembercards.viewmodel.MViewModel
import com.uogames.remembercards.viewmodel.PViewModel
import com.uogames.repository.DataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

	@Provides
	fun provideContext(app: Application): Context = app.applicationContext

	@Singleton
	@Provides
	fun provideAuth(gvm: GlobalViewModel): FirebaseAuth = gvm.auth

	@Provides
	@Singleton
	fun provideDataProvider(gvm: GlobalViewModel): DataProvider = gvm.provider

	@Provides
	@Singleton
	fun provideRegisterViewModel(): RegisterViewModel = RegisterViewModel()

	@Provides
	@Singleton
	fun provideNavigationViewModel(): NavigationViewModel = NavigationViewModel()

	@Provides
	@Singleton
	fun provideGlobalViewModel(context: Context): GlobalViewModel = GlobalViewModel(context)

	@Provides
	@Singleton
	fun providePViewModel(
		model: GlobalViewModel,
		player: ObservableMediaPlayer
	): PViewModel = PViewModel(model, player)

	@Provides
	@Singleton
	fun provideCViewModel(
		globalViewModel: GlobalViewModel,
		player: ObservableMediaPlayer
	) = CViewModel(globalViewModel, player)

	@Provides
	@Singleton
	fun provideMViewModel(
		globalViewModel: GlobalViewModel,
		player: ObservableMediaPlayer
	) = MViewModel(globalViewModel, player)

	@Provides
	@Singleton
	fun providePhraseViewModel2(
		gvm: GlobalViewModel,
		player: ObservableMediaPlayer
	): PhraseViewModel = PhraseViewModel(gvm, player)

	@Provides
	fun providePhraseModel(m: PhraseViewModel): PhraseFragment.Model = m

	@Provides
	@Singleton
	fun provideChoicePhraseViewModel2(
		gvm: GlobalViewModel,
		player: ObservableMediaPlayer
	): ChoicePhraseViewModel = ChoicePhraseViewModel(gvm, player)

	@Provides
	fun provideChoicePhraseModel(m: ChoicePhraseViewModel) : ChoicePhraseFragment.Model = m

	@Provides
	@Singleton
	fun provideAddPhraseViewModel(
		provider: DataProvider,
		player: ObservableMediaPlayer
	): EditPhraseViewModel = EditPhraseViewModel(provider, player)

	@Provides
	@Singleton
	fun provideGameYesOrNotViewModel(
		globalViewModel: GlobalViewModel,
		player: ObservableMediaPlayer
	): GameYesOrNotViewModel = GameYesOrNotViewModel(globalViewModel, player)

	@Provides
	@Singleton
	fun provideCropViewModel(): CropViewModel = CropViewModel()

	@Provides
	@Singleton
	fun provideEditCardViewModel(
		provider: DataProvider,
		player: ObservableMediaPlayer
	): EditCardViewModel = EditCardViewModel(provider, player)


	@Provides
	@Singleton
	fun provideCardViewModelV2(
		model: GlobalViewModel,
		player: ObservableMediaPlayer
	): CardViewModel = CardViewModel(model, player)

	@Provides
	fun provideCardModel(cvm: CardViewModel): CardFragment.Model = cvm

	@Provides
	@Singleton
	fun provideChoiceCardViewModel2(
		gvm: GlobalViewModel,
		player: ObservableMediaPlayer
	): ChoiceCardViewModel = ChoiceCardViewModel(gvm, player)

	@Provides
	fun provideChoiceCardModel(m: ChoiceCardViewModel): ChoiceCardFragment.Model = m

	@Provides
	@Singleton
	fun provideMediaPlayer(): ObservableMediaPlayer = ObservableMediaPlayer(MediaPlayer())

	@Provides
	@Singleton
	fun provideLibraryViewModel(
		model: MViewModel
	): LibraryViewModel = LibraryViewModel(model)

	@Provides
	@Singleton
	fun provideEditModuleViewModel(
		globalViewModel: GlobalViewModel,
		player: ObservableMediaPlayer
	): EditModuleViewModel = EditModuleViewModel(globalViewModel, player)

	@Provides
	@Singleton
	fun provideGameViewModel(provider: DataProvider): GamesViewModel = GamesViewModel(provider)

	@Provides
	@Singleton
	fun provideReportViewModel(provider: DataProvider): ReportViewModel = ReportViewModel(provider)

	@Provides
	@Singleton
	fun provideWatchCardViewModel(
		globalViewModel: GlobalViewModel
	): WatchCardViewModel = WatchCardViewModel(globalViewModel)

	@Provides
	@Singleton
	fun provideChoiceModuleViewModel(
		model: MViewModel
	): ChoiceModuleViewModel = ChoiceModuleViewModel(model)

	@Provides
	@Singleton
	fun provideWatchModuleViewModel(
		model: MViewModel
	) = WatchModuleViewModel(model)

	@Provides
	@Singleton
	fun provideNotificationViewModel(
		globalViewModel: GlobalViewModel
	) = NotificationViewModel(globalViewModel)

	@Provides
	@Singleton
	fun provideWorkManager(
		context: Context
	): WorkManager {
		return WorkManager.getInstance(context)
	}


}
