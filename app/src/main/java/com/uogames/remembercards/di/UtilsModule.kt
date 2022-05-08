package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import com.uogames.remembercards.ui.mainNav.NavigationViewModel
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

}