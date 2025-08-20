package com.ybchen.exception;

import com.ybchen.utils.ReturnData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @description: 全局异常
 * @author: Alex
 * @create: 2023-08-16 22:28
 */
@RestControllerAdvice
@Slf4j
public class GlobalException {
    @ExceptionHandler(Exception.class)
    public ReturnData exception(Exception e) {
        log.error("全局异常：{}", e);
        return ReturnData.buildError(e.getMessage());
    }

}