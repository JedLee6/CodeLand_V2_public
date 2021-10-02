package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by sdyt_ on 2018/3/27.
 */
//存储用户各个关卡的“模块名字”、
public class Rate extends DataSupport {
    private int rating;
    private String name;
    private String model;
    private int id;
    private boolean beClick;

    public void setModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setBeClick(boolean beClick) {
        this.beClick = beClick;
    }

    public int getRating() {

        return rating;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public boolean isBeClick() {
        return beClick;
    }

}
