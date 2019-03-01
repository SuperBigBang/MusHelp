package com.superbigbang.mushelp.services;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.SeekBar;

import com.superbigbang.mushelp.R;
import com.superbigbang.mushelp.model.TickData;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import timber.log.Timber;


public class MetronomeService extends JobIntentService {

    public static final String ACTION_START = "ACTION_START";
    public static final String ACTION_PAUSE = "ACTION_PAUSE";
    public static final String EXTRA_BPM = "EXTRA_BPM";

    public static final String PREF_TICK = "tick";
    public static final String PREF_INTERVAL = "interval";
    public static TickData[] ticks = new TickData[]{
            new TickData(R.string.title_beep, R.raw.beep),
            new TickData(R.string.title_wood, R.raw.tick1),
            new TickData(R.string.title_ding, R.raw.ding),
            new TickData(R.string.title_vibrate),
    };
    private final IBinder binder = new LocalBinder();
    SeekBar mSeekBar;
    private SharedPreferences prefs;
    //  private int bpm;
    private long interval;
    private SoundPool soundPool;
    private int soundId = -1;
    private boolean isPlaying;
    private Vibrator vibrator;
    public static final int JOB_ID = 1;

    /* private boolean start;
     private long startTime1;
     private long endTime2;*/
    private int tick;
    private AudioManager am;
    private MediaPlayer mediaPlayer;
    private int resumePosition;
    private String audioFilePath;
    private String backupAudioFilePath;
    private boolean countdownIsOn;
    private boolean pause;
    private float currentRate = 1f;
    private int countdownNum = 8;

    // private SeekBar mSeekBar;
    private PublishSubject<Object> stopTrigger = PublishSubject.create();
    private PublishSubject<Object> stopSeekBarTrigger = PublishSubject.create();

    private Scheduler scheduler;

    private static int toBpm(long interval) {
        return (int) (60000 / interval);
    }

    private static long toInterval(int bpm) {
        return (long) 60000 / bpm;
    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MetronomeService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        // your code
    }

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        scheduler = Schedulers.newThread();
        loadSoundPoolOnNewThreadRX();

