package com.xuteng.mall.service.impl;

import com.google.gson.Gson;
import com.xuteng.mall.dao.ProductMapper;
import com.xuteng.mall.enums.ProductStatusEnum;
import com.xuteng.mall.enums.ResponseEnum;
import com.xuteng.mall.form.CartAddForm;
import com.xuteng.mall.form.CartUpdateForm;
import com.xuteng.mall.pojo.Cart;
import com.xuteng.mall.pojo.Product;
import com.xuteng.mall.service.ICartService;
import com.xuteng.mall.vo.CartProductVo;
import com.xuteng.mall.vo.CartVo;
import com.xuteng.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by 廖师兄
 * cartvo是传回前端的
 * cart是存入redis中的
 */
@Service
public class CartServiceImpl implements ICartService {

	private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private StringRedisTemplate redisTemplate;

	private Gson gson = new Gson();

	@Override
	public ResponseVo<CartVo> add(Integer uid, CartAddForm form) {
		Integer quantity = 1;

		Product product = productMapper.selectByPrimaryKey(form.getProductId());

		//商品是否存在
		if (product == null) {
			return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
		}

		//商品是否正常在售
		if (!product.getStatus().equals(ProductStatusEnum.ON_SALE.getCode())) {
			return ResponseVo.error(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE);
		}

		//商品库存是否充足
		if (product.getStock() <= 0) {
			return ResponseVo.error(ResponseEnum.PROODUCT_STOCK_ERROR);
		}

		//写入到redis
		//key: cart_1
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		//拼接动态字符串，前面一个有正则占位符，后面是内容
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

		Cart cart;
		//获取用户存在购物车中的商品，通过商品id（这个value是cart对象，json格式存放的）
		String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
		if (StringUtils.isEmpty(value)) {
			//没有该商品, 新增购物陈对象
			cart = new Cart(product.getId(), quantity, form.getSelected());
		}else {
			//已经有了，数量+1
			//通过gson方法将value值赋给cart对象
			cart = gson.fromJson(value, Cart.class);
			cart.setQuantity(cart.getQuantity() + quantity);
		}
		//在将商品数量+1存入redis
		opsForHash.put(redisKey,
				String.valueOf(product.getId()),
				gson.toJson(cart));

		return list(uid);
	}

	@Override
	public ResponseVo<CartVo> list(Integer uid) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
		Map<String, String> entries = opsForHash.entries(redisKey);
		//是否全选
		boolean selectAll = true;
		//总的商品数
		Integer cartTotalQuantity = 0;
		//总的待付款金额
		BigDecimal cartTotalPrice = BigDecimal.ZERO;
		CartVo cartVo = new CartVo();
		//购物车中的商品列表
		List<CartProductVo> cartProductVoList = new ArrayList<>();
		//通过Map.Entry(String,String) 接口，然后使用entry.entrySet()获取映射关系，
		// entrySet()存放的是map中的映射关系
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			Integer productId = Integer.valueOf(entry.getKey());
			Cart cart = gson.fromJson(entry.getValue(), Cart.class);

			//TODO 需要优化，使用mysql里的in
			Product product = productMapper.selectByPrimaryKey(productId);
			if (product != null) {
				CartProductVo cartProductVo = new CartProductVo(productId,
						cart.getQuantity(),
						product.getName(),
						product.getSubtitle(),
						product.getMainImage(),
						product.getPrice(),
						product.getStatus(),
						product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
						product.getStock(),
						cart.getProductSelected()
				);
				cartProductVoList.add(cartProductVo);
				//只要有一个商品没被选中，就直接将全选置为false
				if (!cart.getProductSelected()) {
					selectAll = false;
				}

				//计算总价(只计算选中的)
				if (cart.getProductSelected()) {
					cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
				}
			}

			cartTotalQuantity += cart.getQuantity();
		}

		//有一个没有选中，就不叫全选
		cartVo.setSelectedAll(selectAll);
		cartVo.setCartTotalQuantity(cartTotalQuantity);
		cartVo.setCartTotalPrice(cartTotalPrice);
		cartVo.setCartProductVoList(cartProductVoList);
		return ResponseVo.success(cartVo);
	}

	@Override
	public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm form) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

		String value = opsForHash.get(redisKey, String.valueOf(productId));
		if (StringUtils.isEmpty(value)) {
			//没有该商品, 报错
			return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
		}

		//已经有了，修改内容
		Cart cart = gson.fromJson(value, Cart.class);
		//如果传入具体数字了，就让照着改
		if (form.getQuantity() != null
				&& form.getQuantity() >= 0) {
			cart.setQuantity(form.getQuantity());
		}
		//如果传入是否选中，也照着改
		if (form.getSelected() != null) {
			cart.setProductSelected(form.getSelected());
		}
		//存入redis中
		opsForHash.put(redisKey, String.valueOf(productId), gson.toJson(cart));
		return list(uid);
	}

	@Override
	public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
		//常规操作，新建hash，然后构建对应的用户id，然后取出对应用户id和productId中的value
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);

		String value = opsForHash.get(redisKey, String.valueOf(productId));
		if (StringUtils.isEmpty(value)) {
			//没有该商品, 报错
			return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
		}
		//删除该条数据
		opsForHash.delete(redisKey, String.valueOf(productId));
		return list(uid);
	}

	@Override
	public ResponseVo<CartVo> selectAll(Integer uid) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
		//将该用户的所有商品都置为选中转态，然后在存入redis中
		for (Cart cart : listForCart(uid)) {
			cart.setProductSelected(true);
			opsForHash.put(redisKey,
					String.valueOf(cart.getProductId()),
					gson.toJson(cart));
		}

		return list(uid);
	}

	@Override
	public ResponseVo<CartVo> unSelectAll(Integer uid) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
		//将该用户的所有商品都置为不选中转态，然后在存入redis中
		for (Cart cart : listForCart(uid)) {
			cart.setProductSelected(false);
			opsForHash.put(redisKey,
					String.valueOf(cart.getProductId()),
					gson.toJson(cart));
		}

		return list(uid);
	}

	@Override
	public ResponseVo<Integer> sum(Integer uid) {
		Integer sum = listForCart(uid).stream()
				//取出所有商品的数量
				.map(Cart::getQuantity)
				//求出所有商品的总数
				.reduce(0, Integer::sum);
		return ResponseVo.success(sum);
	}

	public List<Cart> listForCart(Integer uid) {
		HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
		String redisKey  = String.format(CART_REDIS_KEY_TEMPLATE, uid);
		Map<String, String> entries = opsForHash.entries(redisKey);

		List<Cart> cartList = new ArrayList<>();
		//这里得到该用户的所有商品map的映射关系，并且遍历
		for (Map.Entry<String, String> entry : entries.entrySet()) {
			//这里不需要key，只需要把所有cart都取出来，然后放入列表中
			cartList.add(gson.fromJson(entry.getValue(), Cart.class));
		}

		return cartList;
	}

}
