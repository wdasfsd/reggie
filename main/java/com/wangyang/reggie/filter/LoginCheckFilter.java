package com.wangyang.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.wangyang.reggie.common.BaseContext;
import com.wangyang.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
* 检查用户是否已经登录,如果用户不登录就拦截针对controller的请求
*
* */
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配，支持通配符
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request=(HttpServletRequest)servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        //1.获取本次请求的url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求路径: {}",requestURI);
        //放行的url
        String[] urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"

        };

        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        //3.如果不需要处理则放行
        if(check){
            filterChain.doFilter(request,response);
            log.info("不需要处理到请求路径: {}",requestURI);
            return;
        }

        //4-1.判断登录状态，如果已经登录则直接放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("用户已登录");
            Long id =(Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(id);//设置同线程id

            filterChain.doFilter(request,response);
            return;
        }
        //4-2.判断移动端登录状态，如果已经登录则直接放行
        if(request.getSession().getAttribute("user")!=null){
            log.info("用户已登录");
            Long userid =(Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userid);//设置同线程id

            filterChain.doFilter(request,response);
            return;
        }

        //5.如果未登录则返回未登录结果，通过输出流返回客户端相应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }
    /*
    * 路径匹配，检查本次请求是否需要放行
    * */
    public boolean check(String[] urls ,String requestURI){
        for (String url : urls) {
            if(PATH_MATCHER.match(url,requestURI)){
               return true;
            }
        }
        return false;
    }
}