        interval = prefs.getLong(PREF_INTERVAL, 500);

        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        mediaPlayer = new MediaPlayer();
        //   bpm = toBpm(interval);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                case ACTION_START:
                    //          setBpm(intent.getIntExtra(EXTRA_BPM, bpm));
                    stop();
                    play();
                    break;
                case ACTION_PAUSE:
                    stop();
                    break;
            }
        }
        return START_STICKY;
    }

    //SeekBar operations:
    public SeekBar getmSeekBar() {
        return mSeekBar;
    }

    public void setmSeekBar(SeekBar mSeekBar) {
        this.mSeekBar = mSeekBar;
        // mSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.cornerColor), PorterDuff.Mode.MULTIPLY);
        // mSeekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorCursor), PorterDuff.Mode.SRC_ATOP);
        stopSeekBarTrigger.onNext(false);
        initializeSeekBar();
        // Set a change listener for seek bar
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /*
                void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser)
                    Notification that the progress level has changed. Clients can use the fromUser
                    parameter to distinguish user-initiated changes from those that occurred programmatically.

                Parameters
                    seekBar SeekBar : The SeekBar whose progress has changed
                    progress int : The current progress level. This will be in the range min..max
                                   where min and max were set by setMin(int) and setMax(int),
                                   respectively. (The default values for min is 0 and max is 100.)
                    fromUser boolean : True if the progress change was initiated by the user.
            */
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (mediaPlayer != null && b) {
                        /*
                            void seekTo (int msec)
                                Seeks to specified time position. Same as seekTo(long, int)
                                with mode = SEEK_PREVIOUS_SYNC.

                            Parameters
                                msec int: the offset in milliseconds from the start to seek to

                            Throws
                                IllegalStateException : if the internal player engine has not been initialized
                        */
                    mediaPlayer.seekTo(i * 1000);
                    resumePosition = i * 1000;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    @SuppressLint("CheckResult")
    protected void initializeSeekBar() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mSeekBar.setMax(mediaPlayer.getDuration() / 1000);

            try {
                if (mSeekBar.getVisibility() == View.GONE) {
                    mSeekBar.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Observable.interval(1000, TimeUnit.MILLISECONDS, scheduler)
                    .takeUntil(stopSeekBarTrigger)
                    .subscribe((Long value) -> {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000; // In milliseconds
                        mSeekBar.setProgress(mCurrentPosition);
                    });
        }
    }

   /* protected void getAudioStats(){
        int duration  = mPlayer.getDuration()/1000; // In milliseconds
        int due = (mPlayer.getDuration() - mPlayer.getCurrentPosition())/1000;
        int pass = duration - due;

        mPass.setText("" + pass + " seconds");
        mDuration.setText("" + duration + " seconds");
        mDue.setText("" + due + " seconds");
    }*/
    //End SeekBar operations;

    @SuppressLint("CheckResult")
    public void playWithRX() {
        if (audioFilePath == null && backupAudioFilePath != null) {
            audioFilePath = backupAudioFilePath;
            backupAudioFilePath = null;
        }
        if (audioFilePath == null) {
            Observable.interval(interval, TimeUnit.MILLISECONDS, scheduler)
                    .takeUntil(stopTrigger)
                    .subscribe((Long value) -> {
                        if (soundId != -1) {
                            soundPool.play(soundId, 1, 1, 1, 0, 1);
                            //      soundPool.play(soundId, 0, 0, 0, -1, 1);
                            //Timber.e("Setted interval: %s", String.valueOf(interval));
                            //  checkIntervals();
                        } else if (Build.VERSION.SDK_INT >= 26) {
                            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            vibrator.vibrate(20);
                        }
                    });
        } else if (!countdownIsOn) {
            prepareAndStartOrResumePlayback();
        } else {
            Observable.interval(interval, TimeUnit.MILLISECONDS, scheduler)
                    .takeUntil(stopTrigger)
                    .subscribe((Long value) -> {
                        if (countdownNum != 0) {
                            if (soundId != -1) {
                                soundPool.play(soundId, 1, 1, 1, 0, 1);
                                //      soundPool.play(soundId, 0, 0, 0, -1, 1);
                                //Timber.e("Setted interval: %s", String.valueOf(interval));
                                //  checkIntervals();
                            } else if (Build.VERSION.SDK_INT >= 26) {
                                vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE));
                            } else {
                                vibrator.vibrate(20);
                            }
                            countdownNum--;
                        } else {
                            countdownNum = 8;
                            stopTrigger.onNext(false);
                            stopSeekBarTrigger.onNext(false);
                            isPlaying = false;
                            Observable.just(1)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe((Integer values) -> prepareAndStartOrResumePlayback());
                        }
                    });
        }
    }

    private void prepareAndStartOrResumePlayback() {
        if (!isPlaying() && !audioFilePath.equals(backupAudioFilePath)) {
            try {
                // Set the data source to the mediaFile location
                mediaPlayer.setOnErrorListener((MediaPlayer mp, int what, int extra) -> {
                    Timber.e("ERROR: %s", extra);
                    return false;
                });
                mediaPlayer.reset();
                mediaPlayer.setDataSource(audioFilePath);
            } catch (IOException e) {
                e.printStackTrace();
                stopSelf();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            try {
                mediaPlayer.prepare();
                playMedia();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isPlaying = true;
        } else {
            resumeMedia();
            isPlaying = true;
        }

    }

    public boolean mediaPlayerIsPlay() {
        return mediaPlayer.isPlaying();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void changeRate(float speedRate) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(speedRate));
            currentRate = speedRate;
        } else {
            currentRate = speedRate;
        }
    }

    private void playMedia() {
        if (!mediaPlayer.isPlaying()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(currentRate));
            } else mediaPlayer.start();
            initializeSeekBar();
            mediaPlayer.setOnCompletionListener(mp -> {
                stop();
                mSeekBar.setVisibility(View.GONE);
            });
        }
    }

    private void stopMedia() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mSeekBar.setVisibility(View.GONE);
            mediaPlayer.stop();
            mediaPlayer.reset();
            backupAudioFilePath = null;
            audioFilePath = null;
        }
    }

    private void pauseMedia() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
            isPlaying = false;
        }
    }

    private void resumeMedia() {
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mediaPlayer.setPlaybackParams(mediaPlayer.getPlaybackParams().setSpeed(currentRate));
            } else mediaPlayer.start();
            initializeSeekBar();
        }
    }

    @SuppressLint("CheckResult")
    void loadSoundPoolOnNewThreadRX() {
        Observable.just(0)
                .observeOn(scheduler)
                .subscribe((Integer value) -> {
                    SoundPool soundPool0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        soundPool0 = new SoundPool.Builder()
                                .setMaxStreams(4)
                                .setAudioAttributes(new AudioAttributes.Builder()
                                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                        .build())
                                .build();
                    } else soundPool0 = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
                    tick = prefs.getInt(PREF_TICK, 0);
                    if (!ticks[tick].isVibration()) {
                        soundId = ticks[tick].getSoundId(this, soundPool0);
                    }
                    soundPool = soundPool0;
                });
    }

    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void stopWithRX() {
        stopMedia();
        stopTrigger.onNext(false);
        stopSeekBarTrigger.onNext(false);
        countdownNum = 8;
    }

    private void pauseWithRX() {
        pauseMedia();
        backupAudioFilePath = audioFilePath;
        audioFilePath = null;
        stopTrigger.onNext(false);
        stopSeekBarTrigger.onNext(false);
        countdownNum = 8;
    }

    public void play() {
        playWithRX();

        isPlaying = true;
        Intent intent = new Intent(this, MetronomeService.class);
        intent.setAction(ACTION_PAUSE);

        NotificationCompat.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(new NotificationChannel("mushelp", getString(R.string.app_name), NotificationManager.IMPORTANCE_LOW));

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

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setBpm(int bpm) {
        //    this.bpm = bpm;
        interval = toInterval(bpm);
        prefs.edit().putLong(PREF_INTERVAL, interval).apply();
    }

    public void setFilePathOfCurrentAudio(String filePath) {
        if (filePath != null && filePath.equals(backupAudioFilePath)) {
            audioFilePath = filePath;
        } else {
            audioFilePath = filePath;
            backupAudioFilePath = null;
        }
    }

    public void changeTickSound() {
        if (tick + 1 == ticks.length) {
            tick = 0;
            setTick(tick);
        } else {
            tick++;
            setTick(tick);
        }
    }

    public void pause() {
        if (audioFilePath != null) {
            pauseWithRX();
            stopForeground(true);

            //    isPlaying=false;
        } else {
            stop();
        }
    }

    public void stop() {
        stopWithRX();
        stopForeground(true);
        isPlaying = false;
        audioFilePath = null;
        backupAudioFilePath = null;
    }

    public void setCountdownIsOn(boolean countdownIsOn) {
        this.countdownIsOn = countdownIsOn;
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

    public void setTick(int tick) {
        if (!ticks[tick].isVibration()) {
            soundId = ticks[tick].getSoundId(this, soundPool);
         /*   if (!isPlaying){
                soundPool.play(soundId, 1.0f, 1.0f, 1, 0, 1.0f);}*/
        } else soundId = -1;
        prefs.edit().putInt(PREF_TICK, tick).apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        soundPool.release();
        releaseMP();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    public class LocalBinder extends Binder {
        public MetronomeService getService() {
            return MetronomeService.this;
        }
    }
}