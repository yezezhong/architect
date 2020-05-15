package com.chloe.controller;

import com.chloe.common.utils.CookieUtils;
import com.chloe.common.utils.JsonResult;
import com.chloe.common.utils.JsonUtils;
import com.chloe.model.bo.UserBo;
import com.chloe.model.pojo.Users;
import com.chloe.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

@Api(value = "注册登录", tags = {"用于注册登录的接口"})
@RestController
@RequestMapping("passport")
public class PassportController {

    @Resource
    private UserService userService;

    @GetMapping("usernameIsExist")
    @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
    public JsonResult usernameIsExist(@RequestParam String username) {
        if (StringUtils.isBlank(username)) {
            return JsonResult.errorMsg("用户名不能为空");
        }

        boolean isExist = userService.queryUsernameIsExist(username);

        return isExist ? JsonResult.errorMsg("用户名已存在") : JsonResult.ok();
    }

    @PostMapping("register")
    @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
    public JsonResult register(@RequestBody UserBo userBo, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isBlank(userBo.getUsername()) || StringUtils.isBlank(userBo.getPassword())
                || StringUtils.isBlank(userBo.getConfirmPassword())) {
            return JsonResult.errorMsg("用户名或密码不能为空");
        }

        if (userBo.getPassword().length() < 6) {
            return JsonResult.errorMsg("密码长度不能小于6");
        }

        if (!userBo.getPassword().equals(userBo.getConfirmPassword())) {
            return JsonResult.errorMsg("两次输入的密码不一致");
        }

        boolean isExist = userService.queryUsernameIsExist(userBo.getUsername());

        if (isExist) {
            return JsonResult.errorMsg("用户名已存在");
        }

        Users user = userService.createUser(userBo);

        Users maskUser = doMask(user);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(maskUser), true);

        return JsonResult.ok(maskUser);
    }

    @PostMapping("login")
    @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
    public JsonResult login(@RequestBody UserBo userBo, HttpServletRequest request,
                            HttpServletResponse response) {
        if (StringUtils.isBlank(userBo.getUsername()) || StringUtils.isBlank(userBo.getPassword())) {
            return JsonResult.errorMsg("用户名或密码不能为空");
        }

        Users users = userService.queryUserForLogin(userBo);

        if (Objects.isNull(users)) {
            return JsonResult.errorMsg("用户名或密码不正确");
        }

        Users maskUser = doMask(users);

        CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(maskUser), true);

        return JsonResult.ok(doMask(maskUser));
    }

    @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
    @PostMapping("logout")
    public JsonResult logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "user");

        return JsonResult.ok();
    }

    private Users doMask(Users origin) {
        origin.setPassword(null);
        origin.setMobile(null);
        origin.setCreatedTime(null);
        origin.setUpdatedTime(null);
        origin.setEmail(null);

        return origin;
    }
}