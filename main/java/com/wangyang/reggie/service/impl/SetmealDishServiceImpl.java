package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.entity.SetmealDish;
import com.wangyang.reggie.mapper.SetmealDishMapper;
import com.wangyang.reggie.mapper.SetmealMapper;
import com.wangyang.reggie.service.SetmealDishService;
import com.wangyang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
