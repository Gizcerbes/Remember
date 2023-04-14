package com.uogames.remembercards.di

import com.uogames.remembercards.ui.card.cardFragment.CardFragment
import com.uogames.remembercards.ui.card.choiceCardFragment.ChoiceCardFragment
import com.uogames.remembercards.ui.phrase.choicePhraseFragment.ChoicePhraseFragment
import com.uogames.remembercards.ui.cropFragment.CropFragment
import com.uogames.remembercards.ui.card.editCardFragment.EditCardFragment
import com.uogames.remembercards.ui.module.editModuleFragment.EditModuleFragment
import com.uogames.remembercards.ui.phrase.editPhraseFragment.EditPhraseFragment
import com.uogames.remembercards.ui.games.gameYesOrNo.GameYesOrNotFragment
import com.uogames.remembercards.ui.games.gameYesOrNo.ResultYesOrNotFragment
import com.uogames.remembercards.ui.games.notification.NotificationWorkerFragment
import com.uogames.remembercards.ui.games.watchCard.WatchCardFragment
import com.uogames.remembercards.ui.gamesFragment.GamesFragment
import com.uogames.remembercards.ui.module.library.LibraryFragment
import com.uogames.remembercards.ui.mainNav.MainNaviFragment
import com.uogames.remembercards.ui.module.watch.WatchModuleFragment
import com.uogames.remembercards.ui.personFragment.PersonFragment
import com.uogames.remembercards.ui.phrase.phrasesFragment.PhraseFragment
import com.uogames.remembercards.ui.registerFragment.RegisterFragment
import com.uogames.remembercards.ui.reportFragment.ReportFragment
import com.uogames.remembercards.ui.rootFragment.RootFragment
import com.uogames.remembercards.ui.settingFragment.SettingFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract fun contributeRootFragment(): RootFragment

    @ContributesAndroidInjector
    abstract fun contributeNavigationFragment(): MainNaviFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributePhraseFragment(): PhraseFragment

    @ContributesAndroidInjector
    abstract fun contributeLibraryFragment(): LibraryFragment

    @ContributesAndroidInjector
    abstract fun contributePersonFragment(): PersonFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingFragment(): SettingFragment

    @ContributesAndroidInjector
    abstract fun contributeAddCardDialog(): EditPhraseFragment

    @ContributesAndroidInjector
    abstract fun contributeGameFragment(): GamesFragment

    @ContributesAndroidInjector
    abstract fun contributeGameYesOrNotFragment(): GameYesOrNotFragment

    @ContributesAndroidInjector
    abstract fun contributeYesOrNotResultFragment(): ResultYesOrNotFragment

    @ContributesAndroidInjector
    abstract fun contributeCropFragment(): CropFragment

    @ContributesAndroidInjector
    abstract fun contributeCardFragment(): CardFragment

    @ContributesAndroidInjector
    abstract fun contributeEditCardFragment(): EditCardFragment

    @ContributesAndroidInjector
    abstract fun contributeChoicePhraseFragment(): ChoicePhraseFragment

    @ContributesAndroidInjector
    abstract fun contributeEditModuleFragment(): EditModuleFragment

    @ContributesAndroidInjector
    abstract fun contributeChoiceCardFragment(): ChoiceCardFragment

    @ContributesAndroidInjector
    abstract fun contributeReportFragment():ReportFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchCardFragment(): WatchCardFragment

    @ContributesAndroidInjector
    abstract fun contributeWatchModuleFragment(): WatchModuleFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationWorkerFragment(): NotificationWorkerFragment
}
