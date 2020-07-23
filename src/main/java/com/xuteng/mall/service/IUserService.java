package com.xuteng.mall.service;

import com.xuteng.mall.pojo.User;
import com.xuteng.mall.vo.ResponseVo;

/**
 * @ClassName IUserService
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 10:47
 * @Version 1.0
 **/
public interface IUserService {
    /**
     * 注册
     */
      ResponseVo<User> register(User user);
    /**
     * 登录
     */
      ResponseVo<User> login(String username,String password);
    /**
     * 注册
     */
}
