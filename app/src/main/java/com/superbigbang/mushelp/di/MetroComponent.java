package com.superbigbang.mushelp.di;

import com.superbigbang.mushelp.di.modules.MetronomeServiceModule;
import com.superbigbang.mushelp.services.MetronomeService;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {MetronomeServiceModule.class})
public interface MetroComponent {
    MetronomeService getMetronomeService();
}
