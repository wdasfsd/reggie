package com.wangyang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangyang.reggie.common.R;
import com.wangyang.reggie.dto.DishDto;
import com.wangyang.reggie.entity.Category;
import com.wangyang.reggie.entity.Dish;
import com.wangyang.reggie.entity.DishFlavor;
import com.wangyang.reggie.service.CategoryService;
import com.wangyang.reggie.service.DishFlavorService;
import com.wangyang.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/*
* 菜品管理
* */
@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;

    /*
    * 新增菜品
    * */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    /*
    * 菜品信息分页查询
    * */


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize ,String name){
        //构造分页构造器
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage=new Page<>();
       //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null,Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        dishService.page(pageInfo,queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list=new ArrayList<>();
        for (Dish d :records) {
            DishDto dishDto=new DishDto();//new出来的dishdto没有其他属性
            BeanUtils.copyProperties(d,dishDto);//需要把属性加入到上面去
            Long categoryId = d.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id查询分类
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            list.add(dishDto);
        }

        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);
    }
    /*
    * 根据id查询菜品信息和对应的口味信息
    * */

    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.updateWithFlavor(dishDto);

        return R.success("修改菜品成功");
    }

    /*
    * 根据条件查询对应菜品数据
    * */

    /*@GetMapping("/list")
    public R<List<Dish>> list(Dish dish){
        log.info(dish.getCategoryId().toString());
        //条件构造器
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        return R.success(list);
    }*/


    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        log.info(dish.getCategoryId().toString());
        //条件构造器
        LambdaQueryWrapper<Dish>queryWrapper=new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> list = dishService.list(queryWrapper);
        List<DishDto>dishDtoList=new ArrayList<>();
        for (Dish d : list) {
            DishDto dishDto=new DishDto();//new出来的dishdto没有其他属性
            BeanUtils.copyProperties(d,dishDto);//需要把属性加入到上面去
            Long categoryId = d.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);//根据id查询分类
            String categoryName = category.getName();
            //查询口味信息
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,d.getId());
            List<DishFlavor> list1 = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(list1);

            dishDto.setCategoryName(categoryName);
            dishDtoList.add(dishDto);
        }


        return R.success(dishDtoList);
    }



    /*
     * 修改菜品状态为停售
     * */
    @PostMapping("/status/0")
    public R<String> status0(@RequestParam List<Long> ids){
        log.info("ids{}",ids);
        UpdateWrapper<Dish> updateWrapper=new UpdateWrapper<>();
        updateWrapper.in("id",ids);
        updateWrapper.set("status",0);
        dishService.update(updateWrapper);
        return R.success("状态更新成功");
    }

    /*
     * 修改菜品状态为启售
     * */
    @PostMapping("/status/1")
    public R<String> status1(@RequestParam List<Long> ids){
        log.info("ids{}",ids);
        UpdateWrapper<Dish> updateWrapper=new UpdateWrapper<>();
        updateWrapper.in("id",ids);
        updateWrapper.set("status",1);
        dishService.update(updateWrapper);
        return R.success("状态更新成功");
    }


    /*
     * 删除菜品
     *
     * */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("delete");
        log.info("ids{}",ids);
        dishService.removeWithDish(ids);
        return R.success("菜品数据删除成功");
    }

}
