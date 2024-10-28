package com.yxd.yuangongguanli;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootTest
class YuangongguanliApplicationTests {

    @Test
    void Digest() throws NoSuchAlgorithmException {
        String newPassword = DigestUtils.md5DigestAsHex(("abcd"+"mypassword").getBytes());
        System.out.println(newPassword);
    }

    @Test
    void contextLoads() {
    }

}
