package com.superbigbang.mushelp;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.google.android.gms.ads.MobileAds;
import com.superbigbang.mushelp.di.BaseComponent;
import com.superbigbang.mushelp.di.DaggerBaseComponent;
import com.superbigbang.mushelp.di.modules.ContextModule;
import com.superbigbang.mushelp.model.SetList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


public class ExtendApplication extends Application {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FIRST_INSTALL_FLAG = "FIRST_INSTALL_FLAG";

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
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);


        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        sBaseComponent = DaggerBaseComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        Realm.init(sBaseComponent.getContext());

        if (!mSettings.contains(APP_PREFERENCES_FIRST_INSTALL_FLAG)) {
            MyInitialDataRealmTransaction();
        }

        MobileAds.initialize(this, "ca-app-pub-5364969751338385~1161013636");

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_INSTALL_FLAG, false).apply();
    }

    void MyInitialDataRealmTransaction() {
        RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                .name("setlistsrealm.realm")
                .build();

        RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                .name("songsrealm.realm")
                .build();

        Realm setlistsrealm = Realm.getInstance(setListsRealmConfig);
        //    Realm songsrealm = Realm.getInstance(songsRealmConfig);

        setlistsrealm.beginTransaction();
        SetList setList1 = setlistsrealm.createObject(SetList.class); // Create a new object
        setList1.setId(0);
        setList1.setName("Hell Rain");
        setList1.setPosition(0);
        setList1.setOpen(true);
        SetList setList2 = setlistsrealm.createObject(SetList.class);
        setList2.setId(1);
        setList2.setName("Amon Amarth");
        setList2.setPosition(1);
        for (int i = 2; i < 10; i++) {
            SetList setList = setlistsrealm.createObject(SetList.class);
            setList.setId(i);
            setList.setName(getResources().getString(R.string.SetListNameDefault) + (i + 1));
            setList.setPosition(i);
        }
        setlistsrealm.commitTransaction();
        setlistsrealm.close();
    }
}