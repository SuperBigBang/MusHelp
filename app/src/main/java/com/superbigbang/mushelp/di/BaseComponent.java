package com.superbigbang.mushelp.di;

import android.content.Context;

import com.superbigbang.mushelp.di.modules.ContextModule;
import com.superbigbang.mushelp.di.modules.StarbuzzDatabaseHelperModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, StarbuzzDatabaseHelperModule.class})
public interface BaseComponent {
    Context getContext();

    StarbuzzDatabaseHelperModule getStarbuzzDatabaseHelperModule();

    // void inject(DrinkActivityPresenter drinkActivityPresenter);
}
