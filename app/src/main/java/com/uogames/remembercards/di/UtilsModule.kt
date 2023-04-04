package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.google.firebase.auth.FirebaseAuth
import com.uogames.remembercards.viewmodel.GlobalViewModel
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.choiceCardFragment.ChoiceCardViewModel
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseViewModel
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardViewModel
import com.uogames.remembercards.ui.editModuleFragment.EditModuleViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.ui.games.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.games.watchCard.WatchCardViewModel
import com.uogames.remembercards.ui.gamesFragment.GamesViewModel
import com.uogames.remembercards.ui.module.library.LibraryViewModel
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
import com.uogames.remembercards.ui.module.watch.WatchModuleViewModel
import com.uogames.remembercards.ui.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.ui.registerFragment.RegisterViewModel
import com.uogames.remembercards.ui.reportFragment.ReportViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.remembercards.viewmodel.MViewModel
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
    fun providePhraseViewModel(
        globalViewModel: GlobalViewModel,
        player: ObservableMediaPlayer
    ): PhraseViewModel = PhraseViewModel(globalViewModel, player)

    @Provides
    @Singleton
    fun provideChoicePhraseViewModel(
        provider: DataProvider,
        player: ObservableMediaPlayer
    ): ChoicePhraseViewModel = ChoicePhraseViewModel(provider, player)

    @Provides
    @Singleton
    fun provideAddPhraseViewModel(
        provider: DataProvider,
        player: ObservableMediaPlayer
    ): EditPhraseViewModel = EditPhraseViewModel(provider, player)

    @Provides
    @Singleton
    fun provideGameYesOrNotViewModel(provider: DataProvider): GameYesOrNotViewModel = GameYesOrNotViewModel(provider)

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
    fun provideCardViewModel(
        globalViewModel: GlobalViewModel,
        player: ObservableMediaPlayer
    ): CardViewModel = CardViewModel(globalViewModel, player)

    @Provides
    @Singleton
    fun provideChoiceCardViewModel(
        globalViewModel: GlobalViewModel,
        player: ObservableMediaPlayer
    ): ChoiceCardViewModel = ChoiceCardViewModel(globalViewModel, player)

    @Provides
    @Singleton
    fun provideMediaPlayer(): ObservableMediaPlayer = ObservableMediaPlayer(MediaPlayer())

    @Provides
    @Singleton
    fun provideLibraryViewModel(
        globalViewModel: GlobalViewModel
    ): LibraryViewModel = LibraryViewModel(globalViewModel)

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
    fun provideMViewModel(
        globalViewModel: GlobalViewModel,
        player: ObservableMediaPlayer
    ) = MViewModel(globalViewModel, player)

    @Provides
    @Singleton
    fun provideWatchModuleViewModel(
        model: MViewModel
    ) = WatchModuleViewModel(model)


}
