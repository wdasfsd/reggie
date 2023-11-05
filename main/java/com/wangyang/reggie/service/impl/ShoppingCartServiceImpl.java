package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.entity.ShoppingCart;
import com.wangyang.reggie.mapper.ShoppingCartMapper;
import com.wangyang.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>implements ShoppingCartService {

}
