package com.yxd.yuangongguanli.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yxd.yuangongguanli.constant.UserConstant;
import com.yxd.yuangongguanli.service.service.UserService;
import com.yxd.yuangongguanli.model.domain.User;
import com.yxd.yuangongguanli.mapper.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户方法实现类
 *
 * @author 10186
 * @description 针对表【user(用户)】的数据库操作Service实现
 * @createDate 2024-09-03 17:21:47
 */

@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 颜值,混淆密码
     */
    private static final String SALT = "ame";

    /**
     * 用户登录肽键key
     */


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return -1;
        }
        if (userAccount.length() < 4) {
            return -1;
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            return -1;
        }
        if (planetCode.length() > 5) {
            return -1;
        }
        //账户不包含特殊字符
        String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
        /**
         * 校验用户名
         * @param username
         * @return 校验通过返回true，否则返回false
         */
        if (!java.util.regex.Pattern.matches(REGEX_USERNAME, userAccount)) {
            return -1; //校验不通过
        }
        //校验密码和密码
        if (!userPassword.equals(checkPassword)) {
            return -1;//密码不相同
        }
        //账户不能重复  数据库查询是最后一步,放最后面,可以节约资源
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) { //有人注册了
            return -1;
        }

        //星球编号不能重复  数据库查询是最后一步,放最后面,可以节约资源
        QueryWrapper<User> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.eq("planetCode", planetCode);
        long count_planetCode = userMapper.selectCount(queryWrapper);
        if (count_planetCode > 0) { //有人注册了
            return -1;
        }

        //2,密码加密

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
//        System.out.println(newPassword);
        //3.插入数据
        User user = new User();
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);  //Long 和long
        if (!saveResult)
            return -1;

        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        //1.校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        //账户不包含特殊字符
        String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";
        /**
         * 校验用户名
         * @param username
         * @return 校验通过返回true，否则返回false
         */
        if (!java.util.regex.Pattern.matches(REGEX_USERNAME, userAccount)) {
            return null; //校验不通过
        }
        //2,密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());

        //3,查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            log.info("用户名或密码错误");
            return null;
        }

        /**
         * 1.用户连接服务器端后会获得一个session状态(匿名会话),返回给前对
         * 2.登陆成功后会得到session,并且给该session设置用户信息,同时返回给前段一个cookie命令
         * 3.前段接受后端的cokkie后,保存在浏览器内
         * 4.前端再次请求的时候,携带cookie
         * 5.后端根据cookie,去找对应的session
         * 6.从session中得到存取的用户信息
         */
        //5.返回用户信息脱敏
        User safeUser = getSafeUser(user);
        //4.记录用户登录状态session
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, safeUser);
        return safeUser;
    }


    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafeUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safeUser = new User();
        safeUser.setId(originUser.getId());
        safeUser.setUsername(originUser.getUsername());
        safeUser.setUserAccount(originUser.getUserAccount());
        safeUser.setAvataUrl(originUser.getAvataUrl());
        safeUser.setGender(originUser.getGender());
        safeUser.setPhone(originUser.getPhone());
        safeUser.setEmail(originUser.getEmail());
        safeUser.setUserRole(originUser.getUserRole());
        safeUser.setUserStatus(originUser.getUserStatus());
        safeUser.setCreateTime(originUser.getCreateTime());
        safeUser.setPlanetCode(originUser.getPlanetCode());
        return safeUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
        return 1;
    }

}




