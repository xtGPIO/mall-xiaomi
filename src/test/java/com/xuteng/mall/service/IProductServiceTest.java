package com.xuteng.mall.service;

import com.github.pagehelper.PageInfo;
import com.xuteng.mall.MallApplicationTests;
import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.vo.ProductDetailVo;
import com.xuteng.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by 廖师兄
 */
public class IProductServiceTest extends MallApplicationTests {

	@Autowired
	private IProductService productService;

	@Test
	public void list() {
		ResponseVo<PageInfo> responseVo = productService.list(null, 2, 3);
		Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
	}

	@Test
	public void detail() {
		ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
		Assert.assertEquals(ResponseEnum.SUCCESS.getCode(), responseVo.getStatus());
	}
}