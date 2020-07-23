package com.xuteng.mall.service.impl;

import com.xuteng.mall.dao.UserMapper;
import com.xuteng.mall.enums.RoleEnum;
import com.xuteng.mall.pojo.User;
import com.xuteng.mall.service.IUserService;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.xuteng.mall.enums.ResponseEnum.*;

/**
 * @ClassName UserServiceImpl
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 10:54
 * @Version 1.0
 *
 * (required=false)
 * 当我们在使用@Autowired注解的时候，默认required=true,表示注入的时候bean必须存在，否则注入失败。
 **/
@Service
public class UserServiceImpl implements IUserService {
    @Autowired(required=false)
    private UserMapper userMapper;

    @Override
    public ResponseVo register(User user) {
        //测试异常用的，运行项目不需要加
//        error();

        //username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if(countByUsername>0){
            return ResponseVo.error(USERNAME_EXIST);
        }
        //email不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if(countByEmail>0){
            return ResponseVo.error(EMAIL_EXIST);
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());

        //MD5加密摘要算法
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        //写入数据库
        int resultCount = userMapper.insertSelective(user);
        if (resultCount == 0) {
            return ResponseVo.error(ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if(user==null){
            //用户名不存在(返回：用户名或者密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if(!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))){
            //密码错误(返回：用户名或者密码错误)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        //将密码置为空，返回前端的时候不让看见
        user.setPassword("");
        return ResponseVo.success(user);
    }

    private void error() {
        throw new RuntimeException("异常错误");
    }
}
