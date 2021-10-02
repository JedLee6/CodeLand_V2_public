package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sdyt_ on 2018/3/27.
 */

public class Animal extends DataSupport {
    private String name;
    private String eating;
    private String kind;
    private List<String> animalNameList=new ArrayList<>();
    private int level;

    public void setName(String name) {
        this.name = name;
    }

    public void setEating(String eating) {
        this.eating = eating;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setAnimalNameList(List<String> animalNameList) {
        this.animalNameList = animalNameList;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {

        return name;
    }

    public String getEating() {
        return eating;
    }

    public String getKind() {
        return kind;
    }

    public List<String> getAnimalNameList() {
        return animalNameList;
    }

    public int getLevel() {
        return level;
    }
}
