package com.superbigbang.mushelp;

import android.app.Application;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.superbigbang.mushelp.di.BaseComponent;

import timber.log.Timber;

public class ExtendApplication extends Application {

    private static BaseComponent sBaseComponent;

    public static BaseComponent getBaseComponent() {
        return sBaseComponent;
    }

    @VisibleForTesting
    public static void setBaseComponent(@NonNull BaseComponent baseComponent) {
        sBaseComponent = baseComponent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

     /*   sBaseComponent = DaggerBaseComponent.builder()
                .contextModule(new ContextModule(this))
                .starbuzzDatabaseHelperModule(new StarbuzzDatabaseHelperModule(this))
                .build();
*/
    }
}