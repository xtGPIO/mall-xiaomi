package com.xuteng.mall.service;

import com.github.pagehelper.PageInfo;
import com.xuteng.mall.form.ShippingForm;
import com.xuteng.mall.vo.ResponseVo;

import java.util.Map;

/**
 * Created by 廖师兄
 */
public interface IShippingService {

	ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form);

	ResponseVo delete(Integer uid, Integer shippingId);

	ResponseVo update(Integer uid, Integer shippingId, ShippingForm form);

	ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize);
}
