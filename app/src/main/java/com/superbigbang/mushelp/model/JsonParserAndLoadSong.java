package com.superbigbang.mushelp.model;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;

public class JsonParserAndLoadSong {
    public void loadSong(String response, Realm realm, int setlistid, int songid) throws JSONException {
        JSONObject userJson = new JSONObject(response);
        Songs song = realm.createObject(Songs.class);
        song.setTitle(userJson.getString("title"));
        song.setSetlistid(setlistid);
        song.setSongid(songid);
        song.setPosition(userJson.getInt("position"));
        song.setMetronombpm(userJson.getInt("metronombpm"));
        song.setAudioOn(false);
        song.setLyrics(userJson.getString("lyrics"));
        song.setLyricshasopen(false);
        song.setPlaystarted(false);
    }
}
