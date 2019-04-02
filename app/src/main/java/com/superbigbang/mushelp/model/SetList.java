package com.superbigbang.mushelp.model;

import io.realm.RealmObject;
import io.realm.annotations.Required;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SetList extends RealmObject {

    @Required
    private String name;

    private int id;
    private int position;
    private boolean isOpen;
}
