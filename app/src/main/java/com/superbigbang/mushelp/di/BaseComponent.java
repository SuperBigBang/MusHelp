package com.superbigbang.mushelp.di;

import android.content.Context;

import com.superbigbang.mushelp.di.modules.ContextModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class})
public interface BaseComponent {
        Context getContext();
}
