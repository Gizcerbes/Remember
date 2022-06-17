package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import com.uogames.remembercards.GlobalViewModel
import com.uogames.remembercards.ui.addPhraseFragment.AddPhraseViewModel
import com.uogames.remembercards.ui.bookFragment.BookViewModel
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotViewModel
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
import com.uogames.remembercards.ui.selectCountry.SelectCountryViewModel
import com.uogames.repository.DataProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class UtilsModule {

	@Provides
	fun provideContext(app: Application): Context = app.applicationContext

	@Provides
	@Singleton
	fun provideNavigationViewModel(): NavigationViewModel = NavigationViewModel()

	@Provides
	@Singleton
	fun provideGlobalViewModel(): GlobalViewModel = GlobalViewModel()

	@Provides
	@Singleton
	fun provideBookViewModel(context: Context): BookViewModel =
		BookViewModel(DataProvider.get(context))

	@Provides
	@Singleton
	fun provideAddPhraseViewModel(context: Context): AddPhraseViewModel =
		AddPhraseViewModel(DataProvider.get(context))

	@Provides
	@Singleton
	fun provideGameYesOrNotViewModel(context: Context): GameYesOrNotViewModel =
		GameYesOrNotViewModel(DataProvider.get(context))

	@Provides
	@Singleton
	fun provideSelectCountryViewModel(): SelectCountryViewModel =
		SelectCountryViewModel()


}