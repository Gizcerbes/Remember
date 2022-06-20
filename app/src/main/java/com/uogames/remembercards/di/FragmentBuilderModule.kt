package com.uogames.remembercards.di

import com.uogames.remembercards.ui.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.bookFragment.BookFragment
import com.uogames.remembercards.ui.cardFragment.CardFragment
import com.uogames.remembercards.ui.choiceLanguageDialog.ChoiceLanguageDialog
import com.uogames.remembercards.ui.cropFragment.CropFragment
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotFragment
import com.uogames.remembercards.ui.gameYesOrNo.ResultYesOrNotFragment
import com.uogames.remembercards.ui.gamesFragment.GamesFragment
import com.uogames.remembercards.ui.libraryFragment.LibraryFragment
import com.uogames.remembercards.ui.mainNav.MainNaviFragment
import com.uogames.remembercards.ui.personFragment.PersonFragment
import com.uogames.remembercards.ui.selectCountry.SelectCountryFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {


	@ContributesAndroidInjector
	abstract fun contributeNavigationFragment(): MainNaviFragment

	@ContributesAndroidInjector
	abstract fun contributeBookFragment(): BookFragment

	@ContributesAndroidInjector
	abstract fun contributeLibraryFragment(): LibraryFragment

	@ContributesAndroidInjector
	abstract fun contributePersonFragment(): PersonFragment

	@ContributesAndroidInjector
	abstract fun contributeAddCardDialog(): EditPhraseFragment

	@ContributesAndroidInjector
	abstract fun contributeGameFragment(): GamesFragment

	@ContributesAndroidInjector
	abstract fun contributeGameYesOrNotFragment(): GameYesOrNotFragment

	@ContributesAndroidInjector
	abstract fun contributeYesOrNotResultFragment(): ResultYesOrNotFragment

	@ContributesAndroidInjector
	abstract fun contributeSelectCountryFragment(): SelectCountryFragment

	@ContributesAndroidInjector
	abstract fun contributeCropFragment(): CropFragment

	@ContributesAndroidInjector
	abstract fun contributeCardFragment(): CardFragment


}