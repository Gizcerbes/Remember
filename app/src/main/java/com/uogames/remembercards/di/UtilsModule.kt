package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.uogames.remembercards.BuildConfig
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.choiceCardFragment.ChoiceCardViewModel
import com.uogames.remembercards.ui.choicePhraseFragment.ChoicePhraseViewModel
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardViewModel
import com.uogames.remembercards.ui.editModuleFragment.EditModuleViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.gamesFragment.GamesViewModel
import com.uogames.remembercards.ui.libraryFragment.LibraryViewModel
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
import com.uogames.remembercards.ui.phrasesFragment.PhraseViewModel
import com.uogames.remembercards.ui.registerFragment.RegisterViewModel
import com.uogames.remembercards.ui.reportFragment.ReportViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
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
    fun provideAuth(): FirebaseAuth = Firebase.auth

    @Provides
    @Singleton
    fun provideDataProvider(context: Context, auth: FirebaseAuth): DataProvider = DataProvider
        .get(
            context,
            {
                if (BuildConfig.DEBUG) "secret"
                else "It doesn't matter"
            },
            {
                mapOf(
                    "Identifier" to (auth.currentUser?.displayName ?: ""),
                    "User UID" to (auth.currentUser?.uid ?: ""),
                    "UTM" to System.currentTimeMillis().toString()
                )
            }
        )

    @Provides
    @Singleton
    fun provideRegisterViewModel(): RegisterViewModel = RegisterViewModel()

    @Provides
    @Singleton
    fun provideNavigationViewModel(): NavigationViewModel = NavigationViewModel()

    @Provides
    @Singleton
    fun provideGlobalViewModel(provider: DataProvider): GlobalViewModel = GlobalViewModel(provider)

    @Provides
    @Singleton
    fun providePhraseViewModel(provider: DataProvider, player: ObservableMediaPlayer): PhraseViewModel = PhraseViewModel(provider, player)

    @Provides
    @Singleton
    fun provideChoicePhraseViewModel(provider: DataProvider) : ChoicePhraseViewModel = ChoicePhraseViewModel(provider)

    @Provides
    @Singleton
    fun provideAddPhraseViewModel(provider: DataProvider, player: ObservableMediaPlayer): EditPhraseViewModel = EditPhraseViewModel(provider, player)

    @Provides
    @Singleton
    fun provideGameYesOrNotViewModel(provider: DataProvider): GameYesOrNotViewModel = GameYesOrNotViewModel(provider)

    @Provides
    @Singleton
    fun provideCropViewModel(): CropViewModel = CropViewModel()

    @Provides
    @Singleton
    fun provideEditCardViewModel(provider: DataProvider, player: ObservableMediaPlayer): EditCardViewModel = EditCardViewModel(provider, player)

    @Provides
    @Singleton
    fun provideCardViewModel(provider: DataProvider, player: ObservableMediaPlayer): CardViewModel = CardViewModel(provider, player)

    @Provides
    @Singleton
    fun provideChoiceCardViewModel(provider: DataProvider) : ChoiceCardViewModel = ChoiceCardViewModel(provider)

    @Provides
    @Singleton
    fun provideMediaPlayer(): ObservableMediaPlayer = ObservableMediaPlayer(MediaPlayer())

    @Provides
    @Singleton
    fun provideLibraryViewModel(provider: DataProvider): LibraryViewModel = LibraryViewModel(provider)

    @Provides
    @Singleton
    fun provideEditModuleViewModel(provider: DataProvider): EditModuleViewModel = EditModuleViewModel(provider)

    @Provides
    @Singleton
    fun provideGameViewModel(provider: DataProvider): GamesViewModel = GamesViewModel(provider)

    @Provides
    @Singleton
    fun provideReportViewModel(provider: DataProvider): ReportViewModel = ReportViewModel(provider)
}
