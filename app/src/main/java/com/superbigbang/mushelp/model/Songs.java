package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;

public class Songs extends RealmObject {
    @Required
    private int position;
}
