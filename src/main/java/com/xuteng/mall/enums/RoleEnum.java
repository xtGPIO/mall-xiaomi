package com.xuteng.mall.enums;

import lombok.Getter;

/**
 * @ClassName RoleEnum
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 11:28
 * @Version 1.0
 **/
@Getter
public enum RoleEnum {
    ADMIN(0),
    CUSTOMER(1),
    ;
    Integer code;

    RoleEnum(Integer code) {
        this.code=code;
    }
}
