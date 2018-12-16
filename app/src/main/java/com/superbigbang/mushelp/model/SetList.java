package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class SetList extends RealmObject {

    @Required
    private String name;

    private int id;
    private int position;

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
}
