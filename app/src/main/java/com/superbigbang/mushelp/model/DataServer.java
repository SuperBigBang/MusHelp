package com.superbigbang.mushelp.model;

import java.util.ArrayList;
import java.util.List;

public class DataServer {

    private static final String SET_LIST_NAME = "Сет лист ";

    private DataServer() {
    }
    public static List<NormalMultipleEntity> getSetListsMultipleEntities() {
        List<NormalMultipleEntity> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(new NormalMultipleEntity(NormalMultipleEntity.SET_LISTS, SET_LIST_NAME + (i + 1)));
        }
        return list;
    }

    private static final String tdgOverAndOverLyrics = "I feel it everyday, it's all the same\n" +
            "It brings me down, but I'm the one to blame\n" +
            "I've tried everything to get away\n" +
            "So here I go again\n" +
            "Chasing you down again\n" +
            "Why do I do this?\n" +
            "Over and over, over and over\n" +
            "I fall for you\n" +
            "Over and over, over and over\n" +
            "I try not to\n" +
            "It feels like everyday stays the same\n" +
            "It's dragging me down, and I can't pull away\n" +
            "So here I go again\n" +
            "Chasing you down again\n" +
            "Why do I do this?\n" +
            "Over and over, over and over\n" +
            "I fall for you\n" +
            "Over and over, over and over\n" +
            "I try not to\n" +
            "Over and over, over and over\n" +
            "You make me fall for you\n" +
            "Over and over, over and over\n" +
            "You don't even try\n" +
            "So many thoughts that I can't get out of my head\n" +
            "I try to live without you\n" +
            "Every time I do, I feel dead\n" +
            "I know what's best for me\n" +
            "But I want you instead\n" +
            "I'll keep on wasting all my time\n" +
            "Over and over, over and over\n" +
            "I fall for you\n" +
            "Over and over, over and over\n" +
            "I try not to\n" +
            "Over and over, over and over\n" +
            "You make me fall for you\n" +
            "Over and over, over and over\n" +
            "You don't even try to\n" +
            "Авторы: Gavin Brown / Neil Sanderson / Adam Gontier / Brad Walst / Barry Stock\n" +
            "Текст песни \"Over and Over\", © Sony/ATV Music Publishing LLC, Kobalt Music Publishing Ltd.";

    public static List<NormalMultipleEntity> getSongsMultipleEntities() {
        List<NormalMultipleEntity> list = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            list.add(new NormalMultipleEntity(NormalMultipleEntity.SONGS_LISTS,
                    "Song name", "Song lyrics", i, 170));
            if (i == 9) {
                list.add(new NormalMultipleEntity(NormalMultipleEntity.SONGS_LISTS,
                        "Долгое нажатие позволяет отредактировать элемент",
                        "Song lyrics", ++i, 140));
            }
            if (i == 1) {
                list.add(new NormalMultipleEntity(NormalMultipleEntity.SONGS_LISTS,
                        "Three Days Grace - Over and Over",
                        tdgOverAndOverLyrics, ++i, 125));
            }
        }
        return list;
    }
}
