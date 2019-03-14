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

import com.google.android.gms.ads.MobileAds;
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
    private static Animation animFadein;

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
        animFadein = AnimationUtils.loadAnimation(ExtendApplication.getBaseComponent().getContext(), R.anim.fade_in);
        return animFadein;
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

        if (!isIsFull()) {
            MobileAds.initialize(this, "ca-app-pub-5364969751338385~1161013636");
        }

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
        setList1.setName(getResources().getString(R.string.SetListNameDefault) + 1);
        setList1.setPosition(0);
        setList1.setOpen(true);

        for (int i = 1; i < 20; i++) {
            SetList setList = setlistsrealm.createObject(SetList.class);
            setList.setId(i);
            setList.setName(getResources().getString(R.string.SetListNameDefault) + (i + 1));
            setList.setPosition(i);
        }
        setlistsrealm.commitTransaction();
        setlistsrealm.close();

        songsrealm.beginTransaction();
     /*   Songs song1 = songsrealm.createObject(Songs.class);
        song1.setTitle("Предатель");
        song1.setSetlistid(0);
        song1.setSongid(0);
        song1.setPosition(0);
        song1.setMetronombpm(145);
        song1.setAudioOn(false);
        song1.setLyrics("Текст песни Предатель");
        song1.setLyricshasopen(false);
        song1.setPlaystarted(false);

        Songs song2 = songsrealm.createObject(Songs.class);
        song2.setTitle("Проклятая жизнь");
        song2.setSetlistid(0);
        song2.setSongid(1);
        song2.setPosition(1);
        song2.setMetronombpm(115);
        song2.setAudioOn(false);
        song2.setLyrics("Текст песни Проклятая жизнь");
        song2.setLyricshasopen(false);
        song2.setPlaystarted(false);

        Songs song3 = songsrealm.createObject(Songs.class);
        song3.setTitle("Война");
        song3.setSetlistid(0);
        song3.setSongid(2);
        song3.setPosition(2);
        song3.setMetronombpm(130);
        song3.setAudioOn(false);
        song3.setLyrics("Текст песни Война");
        song3.setLyricshasopen(false);
        song3.setPlaystarted(false);

        Songs song4 = songsrealm.createObject(Songs.class);
        song4.setTitle("Мёртвый город");
        song4.setSetlistid(0);
        song4.setSongid(3);
        song4.setPosition(3);
        song4.setMetronombpm(170);
        song4.setAudioOn(false);
        song4.setLyrics("Текст песни Мёртвый город");
        song4.setLyricshasopen(false);
        song4.setPlaystarted(false);

        Songs song5 = songsrealm.createObject(Songs.class);
        song5.setTitle("Сон");
        song5.setSetlistid(0);
        song5.setSongid(4);
        song5.setPosition(4);
        song5.setMetronombpm(130);
        song5.setAudioOn(false);
        song5.setLyrics("Текст песни Сон");
        song5.setLyricshasopen(false);
        song5.setPlaystarted(false);

        Songs song6 = songsrealm.createObject(Songs.class);
        song6.setTitle("Клетка");
        song6.setSetlistid(0);
        song6.setSongid(5);
        song6.setPosition(5);
        song6.setMetronombpm(150);
        song6.setAudioOn(false);
        song6.setLyrics("Текст песни Клетка");
        song6.setLyricshasopen(false);
        song6.setPlaystarted(false);

        Songs song7 = songsrealm.createObject(Songs.class);
        song7.setTitle("Молчание вечного крика");
        song7.setSetlistid(0);
        song7.setSongid(6);
        song7.setPosition(6);
        song7.setMetronombpm(165);
        song7.setAudioOn(false);
        song7.setLyrics("Текст песни Молчание вечного крика");
        song7.setLyricshasopen(false);
        song7.setPlaystarted(false);*/

        Songs song8 = songsrealm.createObject(Songs.class);
        song8.setTitle(getResources().getString(R.string.Hint10));
        song8.setSetlistid(0);
        song8.setSongid(0);
        song8.setPosition(0);
        song8.setMetronombpm(80);
        song8.setAudioOn(false);
        song8.setLyrics(getResources().getString(R.string.EmptyLyrics));
        song8.setLyricshasopen(false);
        song8.setPlaystarted(false);

     /*   Songs song9 = songsrealm.createObject(Songs.class);
        song9.setTitle("Воздаяние");
        song9.setSetlistid(0);
        song9.setSongid(8);
        song9.setPosition(8);
        song9.setMetronombpm(120);
        song9.setAudioOn(false);
        song9.setLyrics("Текст песни Воздаяние");
        song9.setLyricshasopen(false);
        song9.setPlaystarted(false);

        Songs song10 = songsrealm.createObject(Songs.class);
        song10.setTitle("Судный день");
        song10.setSetlistid(0);
        song10.setSongid(9);
        song10.setPosition(9);
        song10.setMetronombpm(140);
        song10.setAudioOn(false);
        song10.setLyrics("Текст песни Судный день");
        song10.setLyricshasopen(false);
        song10.setPlaystarted(false);

        Songs song11 = songsrealm.createObject(Songs.class);
        song11.setTitle("Заготовка 6");
        song11.setSetlistid(0);
        song11.setSongid(10);
        song11.setPosition(10);
        song11.setMetronombpm(140);
        song11.setAudioOn(false);
        song11.setLyrics("Текст песни Заготовка 6");
        song11.setLyricshasopen(false);
        song11.setPlaystarted(false);

        Songs song12 = songsrealm.createObject(Songs.class);
        song12.setTitle("Заготовка 7");
        song12.setSetlistid(0);
        song12.setSongid(11);
        song12.setPosition(11);
        song12.setMetronombpm(135);
        song12.setAudioOn(false);
        song12.setLyrics("Текст песни Заготовка 7");
        song12.setLyricshasopen(false);
        song12.setPlaystarted(false);

        Songs song13 = songsrealm.createObject(Songs.class);
        song13.setTitle("Заготовка 8");
        song13.setSetlistid(0);
        song13.setSongid(12);
        song13.setPosition(12);
        song13.setMetronombpm(150);
        song13.setAudioOn(false);
        song13.setLyrics("Текст песни Заготовка 8");
        song13.setLyricshasopen(false);
        song13.setPlaystarted(false);*/

        /*for (int i = 0; i < 7; i++) {
            int startFrom = 13;
            Songs songNew = songsrealm.createObject(Songs.class);
            songNew.setTitle(getResources().getString(R.string.EmptySongName));
            songNew.setSetlistid(0);
            songNew.setSongid(startFrom + i);
            songNew.setPosition(startFrom + i);
            songNew.setMetronombpm(80);
            songNew.setAudioOn(false);
            songNew.setLyrics(getResources().getString(R.string.EmptyLyrics));
            songNew.setLyricshasopen(false);
            songNew.setPlaystarted(false);
        }*/
        songsrealm.commitTransaction();
        songsrealm.close();
    }
}