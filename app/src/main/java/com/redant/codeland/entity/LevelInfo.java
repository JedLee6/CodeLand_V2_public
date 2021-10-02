package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

/**
 * Created by jedlee on 2018/3/31.
 */

public class LevelInfo extends DataSupport {
    private String category;
    private int level;
    private String url;
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
    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }



    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
