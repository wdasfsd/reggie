package com.wangyang.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wangyang.reggie.common.R;
import com.wangyang.reggie.entity.Employee;
import com.wangyang.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;
    /*
    * 员工登录
    *
    * */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //1.将页面提交的密码进行md5加密
        String password = employee.getPassword();
        password=DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到则返回登录失败结果
        if(emp==null) {
            return R.error("登录失败");
        }

        //4.密码比对.如果不一致则返回登录失败结果

        if(!emp.getPassword().equals(password)){
            return R.error("密码错误");
        }

        //5.查看员工状态

        if(emp.getStatus()==0){
            return R.error("账户已禁用");
        }

        //6.登录成功。将员工id存储session并返回登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }
    /*
    *
    * 员工退出
    *
    * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /*
    * 新增员工
    * */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工 ，员工信息{}",employee.toString());
        //设置初始密码123456
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));


        //employee.setCreateTime(LocalDateTime.now());//创建时间
        //employee.setUpdateTime(LocalDateTime.now());//修改时间
        //获取当前登录用户id
        //Long empid=(Long)request.getSession().getAttribute("employee");
        //employee.setCreateUser(empid);//创建用户
        //employee.setUpdateUser(empid);//修改用户
        employeeService.save(employee);
        return R.success("新增员工成功");
    }


    /*
    * 员工信息分页查询
    * */
    @GetMapping("/page")
    public R<Page> page(Integer page,Integer pageSize,String name){
        log.info("page={},pagesize{},name={}",page,pageSize,name);
        //分页构造器
        Page<Employee> pageInfo =new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤查询器
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    /*
    * 根据id修改员工信息
    *
    * */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){
        log.info(employee.toString());

        //Long empID =(Long)request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());//更新时间
        //employee.setUpdateUser(empID);//更新用户
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /*
    *根据id查询员工信息
    *
    * */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee byId = employeeService.getById(id);
        if(byId!=null){
            return R.success(byId);
        }
        return R.error("没有查询到对应员工信息");
    }
}