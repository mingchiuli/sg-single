package com.chiu.sgsingle.exception;

import com.chiu.sgsingle.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;


/**
 * @author mingchiuli
 * @create 2021-10-27 9:29 PM
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.chiu.sgsingle.controller"})
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(value = AuthenticationException.class)
    public Result<?> handler(AuthenticationException e) {
        log.error("权限异常:{}", e.toString());
        return Result.fail(401, e.getMessage(), null);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result<String> handler(MethodArgumentNotValidException e) {
        log.error("实体校验异常------------{}", e.toString());
        BindingResult bindingResult = e.getBindingResult();
        Optional<ObjectError> objectError = bindingResult.getAllErrors().stream().findFirst();
        return objectError.<Result<String>>map(error ->
                Result.fail(error.getDefaultMessage()))
                .orElseGet(() ->
                        Result.fail(null));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = IllegalArgumentException.class)
    public Result<String> handler(IllegalArgumentException e) {
        log.error("Assert断言异常------------{}", e.toString());
        return Result.fail(e.getMessage());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<String> exceptionHandler(AccessDeniedException e){
        log.error("权限异常------------{}",e.toString());
        return Result.fail(e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = RuntimeException.class)
    public Result<String> handler(RuntimeException e) {
        log.error("运行时异常------------{}", e.toString());
        return Result.fail(e.getMessage());
    }

}
