package com.xuteng.mall.vo;

import lombok.Data;

import java.util.List;

/**
 * @ClassName CategoryVo
 * @Description TODO
 * @Author XT
 * @Date 2020/5/31 22:45
 * @Version 1.0
 **/
@Data
public class CategoryVo {
    private Integer id;
    private Integer parenId;
    private String name;
    private Integer sortOrder;
    private List<CategoryVo> subCategories;
}
