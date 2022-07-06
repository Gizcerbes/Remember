package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import android.media.MediaPlayer
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseViewModel
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.cardFragment.CardViewModel
import com.uogames.remembercards.ui.cropFragment.CropViewModel
import com.uogames.remembercards.ui.editCardFragment.EditCardViewModel
import com.uogames.remembercards.ui.editModuleFragment.EditModuleViewModel
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.libraryFragment.LibraryViewModel
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
import com.uogames.remembercards.ui.registerFragment.RegisterViewModel
import com.uogames.remembercards.ui.choiceCountry.SelectCountryViewModel
import com.uogames.remembercards.ui.gamesFragment.GamesViewModel
import com.uogames.remembercards.utils.ObservableMediaPlayer
import com.uogames.repository.DataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

	@Provides
	fun provideContext(app: Application): Context = app.applicationContext

	@Provides
	fun provideDataProvider(context: Context): DataProvider = DataProvider.get(context)

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
	fun provideBookViewModel(provider: DataProvider): BookViewModel = BookViewModel(provider)

	@Provides
	@Singleton
	fun provideAddPhraseViewModel(provider: DataProvider): EditPhraseViewModel = EditPhraseViewModel(provider)

	@Provides
	@Singleton
	fun provideGameYesOrNotViewModel(provider: DataProvider): GameYesOrNotViewModel = GameYesOrNotViewModel(provider)

	@Provides
	@Singleton
	fun provideSelectCountryViewModel(): SelectCountryViewModel = SelectCountryViewModel()

	@Provides
	@Singleton
	fun provideCropViewModel(): CropViewModel = CropViewModel()

	@Provides
	@Singleton
	fun provideEditCardViewModel(provider: DataProvider): EditCardViewModel = EditCardViewModel(provider)

	@Provides
	@Singleton
	fun provideCardViewModel(provider: DataProvider): CardViewModel = CardViewModel(provider)

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

}