package com.salary.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.salary.system.entity.User;
import com.salary.system.mapper.UserMapper;
import com.salary.system.util.ResponseResult;
import com.salary.system.util.SM3Util;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * 用户管理控制器
 */
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserMapper userMapper;

    /**
     * 密码复杂度正则：至少8位，包含数字、大小写字母和特殊字符
     */
    private static final Pattern PASSWORD_PATTERN =
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,}$");

    /**
     * 用户管理页面
     */
    @GetMapping("")
    public String index() {
        return "user/index";
    }

    /**
     * 用户列表
     */
    @GetMapping("/list")
    @ResponseBody
    public ResponseResult<IPage<User>> list(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           String username) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if (username != null && !username.trim().isEmpty()) {
            queryWrapper.like(User::getUsername, username);
        }

        IPage<User> page = new Page<>(current, size);
        page = userMapper.selectPage(page, queryWrapper);

        return ResponseResult.success(page);
    }

    /**
     * 添加用户页面
     */
    @GetMapping("/add")
    public String add() {
        return "user/add";
    }

    /**
     * 添加用户
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseResult<Void> doAdd(User user, String passwordConfirm) {
        // 参数校验
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseResult.error("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            return ResponseResult.error("密码不能为空");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return ResponseResult.error("角色不能为空");
        }

        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        if (userMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("用户名已存在");
        }

//        // 检查密码是否一致
//        if (!user.getPassword().equals(passwordConfirm)) {
//            return ResponseResult.error("两次输入的密码不一致");
//        }

        // 检查密码复杂度
        if (!PASSWORD_PATTERN.matcher(user.getPassword()).matches()) {
            return ResponseResult.error("密码不符合复杂度要求，至少8位，必须包含数字、大小写字母和特殊字符");
        }

        // 密码加密
        user.setPassword(SM3Util.encrypt(user.getPassword()));
        user.setLastPasswordResetDate(new Date());
        user.setLoginFailCount(0);
        user.setStatus(1);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        // 保存用户
        userMapper.insert(user);

        return ResponseResult.success();
    }

    /**
     * 编辑用户页面
     */
    @GetMapping("/edit/{id}")
    @ResponseBody
    public ResponseResult edit(@PathVariable Integer id, Model model) {
        User user = userMapper.selectById(id);
        model.addAttribute("user", user);
        return ResponseResult.success(user);
    }

    /**
     * 编辑用户
     */
    @PostMapping("/edit")
    @ResponseBody
    public ResponseResult<Void> doEdit(User user) {
        // 参数校验
        if (user.getId() == null) {
            return ResponseResult.error("用户ID不能为空");
        }
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return ResponseResult.error("用户名不能为空");
        }
        if (user.getRealName() == null || user.getRealName().trim().isEmpty()) {
            return ResponseResult.error("姓名不能为空");
        }
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            return ResponseResult.error("角色不能为空");
        }

        // 检查用户是否存在
        User existUser = userMapper.selectById(user.getId());
        if (existUser == null) {
            return ResponseResult.error("用户不存在");
        }

        // 检查用户名是否已被其他用户使用
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername())
                   .ne(User::getId, user.getId());
        if (userMapper.selectCount(queryWrapper) > 0) {
            return ResponseResult.error("用户名已存在");
        }

        // 设置不需要修改的字段
        user.setPassword(existUser.getPassword());
        user.setLastPasswordResetDate(existUser.getLastPasswordResetDate());
        user.setLoginFailCount(existUser.getLoginFailCount());
        user.setLockedTime(existUser.getLockedTime());
        user.setCreateTime(existUser.getCreateTime());
        user.setUpdateTime(new Date());

        // 更新用户
        userMapper.updateById(user);

        return ResponseResult.success();
    }

    /**
     * 删除用户
     */
    @PostMapping("/delete/{id}")
    @ResponseBody
    public ResponseResult<Void> delete(@PathVariable Integer id, HttpSession session) {
        // 检查是否删除自己
        User currentUser = (User) session.getAttribute("user");
        if (currentUser.getId().equals(id)) {
            return ResponseResult.error("不能删除自己");
        }

        userMapper.deleteById(id);
        return ResponseResult.success();
    }

    /**
     * 重置密码页面
     */
    @GetMapping("/reset/{id}")
    public String reset(@PathVariable Integer id, Model model) {
        User user = userMapper.selectById(id);
        model.addAttribute("user", user);
        return "user/reset";
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset")
    @ResponseBody
    public ResponseResult<Void> doReset(Integer id, String password, String passwordConfirm) {
        // 参数校验
        if (id == null) {
            return ResponseResult.error("用户ID不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseResult.error("密码不能为空");
        }

        // 检查用户是否存在
        User user = userMapper.selectById(id);
        if (user == null) {
            return ResponseResult.error("用户不存在");
        }


        // 检查密码复杂度
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ResponseResult.error("密码不符合复杂度要求，至少8位，必须包含数字、大小写字母和特殊字符");
        }

        // 密码加密
        user.setPassword(SM3Util.encrypt(password));
        user.setLastPasswordResetDate(new Date());
        user.setUpdateTime(new Date());

        // 更新用户
        userMapper.updateById(user);

        return ResponseResult.success();
    }

    /**
     * 修改密码页面
     */
    @GetMapping("/password")
    public String password() {
        return "user/password";
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    @ResponseBody
    public ResponseResult<Void> doPassword(String oldPassword, String newPassword, String confirmPassword,
                                          HttpSession session) {
        // 参数校验
        if (oldPassword == null || oldPassword.trim().isEmpty()) {
            return ResponseResult.error("原密码不能为空");
        }
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseResult.error("新密码不能为空");
        }

        // 检查密码是否一致
        if (!newPassword.equals(confirmPassword)) {
            return ResponseResult.error("两次输入的新密码不一致");
        }

        // 检查密码复杂度
        if (!PASSWORD_PATTERN.matcher(newPassword).matches()) {
            return ResponseResult.error("密码不符合复杂度要求，至少8位，必须包含数字、大小写字母和特殊字符");
        }

        // 获取当前用户
        User user = (User) session.getAttribute("user");

        // 验证原密码
        if (!SM3Util.encrypt(oldPassword).equals(user.getPassword())) {
            return ResponseResult.error("原密码错误");
        }

        // 修改密码
        user.setPassword(SM3Util.encrypt(newPassword));
        user.setLastPasswordResetDate(new Date());
        user.setUpdateTime(new Date());

        // 更新用户
        userMapper.updateById(user);

        // 更新会话中的用户信息
        session.setAttribute("user", user);

        return ResponseResult.success();
    }
}
