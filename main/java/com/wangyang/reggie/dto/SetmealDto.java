package com.wangyang.reggie.dto;

import com.wangyang.reggie.entity.Setmeal;
import com.wangyang.reggie.entity.SetmealDish;
import com.wangyang.reggie.entity.Setmeal;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
