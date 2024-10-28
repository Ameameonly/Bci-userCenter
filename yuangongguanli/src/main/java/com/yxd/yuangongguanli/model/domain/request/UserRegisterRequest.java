package com.yxd.yuangongguanli.model.domain.request;


import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 * Serializable 接口，实现序列化
 */

@Data//生成getter setter方法
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;
    private String userPassword ;
    private String checkPassword;
    private String planetCode;

}
