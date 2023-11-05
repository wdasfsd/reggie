package com.wangyang.reggie.common;
/*
* 自定义业务异常
*
* */
public class CustomException extends RuntimeException{
    /*
    * 重写父类构造方法
    * 传入message
    * */
    public CustomException(String message){
        super(message);
    }
}
