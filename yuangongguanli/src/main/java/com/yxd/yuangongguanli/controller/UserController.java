package com.yxd.yuangongguanli.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yxd.yuangongguanli.common.BaseResponse;
import com.yxd.yuangongguanli.common.ErrorCode;
import com.yxd.yuangongguanli.common.ResultUtils;
import com.yxd.yuangongguanli.constant.UserConstant;
import com.yxd.yuangongguanli.exception.BusinessException;
import com.yxd.yuangongguanli.model.domain.User;
import com.yxd.yuangongguanli.model.domain.request.UserLoginRequest;
import com.yxd.yuangongguanli.model.domain.request.UserRegisterRequest;
import com.yxd.yuangongguanli.service.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.yxd.yuangongguanli.constant.UserConstant.ADMIN_ROLE;
import static com.yxd.yuangongguanli.constant.UserConstant.USER_LOGIN_STATE;


/**
 * Controller层,把接口封装成请求
 */
@CrossOrigin(origins = "http://118.31.221.188")
@RestController //适用于编写Restful的api,返回值为json
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;


    @PostMapping("/register") //注册
    public BaseResponse<Long> UserRegister(@RequestBody UserRegisterRequest userRegisterRequest) {//json封装成一个对象
        if (userRegisterRequest == null) {
            return null;
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {//判断是否为空,多加一层校验,保险.
            return null;
        }
        long l = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(l);

    }

    @PostMapping("/login") //登录
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {//json封装成一个对象
        if (userLoginRequest == null) {
            return null;
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {//判断是否为空,多加一层校验,保险.
            return null;
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout") //登录
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {//json封装成一个对象
        if (request == null) {
            return null;
        }
        Integer i = userService.userLogout(request);
        return ResultUtils.success(i);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            return null;
        }
        long userId = currentUser.getId();
        // todo 校验用户是否合法
        User user = userService.getById(userId);
        User safeUser = userService.getSafeUser(user);
        return ResultUtils.success(safeUser);
    }


    //下面是用户管理接口,必须具有管理员权限
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {

        //仅管理员可以查询
        if (!isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH, "无权限");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);//模糊查询,用like
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> {
            return userService.getSafeUser(user);
        }).collect(Collectors.toList());
        return ResultUtils.success(list);
    }



    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!isAdmin(request)) {
            return null;
        }
        if (id <= 0) {
            return null;
        }
        boolean b = userService.removeById(id);//逻辑删除,
        return ResultUtils.success(b);
    }


    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    private boolean isAdmin(HttpServletRequest request) {
        //仅管理员可以查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

}

//终 12.21
