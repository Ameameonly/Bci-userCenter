package com.yxd.yuangongguanli.exception;


import com.yxd.yuangongguanli.common.BaseResponse;
import com.yxd.yuangongguanli.common.ErrorCode;
import com.yxd.yuangongguanli.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public static BaseResponse handleBusinessException(BusinessException e) {
        return ResultUtils.error(e.getCode(), e.getMessage(), e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public static BaseResponse handleRuntimeException(RuntimeException e) {
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR, e.getMessage(), "");
    }
}
