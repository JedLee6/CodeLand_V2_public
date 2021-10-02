package com.redant.codeland.entity;

import org.litepal.crud.DataSupport;

public class Coding extends DataSupport {
    private String date;
    private String code;
    private String name;
    public String getDate()
    {
        return date;
    }
    public String getCode()
    {
        return code;
    }
    public String getName()
    {
        return name;
    }
    public void setDate(String date)
    {
        this.date=date;
    }
    public void setCode(String code)
    {
        this.code=code;
    }
    public void setName(String name)
    {
        this.name=name;
    }

}
