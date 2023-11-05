package com.wangyang.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangyang.reggie.entity.OrderDetail;
import com.wangyang.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersDetailMapper extends BaseMapper<OrderDetail> {
}
