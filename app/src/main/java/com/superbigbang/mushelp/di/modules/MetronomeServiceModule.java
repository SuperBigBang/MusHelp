package com.superbigbang.mushelp.di.modules;

import com.superbigbang.mushelp.services.MetronomeService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class MetronomeServiceModule {
    private MetronomeService mMetronomeService;

    public MetronomeServiceModule(MetronomeService MetronomeService) {
        this.mMetronomeService = MetronomeService;
    }

    @Provides
    @Singleton
    public MetronomeService provideMetronomeService() {
        return mMetronomeService;
    }
}
