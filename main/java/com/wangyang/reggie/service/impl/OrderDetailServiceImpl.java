package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.entity.OrderDetail;
import com.wangyang.reggie.entity.Orders;
import com.wangyang.reggie.mapper.OrdersDetailMapper;
import com.wangyang.reggie.mapper.OrdersMapper;
import com.wangyang.reggie.service.OrderDetailService;
import com.wangyang.reggie.service.OrdersService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrdersDetailMapper, OrderDetail> implements OrderDetailService {
}
