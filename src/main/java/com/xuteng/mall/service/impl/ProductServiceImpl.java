package com.xuteng.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuteng.mall.dao.ProductMapper;
import com.xuteng.mall.pojo.Product;
import com.xuteng.mall.service.ICategoryService;
import com.xuteng.mall.service.IProductService;
import com.xuteng.mall.vo.ProductDetailVo;
import com.xuteng.mall.vo.ProductVo;
import com.xuteng.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xuteng.mall.enums.ProductStatusEnum.*;
import static com.xuteng.mall.enums.ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE;

/**
 * Created by 廖师兄
 */
@Service
@Slf4j
public class ProductServiceImpl implements IProductService {

	@Autowired
	private ICategoryService categoryService;

	@Autowired
	private ProductMapper productMapper;

	@Override
	public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
		Set<Integer> categoryIdSet = new HashSet<>();
		//查出categoryId子目录下的所有子categoryId，并加入set中
		if (categoryId != null) {
			categoryService.findSubCategoryId(categoryId, categoryIdSet);
			categoryIdSet.add(categoryId);
		}
		//pagenum是显示第几页的内容，pagesize是一页多少条数据
		PageHelper.startPage(pageNum, pageSize);
		//将所有子目录下的商品都查出来
		List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
		//将所有的Product类变为productVo类
		List<ProductVo> productVoList = productList.stream()
				.map(e -> {
					ProductVo productVo = new ProductVo();
					BeanUtils.copyProperties(e, productVo);
					return productVo;
				})
				.collect(Collectors.toList());
		/**
		 * 让productList 而不是 productVoList 作为构造函数的参数是因为,
		 * 与数据库交互的是productList, 而不是productVoList !!
		 * 所以, 把productList传过去,
		 * 就可以对获取productList要执行的sql (监听?拦截?修改?),
		 * 先查询count,再计算limit的参数, 最后生成分页sql执行
		 */
		PageInfo pageInfo = new PageInfo<>(productList);
		//到时候查一下，有疑问？为啥要set一下
		pageInfo.setList(productVoList);
		return ResponseVo.success(pageInfo);
	}
	//查询商品细节
	@Override
	public ResponseVo<ProductDetailVo> detail(Integer productId) {
		Product product = productMapper.selectByPrimaryKey(productId);

		//只对确定性条件判断
		//如果查出商品状态异常，就返回error
		if (product.getStatus().equals(OFF_SALE.getCode())
				|| product.getStatus().equals(DELETE.getCode())) {
			return ResponseVo.error(PRODUCT_OFF_SALE_OR_DELETE);
		}

		ProductDetailVo productDetailVo = new ProductDetailVo();
		BeanUtils.copyProperties(product, productDetailVo);
		//敏感数据处理，超过100就显示100，少于100就显示实际库存
		productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());
		return ResponseVo.success(productDetailVo);
	}
}
