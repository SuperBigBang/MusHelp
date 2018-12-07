package com.superbigbang.mushelp.model;

public class NormalMultipleEntity {

    public static final int SET_LISTS = 1;
    public static final int SONGS_LISTS = 4;

    public int type;
    public int id;
    public String setlistname;
    public String songname;
    public String lyrics;
    public int bitrate;

    NormalMultipleEntity(int type, String setlistname) {
        this.type = type;
        this.setlistname = setlistname;
    }

    NormalMultipleEntity(int type, String songname, String lyrics, int id, int bitrate) {
        this.type = type;
        this.id = id;
        this.songname = songname;
        this.lyrics = lyrics;
        this.bitrate = bitrate;
    }
}
