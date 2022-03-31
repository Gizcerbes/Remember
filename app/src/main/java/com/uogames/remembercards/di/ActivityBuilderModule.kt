package com.uogames.remembercards.di

import com.uogames.remembercards.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

	@ContributesAndroidInjector
	abstract fun contributeMainActivity(): MainActivity
}