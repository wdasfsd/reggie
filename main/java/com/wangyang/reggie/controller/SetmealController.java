package com.wangyang.reggie.controller;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangyang.reggie.common.R;
import com.wangyang.reggie.dto.SetmealDto;
import com.wangyang.reggie.entity.Category;
import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.service.CategoryService;
import com.wangyang.reggie.service.SetmealDishService;
import com.wangyang.reggie.service.SetmealService;
import jdk.jfr.internal.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.rmi.CORBA.Util;
import java.util.ArrayList;
import java.util.List;

/*
*
* 套餐管理
* */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;
    /*
    * 新增套餐
    * */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){


        setmealService.saveWithDish(setmealDto);

        return R.success("新增套餐成功");
    }

    /*
    * 套餐分页查询
    * */
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        //分页构造器
        Page<Setmeal> setmealPage = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage=new Page<>();

        //查询构造器
        LambdaQueryWrapper<Setmeal> queryWrapper =new LambdaQueryWrapper<>();
        //添加查询条件,根据name进行like查询
        queryWrapper.like(name!=null,Setmeal::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(setmealPage,dtoPage,"records");
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> list=new ArrayList<>();
        for (Setmeal st:records) {
            SetmealDto setmealDto=new SetmealDto();
            //对象拷贝
            BeanUtils.copyProperties(st,setmealDto);
            //分类id
            Long categoryId = st.getCategoryId();
            //根据分类id查询分类对象
            Category category = categoryService.getById(categoryId);
            if(category!=null){
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            list.add(setmealDto);
        }
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }
    /*
    * 修改套餐状态为停售
    * */
    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids){
        log.info("ids{}",ids);
        UpdateWrapper<Setmeal> updateWrapper=new UpdateWrapper<>();
        updateWrapper.in("id",ids);
        updateWrapper.set("status",0);
        setmealService.update(updateWrapper);
        return R.success("状态更新成功");
    }

    /*
     * 修改套餐状态为启售
     * */
    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids){
        log.info("ids{}",ids);
        UpdateWrapper<Setmeal> updateWrapper=new UpdateWrapper<>();
        updateWrapper.in("id",ids);
        updateWrapper.set("status",1);
        setmealService.update(updateWrapper);
        return R.success("状态更新成功");
    }







    /*
    * 删除套餐
    *
    * */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("delete");
        log.info("ids{}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐数据删除成功");
    }
    /*
    * 根据套餐分类查询套餐
    *
    * */
    @GetMapping("/list")
    public R<List<Setmeal>>list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(queryWrapper);
        return R.success(list);
    }


}