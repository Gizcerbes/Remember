package com.uogames.remembercards.di

import com.uogames.remembercards.ui.bookFragment.AddCardDialog
import com.uogames.remembercards.ui.bookFragment.BookFragment
import com.uogames.remembercards.ui.bookFragment.BookRecyclerFragment
import com.uogames.remembercards.ui.gameYesOrNo.GameYesOrNotFragment
import com.uogames.remembercards.ui.gamesFragment.GamesFragment
import com.uogames.remembercards.ui.libraryFragment.LibraryFragment
import com.uogames.remembercards.ui.mainNav.MainNaviFragment
import com.uogames.remembercards.ui.personFragment.PersonFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {


    @ContributesAndroidInjector
    abstract fun contributeNavigationFragment(): MainNaviFragment

    @ContributesAndroidInjector
    abstract fun contributeBookFragment(): BookFragment

    @ContributesAndroidInjector
    abstract fun contributeBookRecyclerFragment(): BookRecyclerFragment

    @ContributesAndroidInjector
    abstract fun contributeLibraryFragment(): LibraryFragment

    @ContributesAndroidInjector
    abstract fun contributePersonFragment(): PersonFragment

    @ContributesAndroidInjector
    abstract fun contributeAddCardDialog(): AddCardDialog

    @ContributesAndroidInjector
    abstract fun contributeGameFragment(): GamesFragment

    @ContributesAndroidInjector
    abstract fun contributeGameYesOrNotFragment(): GameYesOrNotFragment


}