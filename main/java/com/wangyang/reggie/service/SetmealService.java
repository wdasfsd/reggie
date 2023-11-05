package com.wangyang.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wangyang.reggie.dto.SetmealDto;
import com.wangyang.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    /*
    * 新增套餐，同时保存套餐和菜品的关系
    * */
    public void saveWithDish(SetmealDto setmealDto);

    /*
    * 删除套餐，同时删除套餐和菜品关联数据
    *
    * */
    public void removeWithDish(List<Long> ids);
}
