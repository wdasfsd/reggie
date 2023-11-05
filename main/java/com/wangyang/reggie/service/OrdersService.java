package com.wangyang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangyang.reggie.entity.Employee;
import com.wangyang.reggie.entity.Orders;

public interface OrdersService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);
}
