package com.xuteng.mall.controller;

import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.form.UserLoginForm;
import com.xuteng.mall.form.UserRegisterForm;
import com.xuteng.mall.pojo.User;
import com.xuteng.mall.service.IUserService;
import com.xuteng.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.xuteng.mall.consts.MallConst.CURRENT_USER;

/**
 * @ClassName UserController
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 13:10
 * @Version 1.0
 **/
@RestController
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    /**
     * 前端使用urlencode和json格式传参使用的参数注解不一样
     * urlencode：@RequestParam String username或者User user
     * json:@RequestBody User user
     *https://blog.csdn.net/weixin_43770545/article/details/90237097
     * 上面链接是使用注解@Validated和BindingResult对入参进行非空校验
     * BeanUtils.copyProperties是spring中拷贝一个对象的内容到另一个对象中
     */
    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userForm,
                               BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("注册提交的参数有误,{}{}",bindingResult.getFieldError().getField(),bindingResult.getFieldError().getDefaultMessage());
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
        }
        User user = new User();
        BeanUtils.copyProperties(userForm,user);
        return userService.register(user);
    }

    /**
     *
     * @param userLoginForm
     * @param bindingResult
     * @param session
     * @return
     * sessin是一个map类型的类，键值对都是String
     */
    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  BindingResult bindingResult,
                                  HttpSession session){
        if(bindingResult.hasErrors()){
            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
        }

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        //设置Session
        session.setAttribute(CURRENT_USER,userResponseVo.getData());
        log.info("/login sessionId={}", session.getId());
        return userResponseVo;
    }
    //session保存在内存中，为了不丢失一般存在redis中，改进版：token+redis
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        log.info("/user sessionId={}", session.getId());
        User user = (User)session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);
    }
    /**
     * {@link TomcatServletWebServerFactory} getSessionTimeoutInMinutes
     */
    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session){
        log.info("/user/logout sessionId={}", session.getId());
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();
    }
}
