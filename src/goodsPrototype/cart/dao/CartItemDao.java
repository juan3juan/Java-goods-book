package goodsPrototype.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import goodsPrototype.book.domain.Book;
import goodsPrototype.cart.domain.CartItem;
import goodsPrototype.user.domain.User;

public class CartItemDao {
	QueryRunner qr = new TxQueryRunner();
	
	private CartItem toCartItem(Map<String, Object> map) {
		if(map==null || map.size()==0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	private List<CartItem> toCartItemList(List<Map<String, Object>> mapList){
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String, Object> map: mapList) {
			CartItem cartItem = toCartItem(map);
			cartItemList.add(cartItem);
		}
		return cartItemList;
	}
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.uid=? order by c.orderBy";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),uid);
		List<CartItem> cartItemList = toCartItemList(mapList);
		return cartItemList;
	}
	
	/**
	 * 查询购物车中某个 用户的某本图书是否存在
	 * @param uid
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByUidAndBid(String uid, String bid) throws SQLException {
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), uid, bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;		
	}
	
	/**
	 * 修改指定条目的数量
	 * @param cartItemId
	 * @param quantity
	 * @throws SQLException 
	 */
	public void updateQuantity(String cartItemId, int quantity) throws SQLException {
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql, quantity, cartItemId);
	}
	
	public CartItem findByCartItemId(String cartItemId) throws SQLException {
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c.cartItemId=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), cartItemId);
		return toCartItem(map);
	}
	
	public void addCartItem(CartItem cartItem) throws SQLException {
		String sql = "insert into t_cartitem(cartItemId, quantity, bid, uid)"+
					"values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(), cartItem.getQuantity(),
					cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	
	private String toWhereSql(int len) {
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i=0; i<len; i++)
		{
			sb.append("?");
			if(i<len-1)
				sb.append(",");
		}
		sb.append(")");
		return sb.toString();
	}
	
	public void batchDelete(String cartItemIds) throws SQLException {
		/*
		 * 先把cartItemIds转换成数据
		 * 1. cartItemIds 转换成where子句
		 * 2. 与delete from 连接，执行
		 */
		// 此处必须要Object对象，因为qr.update()中参数为可变数组
		// 若Object[]改为String[],qr.update()不会展开数组
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where " + whereSql;
		qr.update(sql, cartItemIdArray);
	}
	
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException {
		/*
		 * 1.先把cartItemIds转换成数据
		 * 2. cartItemIds 转换成where子句
		 * 3. 生成sql语句
		 * 4. 执行，返回cartItemList
		 */
		Object[] cartItemIdArray = cartItemIds.split(",");
		String wheresql = toWhereSql(cartItemIdArray.length);
		String sql = "select * from t_cartitem c, t_book b where c.bid=b.bid and c." + wheresql;
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), cartItemIdArray);
		List<CartItem> cartItemList = toCartItemList(mapList);
		return cartItemList;
	}
	
}
