package com.ldcc.pliss.deliveryadvisor.databases;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by pliss on 2018. 2. 23..
 */

public class WorkRepository extends RealmObject {
    @PrimaryKey
    private int id;
    private int stars;

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getStars() { return stars; }

    public void setStars(int stars) { this.stars = stars; }


}
