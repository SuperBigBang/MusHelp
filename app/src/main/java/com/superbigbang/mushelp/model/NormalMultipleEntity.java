package com.superbigbang.mushelp.model;

public class NormalMultipleEntity {

    public static final int SET_LISTS = 1;
    public static final int SONGS_LISTS = 4;

    public int type;
    public int id;
    public String content;
    public String lyrics;
    public int bitrate;

    NormalMultipleEntity(int type, String content) {
        this.type = type;
        this.content = content;
    }

    NormalMultipleEntity(int type, String content, String lyrics, int id, int bitrate) {
        this.type = type;
        this.id = id;
        this.content = content;
        this.lyrics = lyrics;
        this.bitrate = bitrate;
    }
}
