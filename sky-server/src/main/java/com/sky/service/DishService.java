package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

public interface DishService {
    void insert(DishDTO dishDTO);

    PageResult page(DishPageQueryDTO dishPageQueryDTO);
}
