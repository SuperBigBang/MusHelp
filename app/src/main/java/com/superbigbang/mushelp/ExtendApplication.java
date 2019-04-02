package com.superbigbang.mushelp;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.superbigbang.mushelp.di.BaseComponent;
import com.superbigbang.mushelp.di.DaggerBaseComponent;
import com.superbigbang.mushelp.di.DaggerMetroComponent;
import com.superbigbang.mushelp.di.MetroComponent;
import com.superbigbang.mushelp.di.modules.ContextModule;
import com.superbigbang.mushelp.di.modules.MetronomeServiceModule;
import com.superbigbang.mushelp.model.SetList;
import com.superbigbang.mushelp.model.Songs;
import com.superbigbang.mushelp.services.MetronomeService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import timber.log.Timber;


public class ExtendApplication extends Application implements ServiceConnection {

    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_FIRST_INSTALL_FLAG = "FIRST_INSTALL_FLAG";
    public static boolean isBound;
    private static BaseComponent sBaseComponent;
    private static MetroComponent sMetroComponent;
    public static int currentThemeColorsTextSelected = Color.parseColor("#FFFFFFFF");
    public static int currentThemeColorsTextUNSelected = Color.parseColor("#66bfff");
    public static int currentThemeColorsUnavailable = Color.parseColor("#4DFFFFFF");
    private static boolean isFull;

    public static boolean isIsFull() {
        return isFull;
    }

    public static void setIsFull(boolean isFull) {
        ExtendApplication.isFull = isFull;
    }

    public static BaseComponent getBaseComponent() {
        return sBaseComponent;
    }

    @VisibleForTesting
    public static void setBaseComponent(@NonNull BaseComponent baseComponent) {
        sBaseComponent = baseComponent;
    }

    public static Animation getAnimFadein() {
        return AnimationUtils.loadAnimation(ExtendApplication.getBaseComponent().getContext(), R.anim.fade_in);
    }

    public static MetroComponent getMetroComponent() {
        return sMetroComponent;
    }

    @VisibleForTesting
    public static void setMetroComponent(@NonNull MetroComponent metroComponent) {
        sMetroComponent = metroComponent;
    }

    public static boolean isBound() {
        return isBound;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains("Premium")) {
            setIsFull(mSettings.getBoolean("Premium", false));
        }

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        Intent intent = new Intent(this, MetronomeService.class);
        MetronomeService.enqueueWork(this, intent);
        bindService(intent, this, Context.BIND_AUTO_CREATE);

        sBaseComponent = DaggerBaseComponent.builder()
                .contextModule(new ContextModule(this))
                .build();

        Realm.init(sBaseComponent.getContext());

        if (!mSettings.contains(APP_PREFERENCES_FIRST_INSTALL_FLAG)) {
            MyInitialDataRealmTransaction();
        }

  /*      if (!isIsFull()) {
            MobileAds.initialize(this, "ca-app-pub-5364969751338385~1161013636");
        }*/

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(APP_PREFERENCES_FIRST_INSTALL_FLAG, false).apply();
    }

    @Override
    public void onTerminate() {
        if (isBound) {
            unbindService(this);
            isBound = false;
        }
        super.onTerminate();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        MetronomeService.LocalBinder binder = (MetronomeService.LocalBinder) iBinder;
        MetronomeService service = binder.getService();
        isBound = true;
        sMetroComponent = DaggerMetroComponent.builder()
                .metronomeServiceModule(new MetronomeServiceModule(service))
                .build();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        isBound = false;
    }

    public String getStringResourceByName(String aString) {
        String packageName = getPackageName();
        int resId = getResources().getIdentifier(aString, "string", packageName);
        return getString(resId);
    }

    void MyInitialDataRealmTransaction() {
        RealmConfiguration setListsRealmConfig = new RealmConfiguration.Builder()
                .name("setlistsrealm.realm")
                .build();

        RealmConfiguration songsRealmConfig = new RealmConfiguration.Builder()
                .name("songsrealm.realm")
                .build();

        Realm setlistsrealm = Realm.getInstance(setListsRealmConfig);
        Realm songsrealm = Realm.getInstance(songsRealmConfig);

        setlistsrealm.beginTransaction();
        SetList setList1 = setlistsrealm.createObject(SetList.class); // Create a new object
        setList1.setId(0);
        setList1.setName(getStringResourceByName("SetListNameDefault") + 1);
        setList1.setPosition(0);
        setList1.setOpen(true);

        for (int i = 1; i < 20; i++) {
            SetList setList = setlistsrealm.createObject(SetList.class);
            setList.setId(i);
            setList.setName(getStringResourceByName("SetListNameDefault") + (i + 1));
            setList.setPosition(i);
        }
        setlistsrealm.commitTransaction();
        setlistsrealm.close();

        songsrealm.beginTransaction();

        Songs song8 = songsrealm.createObject(Songs.class);
        song8.setTitle(getStringResourceByName("Hint10"));
        song8.setSetlistid(0);
        song8.setSongid(0);
        song8.setPosition(0);
        song8.setMetronombpm(80);
        song8.setAudioOn(false);
        song8.setLyrics(getStringResourceByName("EmptyLyrics"));
        song8.setLyricshasopen(false);
        song8.setPlaystarted(false);

        songsrealm.commitTransaction();
        songsrealm.close();
    }
}