package com.xuteng.mall.service;

import com.xuteng.mall.form.CartAddForm;
import com.xuteng.mall.form.CartUpdateForm;
import com.xuteng.mall.pojo.Cart;
import com.xuteng.mall.vo.CartVo;
import com.xuteng.mall.vo.ResponseVo;

import java.util.List;

/**
 * Created by 廖师兄
 */
public interface ICartService {

	ResponseVo<CartVo> add(Integer uid, CartAddForm form);

	ResponseVo<CartVo> list(Integer uid);

	ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form);

	ResponseVo<CartVo> delete(Integer uid, Integer productId);

	ResponseVo<CartVo> selectAll(Integer uid);

	ResponseVo<CartVo> unSelectAll(Integer uid);

	ResponseVo<Integer> sum(Integer uid);

	List<Cart> listForCart(Integer uid);
}
