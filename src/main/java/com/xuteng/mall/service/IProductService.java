package com.xuteng.mall.service;

import com.github.pagehelper.PageInfo;
import com.xuteng.mall.vo.ProductDetailVo;
import com.xuteng.mall.vo.ResponseVo;

/**
 * Created by 廖师兄
 */
public interface IProductService {

	ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);

	ResponseVo<ProductDetailVo> detail(Integer productId);
}
