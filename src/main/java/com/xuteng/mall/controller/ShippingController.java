package com.xuteng.mall.controller;

import com.xuteng.mall.consts.MallConst;
import com.xuteng.mall.form.ShippingForm;
import com.xuteng.mall.pojo.User;
import com.xuteng.mall.service.IShippingService;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * Created by 廖师兄
 */
@RestController
public class ShippingController {

	@Autowired
	private IShippingService shippingService;

	@PostMapping("/shippings")
	public ResponseVo add(@Valid @RequestBody ShippingForm form,
						  HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return shippingService.add(user.getId(), form);
	}
	//通过 @PathVariable 可以将 URL 中占位符参数绑定到控制器处理方法的入参中：
	// URL 中的 {xxx} 占位符可以通过@PathVariable(“xxx“) 绑定到操作方法的入参中。
	@DeleteMapping("/shippings/{shippingId}")
	public ResponseVo delete(@PathVariable Integer shippingId,
							 HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return shippingService.delete(user.getId(), shippingId);
	}

	@PutMapping("/shippings/{shippingId}")
	public ResponseVo update(@PathVariable Integer shippingId,
							 @Valid @RequestBody ShippingForm form,
							 HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return shippingService.update(user.getId(), shippingId, form);
	}

	@GetMapping("/shippings")
	public ResponseVo list(@RequestParam(required = false, defaultValue = "1") Integer pageNum,
						   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
						   HttpSession session) {
		User user = (User) session.getAttribute(MallConst.CURRENT_USER);
		return shippingService.list(user.getId(), pageNum, pageSize);
	}
}
