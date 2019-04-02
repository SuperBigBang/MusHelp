package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
