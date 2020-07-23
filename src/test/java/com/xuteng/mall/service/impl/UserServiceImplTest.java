package com.xuteng.mall.service.impl;

import com.xuteng.mall.MallApplicationTests;
import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.enums.RoleEnum;
import com.xuteng.mall.pojo.User;
import com.xuteng.mall.service.IUserService;
import com.xuteng.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @ClassName UserServiceImplTest
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 11:23
 * @Version 1.0
 * @Transactional让测试数据不进入数据库
 **/
@Transactional
public class UserServiceImplTest extends MallApplicationTests {

    public static final String USERNAME = "jack";

    public static final String PASSWORD = "123456";

    @Autowired
    private IUserService userService;

    @Before
    public void register() {
        User user = new User(USERNAME, PASSWORD, "jack@qq.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login() {
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        /**
         * 多个断言方法. 主用于比较测试传递进去的两个参数.
         *
         * Assert.assertEquals();及其重载方法: 1. 如果两者一致, 程序继续往下运行. 2. 如果两者不一致, 中断测试方法, 抛出异常信息 AssertionFailedError .
         */
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
    }
}