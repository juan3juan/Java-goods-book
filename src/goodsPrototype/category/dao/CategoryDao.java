package goodsPrototype.category.dao;

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
import goodsPrototype.category.domain.Category;

public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 *mapping one map to a category
	 * @param map
	 * @return
	 */
	private Category toCategory(Map<String, Object> map) {
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");
		if(pid != null) {
			Category parent = new Category(); //new a category to store parent, and then map the parent to the category created before	
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	/**
	 * mapping mapList to categoryList 
	 * @param mapList
	 * @return
	 */
	private List<Category> toCategoryList(List<Map<String, Object>> mapList) {
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String, Object>map: mapList) {
			Category c = toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	/**
	 * return all category
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findAll() throws SQLException {
		/*
		 * 1. query all the first category 
		 */

		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		/*
		 * 2. loop parent, add children for each one
		 */
		for(Category parent: parents) {
			List<Category> children = findByParent(parent.getCid());
			parent.setChildren(children);
		}		
		return parents;
	}
	
	/**
	 * find children by parent
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException{
		String sql = "select * from t_category where pid=?";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(), pid);		
		return toCategoryList(mapList);
	}
	
	/**
	 * 后台添加分类
	 * @param category
	 * @throws SQLException 
	 */
	public void add(Category category) throws SQLException {
		//`desc` 因desc为数据库关键字，所以要用esc下面的``括起来，否则会出错
		String sql = "insert into t_category(cid,cname,pid,`desc`) values(?,?,?,?)";
		/*
		 * 因为一级分类无parent, 二级分类有
		 * 此方法要兼容，所以需要判断
		 */
		String pid = null; //一级分类
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCid(), category.getCname(), pid, category.getDesc()};
		qr.update(sql,params);
		
	}
	
	/**
	 * 获取所有父分类，不带子分类
	 * @return
	 * @throws SQLException
	 */
	public List<Category> findParents() throws SQLException {
		/*
		 * 1. query all the first category 
		 */
		String sql = "select * from t_category where pid is null order by orderBy";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
			
		return parents;
	}
	
	/**
	 * 加载分类
	 * both 一级 and 二级
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public Category load(String cid) throws SQLException {
		String sql = "select * from t_category where cid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), cid);
		return toCategory(map);		
	}
	
	/**
	 * 修改分类
	 * both 一级 and 二级
	 * @param category
	 * @throws SQLException
	 */
	public void edit(Category category) throws SQLException {
		String sql = "update t_category set cname=?, pid=?, `desc`=? where cid=?";
		//一级pid为null
		String pid = null;
		//二级pid不为null，赋值
		if(category.getParent() != null) {
			pid = category.getParent().getCid();
		}
		Object[] params = {category.getCname(), pid, category.getDesc(), category.getCid()};
		qr.update(sql,params);
	}
	
	public int findChildrenCountByParent(String pid) throws SQLException {
		String sql = "select count(*) from t_category where pid=?";
		Number cnt = (Number) qr.query(sql, new ScalarHandler(), pid);
		return cnt == null ? 0 : cnt.intValue();
	}
	
	public void delete(String cid) throws SQLException {
		String sql = "delete from t_category where cid=?";
		qr.update(sql, cid);
	}
}
