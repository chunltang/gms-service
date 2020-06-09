package com.zs.gms.common.handler;

import com.zs.gms.common.entity.GmsResponse;
import com.zs.gms.common.exception.GmsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public GmsResponse handleException(Exception e){
       log.error("系统内部异常，异常信息:",e);
       return new GmsResponse().badRequest().message("系统内部异常");
    }

    @ExceptionHandler(value = GmsException.class)
    public GmsResponse handleGmsException(GmsException e){
       log.error("系统错误,异常信息:"+e.getMessage(),e);
       return new GmsResponse().badRequest().message(e.getMessage());
    }

    @ExceptionHandler(value = UnauthorizedException.class)
    public GmsResponse handleUnauthorizedException(UnauthorizedException e) {
        log.debug("UnauthorizedException", e);
        return new GmsResponse().code(HttpStatus.FORBIDDEN).message("无相关访问权限");
    }

    @ExceptionHandler(value = AuthenticationException.class)
    public GmsResponse handleAuthenticationException(AuthenticationException e) {
        log.debug("AuthenticationException", e);
        return new GmsResponse().code(HttpStatus.UNAUTHORIZED).message("登录校验失败");
    }

    /**
     * 账号未注册
     * */
    @ExceptionHandler(value = UnknownAccountException.class)
    public GmsResponse handleUnknownAccountException(UnknownAccountException e) {
        log.debug("UnknownAccountException", e);
        return new GmsResponse().code(HttpStatus.UNAUTHORIZED).message(e.getMessage());
    }

    /**
     * 账号或密码不存在!
     * */
    @ExceptionHandler(value = IncorrectCredentialsException.class)
    public GmsResponse handleIncorrectCredentialsException(IncorrectCredentialsException e) {
        log.debug("IncorrectCredentialsException", e);
        return new GmsResponse().code(HttpStatus.UNAUTHORIZED).message(e.getMessage());
    }

    /**
     * 注意处理请求参数校验(普通参数校验)
     * */
    @ExceptionHandler(value = ConstraintViolationException.class)
    public GmsResponse handleConstraintViolationException(ConstraintViolationException e) {
        log.debug("ConstraintViolationException", e);
        StringBuilder message = new StringBuilder();
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        for (ConstraintViolation<?> violation : violations) {
            //Path path = violation.getPropertyPath();
            //String[] pathArr = StringUtils.splitByWholeSeparatorPreserveAllTokens(path.toString(), ".");
            message.append(violation.getMessage()).append(",");
            //message.append(pathArr[1]).append(violation.getMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new GmsResponse().code(HttpStatus.BAD_REQUEST).message(message.toString());
    }

    /**
     * 实体传参参数校验
     * */
    @ExceptionHandler(BindException.class)
    public GmsResponse validExceptionHandler(BindException e) {
        log.debug("BindException", e);
        StringBuilder message = new StringBuilder();
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        for (FieldError error : fieldErrors) {
            message.append(error.getDefaultMessage()).append(",");
        }
        message = new StringBuilder(message.substring(0, message.length() - 1));
        return new GmsResponse().code(HttpStatus.BAD_REQUEST).message(message.toString());
    }
}
