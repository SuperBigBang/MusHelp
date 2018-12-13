package com.superbigbang.mushelp.model;

public class NormalMultipleEntity {

    public static final int SET_LISTS = 1;
    public static final int SONGS_LISTS = 4;

    public int type;
    public int position;
    public int id;
    public String setlistname;
    public String songname;
    public String lyrics;
    public String audioFile;
    public int bitrate;
    public boolean audioIsOn;
    public boolean countdownIsOn;

    NormalMultipleEntity(int type, String setlistname) {
        //setList
        this.type = type;
        this.setlistname = setlistname;
    }

    NormalMultipleEntity(int type, String songname, String lyrics, String audioFile, int id, int position, int bitrate,
                         boolean audioIsOn, boolean countdownIsOn) {
        //songs
        this.type = type;
        this.id = id;
        this.songname = songname;
        this.lyrics = lyrics;
        this.audioFile = audioFile;
        this.bitrate = bitrate;
        this.position = position;
        this.audioIsOn = audioIsOn;
        this.countdownIsOn = countdownIsOn;
    }
}
