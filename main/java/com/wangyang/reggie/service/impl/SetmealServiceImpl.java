package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.common.CustomException;
import com.wangyang.reggie.dto.SetmealDto;
import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.entity.SetmealDish;
import com.wangyang.reggie.mapper.SetmealMapper;
import com.wangyang.reggie.service.SetmealDishService;
import com.wangyang.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);
        //保存套餐和菜品的关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();//取出管理关系集合
        Long id = setmealDto.getId();
        //取出每一条数据，然后修改起套餐id
        for (SetmealDish sh: setmealDishes) {
            sh.setSetmealId(id);
        }


        //保存套餐和菜品的关联关系
        setmealDishService.saveBatch(setmealDishes);


    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        if(ids.isEmpty()){
            throw new CustomException("没有可删除的套餐");
        }
        //查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(queryWrapper);
        //如果不能删除，抛出业务异常
        if (count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);
    }
}
