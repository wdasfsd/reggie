package com.wangyang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangyang.reggie.common.R;
import com.wangyang.reggie.entity.Category;
import com.wangyang.reggie.entity.Employee;
import com.wangyang.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /*
    * 添加套餐或者菜品
    * */

    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("添加成功");
    }

    /*
     * 菜品信息分页查询
     * */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize){
        log.info("page={},pagesize={}",page,pageSize);
        log.info(page.toString(),pageSize);
        //分页构造器
        Page<Category> pageInfo =new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //添加排序条件
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }
    /*
    * 删除对应的分类
    * */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除分类,id为：{}",ids);

        //categoryService.removeById(id);
        categoryService.remove(ids);
        return R.success("分类信息删除成功");
    }

    /*
    * 根据id修改分类信息
    * */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息{}",category);
        categoryService.updateById(category);
        return R.success("修改分类信息成功");
    }

    /*
    * 根据条件查询分类数据
    *
    * */

    @GetMapping("/list")
    public R<List<Category>> list(Category category){
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        queryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序跳进
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        //查询
        List<Category> list = categoryService.list(queryWrapper);
        //返回数据
        return R.success(list);
    }
}
