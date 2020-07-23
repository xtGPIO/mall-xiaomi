package com.xuteng.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xuteng.mall.dao.ShippingMapper;
import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.form.ShippingForm;
import com.xuteng.mall.pojo.Shipping;
import com.xuteng.mall.service.IShippingService;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 廖师兄
 */
@Service
public class ShippingServiceImpl implements IShippingService {

	@Autowired
	private ShippingMapper shippingMapper;

	@Override
	public ResponseVo<Map<String, Integer>> add(Integer uid, ShippingForm form) {
		Shipping shipping = new Shipping();
		BeanUtils.copyProperties(form, shipping);
		shipping.setUserId(uid);
		int row = shippingMapper.insertSelective(shipping);
		if (row == 0) {
			return ResponseVo.error(ResponseEnum.ERROR);
		}

		Map<String, Integer> map = new HashMap<>();
		map.put("shippingId", shipping.getId());

		return ResponseVo.success(map);
	}

	@Override
	public ResponseVo delete(Integer uid, Integer shippingId) {
		int row = shippingMapper.deleteByIdAndUid(uid, shippingId);
		if (row == 0) {
			return ResponseVo.error(ResponseEnum.DELETE_SHIPPING_FAIL);
		}

		return ResponseVo.success();
	}

	@Override
	public ResponseVo update(Integer uid, Integer shippingId, ShippingForm form) {
		Shipping shipping = new Shipping();
		BeanUtils.copyProperties(form, shipping);
		shipping.setUserId(uid);
		shipping.setId(shippingId);
		int row = shippingMapper.updateByPrimaryKeySelective(shipping);
		if (row == 0) {
			return ResponseVo.error(ResponseEnum.ERROR);
		}
		return ResponseVo.success();
	}

	@Override
	public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Shipping> shippings = shippingMapper.selectByUid(uid);
		PageInfo pageInfo = new PageInfo(shippings);
		return ResponseVo.success(pageInfo);
	}
}
