package com.xuteng.mall.exception;

import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.xuteng.mall.enums.ResponseEnum.ERROR;

/**
 * @ClassName RuntimeException
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 17:07
 * @Version 1.0
 * @ResponseBody 返回json格式数据
 * @ControllerAdvice
 * 全局异常处理
 * 全局数据绑定
 * 全局数据预处理
 **/
@ControllerAdvice
public class RuntimeExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseVo handle(RuntimeException e){
        return ResponseVo.error(ERROR,e.getMessage());
    }
    @ExceptionHandler(UserLoginException.class)
    @ResponseBody
    public ResponseVo userLoginHandle() {
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }
}
