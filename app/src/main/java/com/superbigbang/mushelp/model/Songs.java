package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Songs extends RealmObject {
    @Required
    private String title;

    private int setlistid;
    private int songid;
    private int position;
    private int metronombpm;
    private boolean audioOn;
    private String audiofile;
    private boolean countdownOn;
    private String lyrics;
    private boolean lyricshasopen;
    private boolean playstarted;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSetlistid() {
        return setlistid;
    }

    public void setSetlistid(int setlistid) {
        this.setlistid = setlistid;
    }

    public int getSongid() {
        return songid;
    }

    public void setSongid(int songid) {
        this.songid = songid;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getMetronombpm() {
        return metronombpm;
    }

    public void setMetronombpm(int metronombpm) {
        this.metronombpm = metronombpm;
    }

    public boolean isAudioOn() {
        return audioOn;
    }

    public void setAudioOn(boolean audioOn) {
        this.audioOn = audioOn;
    }

    public String getAudiofile() {
        return audiofile;
    }

    public void setAudiofile(String audiofile) {
        this.audiofile = audiofile;
    }

    public boolean isCountdownOn() {
        return countdownOn;
    }

    public void setCountdownOn(boolean countdownOn) {
        this.countdownOn = countdownOn;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public boolean isLyricshasopen() {
        return lyricshasopen;
    }

    public void setLyricshasopen(boolean lyricshasopen) {
        this.lyricshasopen = lyricshasopen;
    }

    public boolean isPlaystarted() {
        return playstarted;
    }

    public void setPlaystarted(boolean playstarted) {
        this.playstarted = playstarted;
    }
}
