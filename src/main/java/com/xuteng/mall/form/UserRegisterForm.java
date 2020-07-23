package com.xuteng.mall.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName UserForm
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 16:12
 * @Version 1.0
 **/
@Data
public class UserRegisterForm {
//    @NotBlank 用于String 判断空格
//    @NotNull
//    @NotEmpty 用于集合
    @NotBlank(message = "注册提交的参数名有误")
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
}
