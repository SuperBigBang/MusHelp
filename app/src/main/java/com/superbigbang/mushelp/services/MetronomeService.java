package com.superbigbang.mushelp.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.TickData;

public class MetronomeService extends Service implements Runnable {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String EXTRA_BPM = "EXTRA_BPM";

    public static final String PREF_TICK = "tick";
    public static final String PREF_INTERVAL = "interval";
    public static final TickData[] ticks = new TickData[]{
            new TickData(R.string.title_beep, R.raw.beep),
            new TickData(R.string.title_wood, R.raw.wood),
            new TickData(R.string.title_vibrate)
    };
    private final IBinder binder = new LocalBinder();
    private SharedPreferences prefs;
    private int bpm;
    private long interval;
    private SoundPool soundPool;
    private Handler handler;
    private int soundId = -1;
    private boolean isPlaying;
    private Vibrator vibrator;

    private static int toBpm(long interval) {
        return (int) (60000 / interval);
    }

    private static long toInterval(int bpm) {
        return (long) 60000 / bpm;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build())
                    .build();
        } else soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        int tick = prefs.getInt(PREF_TICK, 0);
        if (!ticks[tick].isVibration())
            soundId = ticks[tick].getSoundId(this, soundPool);

        interval = prefs.getLong(PREF_INTERVAL, 500);
        bpm = toBpm(interval);

        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    setBpm(intent.getIntExtra(EXTRA_BPM, bpm));
                    pause();
                    play();
                    break;
                case ACTION_PAUSE:
                    pause();
                    break;
            }
        }
        return START_STICKY;
    }

    public void play() {
        handler.post(this);
        isPlaying = true;

        Intent intent = new Intent(this, MetronomeService.class);
        intent.setAction(ACTION_PAUSE);

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(new NotificationChannel("mushelp", getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT));

            builder = new NotificationCompat.Builder(this, "mushelp");
        } else
            builder = new NotificationCompat.Builder(this);

        startForeground(530,
                builder.setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_desc))
                        .setSmallIcon(R.drawable.ic_action_name)
                        .setContentIntent(PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_ONE_SHOT))
                        .setPriority(NotificationCompat.PRIORITY_LOW)
                        .build()
        );
    }

    public void pause() {
        handler.removeCallbacks(this);
        stopForeground(true);
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public long getInterval() {
        return interval;
    }

    public int getBpm() {
        return bpm;
    }

    public void setBpm(int bpm) {
        this.bpm = bpm;
        interval = toInterval(bpm);
        prefs.edit().putLong(PREF_INTERVAL, interval).apply();
    }

    public int getTick() {
        return prefs.getInt(PREF_TICK, 0);
    }

    public void setTick(int tick) {
        if (!ticks[tick].isVibration()) {
            soundId = ticks[tick].getSoundId(this, soundPool);
            if (!isPlaying)
                soundPool.play(soundId, 1.0f, 1.0f, 0, 0, 1.0f);
        } else soundId = -1;

        prefs.edit().putInt(PREF_TICK, tick).apply();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        super.onDestroy();
    }

    @Override
    public void run() {
        if (isPlaying) {
            handler.postDelayed(this, interval);

            if (soundId != -1) {
                soundPool.play(soundId, 1, 1, 0, 0, 1);
            } else if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(20);
            }
        }
    }

    public class LocalBinder extends Binder {
        public MetronomeService getService() {
            return MetronomeService.this;
        }
    }
}
