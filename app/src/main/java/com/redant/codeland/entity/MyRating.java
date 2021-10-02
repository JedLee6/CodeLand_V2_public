package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by sdyt_ on 2018/4/11.
 */

public class MyRating extends DataSupport {
    private int rating;
    private String name;
    private String model;

    public int getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
