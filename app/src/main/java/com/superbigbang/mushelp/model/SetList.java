package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class SetList extends RealmObject {

    @Required
    private String name;

    private int id;
    private int position;
    private boolean isOpen;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setAllSettings(SetList setListLoad) {
        setId(setListLoad.getId());
        setName(setListLoad.getName());
        setPosition(setListLoad.getPosition());
        setOpen(setListLoad.isOpen());
    }
}
