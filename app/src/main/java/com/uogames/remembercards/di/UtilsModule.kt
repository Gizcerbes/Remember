package com.uogames.remembercards.di

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class UtilsModule {

	@Provides
	fun provideContext(app: Application): Context = app.applicationContext



}