package com.redant.codeland.entity.Bmob;

import cn.bmob.v3.BmobUser;

public class User extends BmobUser {
    //设置用户的管理权限 1：版主，0：普通用户
    private Integer controlLevel;

    public Integer getControlLevel() {
        return controlLevel;
    }

    public void setControlLevel(Integer controlLevel) {
        this.controlLevel = controlLevel;
    }
}
