package com.uogames.remembercards.di

import com.uogames.remembercards.ui.bookFragment.BookFragment
import com.uogames.remembercards.ui.mainNav.MainNaviFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {


    @ContributesAndroidInjector
    abstract fun contributeNavigationFragment(): MainNaviFragment

    @ContributesAndroidInjector
    abstract fun contributeBookFragment(): BookFragment

}