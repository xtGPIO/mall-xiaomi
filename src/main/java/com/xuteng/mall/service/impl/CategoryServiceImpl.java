package com.xuteng.mall.service.impl;

import com.xuteng.mall.dao.CategoryMapper;
import com.xuteng.mall.pojo.Category;
import com.xuteng.mall.service.ICategoryService;
import com.xuteng.mall.vo.CategoryVo;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.xuteng.mall.consts.MallConst.ROOT_PARENT_ID;

/**
 * Created by 廖师兄
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

	@Autowired
	private CategoryMapper categoryMapper;

	/**
	 * 耗时：http(请求微信api) > 磁盘 > 内存
	 * mysql(内网+磁盘)
	 * @return
	 */
	@Override
	public ResponseVo<List<CategoryVo>> selectAll() {
		List<Category> categories = categoryMapper.selectAll();

		//查出parent_id=0
//		for (Category category : categories) {
//			if (category.getParentId().equals(ROOT_PARENT_ID)) {
//				CategoryVo categoryVo = new CategoryVo();
//				BeanUtils.copyProperties(category, categoryVo);
//				categoryVoList.add(categoryVo);
//			}
//		}

		//lambda + stream
		//流编程
		List<CategoryVo> categoryVoList = categories.stream()
				//过滤器，选出那些根目录的分类
				.filter(e -> e.getParentId().equals(ROOT_PARENT_ID))
				//类似于机器，从这过必须被它操作一下，这里就是把每个Category对象复制给CategoryVo，并且返回CategoryVo
				//也就是说下面对象都是CategoryVo了
				.map(this::category2CategoryVo)
				//可以看出是排序，使用SortOrder排序，而且是逆序
				.sorted(Comparator.comparing(CategoryVo::getSortOrder).reversed())
				//最后元素形成集合
				.collect(Collectors.toList());

		//查询子目录
		findSubCategory(categoryVoList, categories);

		return ResponseVo.success(categoryVoList);
	}
	//商品服务用的，等会在解读
	@Override
	public void findSubCategoryId(Integer id, Set<Integer> resultSet) {
		List<Category> categories = categoryMapper.selectAll();
		findSubCategoryId(id, resultSet, categories);
	}

	private void findSubCategoryId(Integer id, Set<Integer> resultSet, List<Category> categories) {
		for (Category category : categories) {
			if (category.getParentId().equals(id)) {
				resultSet.add(category.getId());
				findSubCategoryId(category.getId(), resultSet, categories);
			}
		}
	}


	private void findSubCategory(List<CategoryVo> categoryVoList, List<Category> categories) {
		//遍历前端根目录
		for (CategoryVo categoryVo : categoryVoList) {
			//每个前端根目录都有不止一个前端子目录，所以新建列表
			List<CategoryVo> subCategoryVoList = new ArrayList<>();
			//遍历所有的结果
			for (Category category : categories) {
				//如果查到ParentId与根目录ID相同的子目录，复制给前端子目录subCategoryVo，然后加入列表
				// 设置subCategory, 继续往下查
				if (categoryVo.getId().equals(category.getParentId())) {
					CategoryVo subCategoryVo = category2CategoryVo(category);
					subCategoryVoList.add(subCategoryVo);
				}
				//是排序，使用SortOrder排序，而且是逆序
				subCategoryVoList.sort(Comparator.comparing(CategoryVo::getSortOrder).reversed());
				//前端子目录加入前端根目录
				categoryVo.setSubCategories(subCategoryVoList);
				//同时利用递归再查这个子目录的子目录，直到查空为止
				findSubCategory(subCategoryVoList, categories);
			}
		}
	}

	private CategoryVo category2CategoryVo(Category category) {
		CategoryVo categoryVo = new CategoryVo();
		BeanUtils.copyProperties(category, categoryVo);
		return categoryVo;
	}
}
