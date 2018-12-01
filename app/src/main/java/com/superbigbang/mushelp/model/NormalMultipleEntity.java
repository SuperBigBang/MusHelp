package com.superbigbang.mushelp.model;

public class NormalMultipleEntity {

    public static final int SET_LISTS = 1;
    public static final int SONGS_LISTS = 4;
    public static final int SINGLE_IMG = 2;
    public static final int TEXT_IMG = 3;

    public int type;
    public String content;

    public NormalMultipleEntity(int type) {
        this.type = type;
    }

    public NormalMultipleEntity(int type, String content) {
        this.type = type;
        this.content = content;
    }
}
