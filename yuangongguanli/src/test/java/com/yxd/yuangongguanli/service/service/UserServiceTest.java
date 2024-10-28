package com.yxd.yuangongguanli.service.service;
import java.util.Date;

import com.yxd.yuangongguanli.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;


/**
 * 用户测试
 *
 * @author ame
 */
@SpringBootTest
class UserServiceTest  {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser(){
        User user = new User();
        user.setId(0L);
        user.setUsername("ame");
        user.setUserAccount("123");
        user.setAvataUrl("");
        user.setGender(0);
        user.setUserPassword("123");
        user.setPhone("123");
        user.setEmail("123");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);


        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yxd123";
        String userPassword = "123456789";
        String checkPassword = "123456789";
        String planetCode = "1";
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        System.out.println(result);
        Assertions.assertEquals(-1, result);
    }
}