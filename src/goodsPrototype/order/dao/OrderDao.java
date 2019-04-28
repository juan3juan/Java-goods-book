package goodsPrototype.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import goodsPrototype.book.domain.Book;
import goodsPrototype.order.domain.Order;
import goodsPrototype.order.domain.OrderItem;
import goodsPrototype.pager.Expression;
import goodsPrototype.pager.PageBean;
import goodsPrototype.pager.PageConstants;

public class OrderDao {
	QueryRunner qr = new TxQueryRunner();
	
	public Order load(String oid) throws SQLException {
		String sql = "select * from t_order where oid=?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class), oid);
		loadOrderItem(order);
		return order;
	}
	
	public PageBean<Order> findByCriteria(List<Expression> exprList, int pc) throws SQLException {
		/*
		 * 1. obtain ps
		 * 2. obtain tr
		 * 3. obtain beanList
		 * 4. create PageBean, return 
		 */
		int ps = PageConstants.ORDER_PAGE_SIZE;
		
		StringBuilder whereSql = new StringBuilder("where 1=1");
		List<Object> params = new ArrayList<Object>();
		for(Expression expr: exprList) {
			whereSql.append(" and ").append(expr.getName())
			.append(" ").append(expr.getOperator()).append(" ");
			if(expr.getOperator() != "is null") {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
		
		String sql = "select count(*) from t_order " + whereSql;
		Number number;
		if(exprList.size()==0)
			number = (Number) qr.query(sql, new ScalarHandler());
		else 
			number = (Number) qr.query(sql, new ScalarHandler(), params);
		int tr = number.intValue();
		sql = "select * from t_order " + whereSql + " order by ordertime desc limit ?,?";
		params.add((pc-1)*ps);
		params.add(ps);
		List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class), params.toArray());
		
		//虽然已经获取所有订单，但每个订单中并没有订单条目
		//遍历每个订单，为其加载它所有的订单条目
		for(Order order: beanList) {
			loadOrderItem(order);
		}
		
		
		PageBean<Order> pb = new PageBean<Order>();
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		pb.setBeanList(beanList);
		
		return pb;
	}
	
	public PageBean<Order> findByUser(String uid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("uid", "=", uid));
		return findByCriteria(exprList, pc);
	}
	
	public PageBean<Order> findByStatus(int status, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		//因为Expression参数是字符串，此处直接把status链接上空字符改为字符串即可，不影响查询，因sql语句中全都是字符串
		exprList.add(new Expression("status", "=", status+""));
		return findByCriteria(exprList, pc);
	}

	
	public void loadOrderItem(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid =?";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		
		order.setOrderItemList(orderItemList);
	}
	
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
	
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for(Map<String, Object> map: mapList) {
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
	
	public void add(Order order) throws SQLException {
		/*
		 * 1. 插入订单
		 */
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {order.getOid(), order.getOrdertime(),
				order.getTotal(), order.getStatus(), order.getAddress(),
				order.getOwner().getUid()};
		qr.update(sql, params);
		/*
		 * 2. 循环遍历订单所有条目，每个条目生成一个Object[]
		 * 多个条目对应Object[][]
		 * 执行批处理，完成插入订单条目
		 */
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		int len = order.getOrderItemList().size();
		Object[][] objs = new Object[len][];
		for(int i=0; i<len; i++) {
			OrderItem item = order.getOrderItemList().get(i);
			objs[i] = new Object[] {item.getOrderItemId(), item.getQuantity(),
					item.getSubtotal(), item.getBook().getBid(),
					item.getBook().getBname(), item.getBook().getCurrPrice(),
					item.getBook().getImage_b(), order.getOid()};
		}
		//执行批处理
		qr.batch(sql, objs);
	}
	
	public int findStatus(String oid) throws SQLException {
		String sql = "select status from t_order where oid=?";
		Number number = (Number) qr.query(sql, new ScalarHandler(), oid);
		return number.intValue();				
	}
	
	public void updateStatus(String oid, int status) throws SQLException {
		String sql = "update t_order set status=? where oid=?";
		qr.update(sql, status, oid);
	}
	
	public PageBean<Order> findAll(int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		return findByCriteria(exprList, pc);
	}

}
