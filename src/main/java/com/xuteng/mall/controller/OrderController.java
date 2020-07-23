package com.xuteng.mall.controller;

import com.github.pagehelper.PageInfo;
import com.xuteng.mall.consts.MallConst;
import com.xuteng.mall.form.OrderCreateForm;
import com.xuteng.mall.pojo.User;
import com.xuteng.mall.service.IOrderService;
import com.xuteng.mall.vo.OrderVo;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by 廖师兄
 */
@RestController
public class OrderController {

	@Autowired
	private IOrderService orderService;

	@PostMapping("/orders")
	public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
									  HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return orderService.create(user.getId(), form.getShippingId());
	}

	@GetMapping("/orders")
	public ResponseVo<PageInfo> list(@RequestParam Integer pageNum,
									 @RequestParam Integer pageSize,
									 HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return orderService.list(user.getId(), pageNum, pageSize);
	}

	@GetMapping("/orders/{orderNo}")
	public ResponseVo<OrderVo> detail(@PathVariable Long orderNo,
									  HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return orderService.detail(user.getId(), orderNo);
	}

	@PutMapping("/orders/{orderNo}")
	public ResponseVo cancel(@PathVariable Long orderNo,
							 HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return orderService.cancel(user.getId(), orderNo);
	}
}
