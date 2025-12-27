package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select count(*) from dish where category_id = #{id}")
    Integer countByCategoryId(Long id);
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);
    @Delete("delete from dish where id = #{id}")
    void delete(Long id);

    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
}
