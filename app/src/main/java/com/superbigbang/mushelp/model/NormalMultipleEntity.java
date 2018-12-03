package com.superbigbang.mushelp.model;

public class NormalMultipleEntity {

    public static final int SET_LISTS = 1;
    public static final int SONGS_LISTS = 4;
    public static final int SINGLE_IMG = 2;
    public static final int TEXT_IMG = 3;

    public int type;
    public int id;
    public String content;
    public String lyrics;


    public NormalMultipleEntity(int type) {
        this.type = type;
    }

    public NormalMultipleEntity(int type, String content) {
        this.type = type;
        this.content = content;
    }

    public NormalMultipleEntity(int type, String content, String lyrics, int id) {
        this.type = type;
        this.id = id;
        this.content = content;
        this.lyrics = lyrics;
    }
}
