package com.wangyang.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wangyang.reggie.common.CustomException;
import com.wangyang.reggie.common.R;
import com.wangyang.reggie.dto.DishDto;
import com.wangyang.reggie.entity.Dish;
import com.wangyang.reggie.entity.DishFlavor;
import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.entity.SetmealDish;
import com.wangyang.reggie.mapper.DishMapper;
import com.wangyang.reggie.service.DishFlavorService;
import com.wangyang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    /*
    * 新增菜品 同时保存对应的口味信息
    * */
    @Autowired
    private DishFlavorService dishFlavorService;

    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存基本信息到菜品表dish
        this.save(dishDto);
        Long id = dishDto.getId();//菜品id

        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dish: flavors) {
            dish.setDishId(id);
        }

        //保存菜品口味信息到口味表
        dishFlavorService.saveBatch(flavors);
    }

    /*
    * 根据id查询菜品信息和对应的口味信息
    * */
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息，从dish表查询
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询当前菜品对应的口味信息，从dish_flavor表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);

        //更新口味表 1.先清理当前口味表 2.添加口味数据
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        dishFlavorService.remove(queryWrapper);


        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor dish: flavors) {
            dish.setDishId(id);
        }
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        if(ids.isEmpty()){
            throw new CustomException("没有可删除的菜品");
        }
        //查询套餐状态，确认是否可以删除
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        queryWrapper.eq(Dish::getStatus,1);
        int count = this.count(queryWrapper);
        //如果不能删除，抛出业务异常
        if (count>0){
            throw new CustomException("菜品正在售卖中，不能删除");
        }

        //如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);
        //删除关系表中的数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
    }


}
