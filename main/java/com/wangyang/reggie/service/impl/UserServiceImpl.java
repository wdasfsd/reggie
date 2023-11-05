package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.entity.User;
import com.wangyang.reggie.mapper.UserMapper;
import com.wangyang.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>implements UserService {
}
