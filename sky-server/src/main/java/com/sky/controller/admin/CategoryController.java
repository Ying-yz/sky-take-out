package com.sky.controller.admin;

import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@ApiOperation("分类管理")
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    @ApiOperation("分类分页查询")
    public Result<PageResult> page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分类分页查询：{}",categoryPageQueryDTO);
        PageResult pageResult = categoryService.page(categoryPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/list")
    @ApiOperation("查询分类")
    public Result<List<Category>> list(@RequestParam Integer type){
        log.info("查询分类：{}",type);
        List<Category> list = categoryService.list(type);

        return Result.success(list);
    }
    @DeleteMapping
    @ApiOperation("删除分类")
    public Result delete(@RequestParam Long id){
        log.info("删除分类：{}",id);
        categoryService.delete(id);
        return Result.success();
    }
}
