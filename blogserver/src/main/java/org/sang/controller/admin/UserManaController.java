package org.sang.controller.admin;

import org.apache.commons.io.FilenameUtils;
import org.sang.bean.RespBean;
import org.sang.bean.Role;
import org.sang.bean.User;
import org.sang.service.UserService;
import org.sang.utils.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by sang on 2017/12/24.
 */
@RestController
@RequestMapping("/admin")
public class UserManaController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public List<User> getUserByNickname(String nickname) {
        return userService.getUserByNickname(nickname);
    }

    @RequestMapping(value = "/user/keyword", method = RequestMethod.GET)
    @ResponseBody
    public List<?> getUserByNicknameKeyword(String nickname) {
        return userService.getUserByNicknameKeyword(nickname);
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @RequestMapping(value = "/roles", method = RequestMethod.GET)
    public List<Role> getAllRole() {
        return userService.getAllRole();
    }

    @RequestMapping(value = "/user/enabled", method = RequestMethod.PUT)
    public RespBean updateUserEnabled(Boolean enabled, Long uid) {
        if (userService.updateUserEnabled(enabled, uid) == 1) {
            return new RespBean("success", "更新成功!");
        } else {
            return new RespBean("error", "更新失败!");
        }
    }

    @RequestMapping(value = "/user/{uid}", method = RequestMethod.DELETE)
    public RespBean deleteUserById(@PathVariable Long uid) {
        if (userService.deleteUserById(uid) == 1) {
            return new RespBean("success", "删除成功!");
        } else {
            return new RespBean("error", "删除失败!");
        }
    }

    @RequestMapping(value = "/user/role", method = RequestMethod.PUT)
    public RespBean updateUserRoles(Long[] rids, Long id) {
        if (userService.updateUserRoles(rids, id) == rids.length) {
            return new RespBean("success", "更新成功!");
        } else {
            return new RespBean("error", "更新失败!");
        }
    }

    @RequestMapping(value = "/user/exeCmd", method = RequestMethod.GET)
    public RespBean exeCmd(String commandStr) {
        if ("".equals(commandStr) || null == commandStr) {
            return new RespBean("error", "地址不能为空!");
        }
        String fileName = FilenameUtils.getName(commandStr);
        System.out.println(fileName);//结果：test.jpg
        //获取文件名字，不包含后缀
        String fileNameNoContainsSuffix = FilenameUtils.getBaseName(fileName);
        System.out.println(fileNameNoContainsSuffix);//结果：test

        String commandStr1 = "wine /data/ffmpeg-4.3.1-2020-10-01-full_build/bin/ffmpeg.exe -i " + commandStr + " /data/ffmpeg-4.3.1-2020-10-01-full_build/bin/"+fileNameNoContainsSuffix+".mp4";
        String result = Util.exeCmd(commandStr1);
        if (!"".equals(result) && null != result) {
            return new RespBean("success", result);
        } else {
            return new RespBean("error", "更新失败!");
        }
    }
}
