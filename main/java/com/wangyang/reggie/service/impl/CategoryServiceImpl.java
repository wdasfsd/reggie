package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.common.CustomException;
import com.wangyang.reggie.entity.Category;
import com.wangyang.reggie.entity.Dish;
import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.mapper.CategoryMapper;
import com.wangyang.reggie.service.CategoryService;
import com.wangyang.reggie.service.DishService;
import com.wangyang.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /*
    * 根据id删除分类，删除之前需要进行判断
    *
    * */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联了菜品，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);//如果dish表中的创建id和菜品分类id相同，就说明改分类有菜品
        int count1 = dishService.count(dishLambdaQueryWrapper);
        if(count1>0){
            //已经关联菜品，抛出业务异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }


        //查询当前分类是否关联了套餐，如果已经关联，抛出一个业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);//如果dish表中的创建id和菜品分类id相同，就说明改分类有菜品
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if(count2>0){
            //已经关联套餐，抛出业务异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }

        //正常删除分类
        super.removeById(id);
    }
}
