package com.uogames.remembercards.di

import android.app.Application
import com.uogames.remembercards.App
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Singleton


@Component(
    modules = [
        AndroidInjectionModule::class,
        ActivityBuilderModule::class,
        FragmentBuilderModule::class,
        UtilsModule::class
    ]
)
@Singleton
interface AppComponent : AndroidInjector<App> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun bindApplication(app: Application): Builder

        fun build(): AppComponent

    }


}