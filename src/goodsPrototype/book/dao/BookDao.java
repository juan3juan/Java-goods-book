package goodsPrototype.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import goodsPrototype.book.domain.Book;
import goodsPrototype.category.domain.Category;
import goodsPrototype.pager.Expression;
import goodsPrototype.pager.PageBean;
import goodsPrototype.pager.PageConstants;

public class BookDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 查询指定分类下图书的个数
	 * @param cid
	 * @return
	 * @throws SQLException
	 */
	public int findBookCountByCategory(String cid) throws SQLException {
		String sql = "select * from t_book where cid=?";
		Number cnt = (Number) qr.query(sql, new ScalarHandler(), cid);
		return cnt == null ? 0: cnt.intValue();
	}
	
	public Book findByBid(String bid) throws SQLException {
		String sql = "select * from t_book b,t_category c where b.cid=c.cid and b.bid=?";
		Map<String, Object> map = qr.query(sql, new MapHandler(), bid);
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		// 把pid获取出来，创建一个Category parent,把pid赋给它
		if(map.get("pid") != null) {
			Category parent = new Category();
			parent.setCid((String)map.get("pid"));
			category.setParent(parent);
		}	
		return book;
	}
	/**
	 * find by Category
	 * @param cid
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCategory(String cid, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid", "=", cid));
		return findByCriteria(exprList, pc);
	}
	/**
	 * find by Author
	 * @param author
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByAuthor(String author, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author", "like", "%" + author + "%"));
System.out.print("\nexprList: "+exprList);
		PageBean<Book> books = findByCriteria(exprList, pc);
System.out.print("\nbooks: "+books);
		return books;
	}
	/**
	 * find by book name
	 * @param bname
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByBname(String bname, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%"+bname+"%"));
		return findByCriteria(exprList, pc);
	}
	/**
	 * find by press
	 * @param press
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByPress(String press, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press", "like", "%"+press+"%"));
		return findByCriteria(exprList, pc);
	}
	
	/**
	 * 多条件组合查询
	 * @param combination
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCombination(Book criteria, int pc) throws SQLException {
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname", "like", "%"+criteria.getBname()+"%"));
		exprList.add(new Expression("author","like", "%"+criteria.getAuthor()+"%"));
		exprList.add(new Expression("press","like", "%"+criteria.getPress()+"%"));
		return findByCriteria(exprList, pc);
	}
	/**
	 * 通用函数，通过条件查询
	 * @param exprList
	 * @param pc
	 * @return
	 * @throws SQLException
	 */
	public PageBean<Book> findByCriteria(List<Expression> exprList, int pc) throws SQLException {
		/*
		 * 1. obtain ps
		 * 2. obtain tr
		 * 3. obtain beanList
		 * 4. create PageBean, return 
		 */
		int ps = PageConstants.BOOK_PAGE_SIZE;
		/*
		 * 2. 通过expressList生成where子句
		 */
		StringBuilder whereSql = new StringBuilder(" where 1=1");
		List<Object> params = new ArrayList<Object>();
		for(Expression expr: exprList) {
			/*
			 * add condition
			 * a. begin with "and"
			 * b. name
			 * c. operator
			 * d. if condition is not null, add param
			 */
			whereSql.append(" and ").append(expr.getName())
			.append(" ").append(expr.getOperator()).append(" ");
			// where 1=1 and bid=?
			if(!expr.getOperator().equals("is null")) {
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
		/*
		 * 3. tr
		 */
		String sql = "select count(*) from t_book" + whereSql;
		//int tr111 = (int) qr.query(sql, new ScalarHandler(), params.toArray());
		Number number = (Number) qr.query(sql, new ScalarHandler(), params.toArray());
		int tr = number.intValue();
System.out.print("\nwheresql: "+whereSql);
System.out.print("\ntr: "+tr);

		/*
		 * 4. beanList, current Page
		 */
		sql = "select * from t_book" + whereSql + " order by orderBy limit ?,?";
		params.add((pc-1)*ps); //当前页首行记录下标
		params.add(ps); //共查询行数，即每页记录数
		//query 最后一个参数为可变参数，即类比于数组
		List<Book> beanList = qr.query(sql, new BeanListHandler<Book>(Book.class), params.toArray());
System.out.print("\nbeanList--DAO: "+beanList);

		/*
		 * 5. create PageBean, 但现在无url，需要servlet添加
		 */
		PageBean<Book> pb = new PageBean<Book>();
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		pb.setBeanList(beanList);
		
		return pb;
	}
	
	public void add(Book book) throws SQLException {
		String sql = "insert into t_book(bid,bname,author,price,currPrice," +
				"discount,press,publishtime,edition,pageNum,wordNum,printtime," +
				"booksize,paper,cid,image_w,image_b)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] params = {book.getBid(),book.getBname(),book.getAuthor(),
				book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),
				book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),
				book.getImage_w(),book.getImage_b()};
		qr.update(sql, params);
	}
	
	/**
	 * 修改图书
	 * @param book
	 * @throws SQLException 
	 */
	public void edit(Book book) throws SQLException {
		String sql = "update t_book set bname=?,author=?,price=?,currPrice=?,"+
				"discount=?,press=?,publishtime=?,edition=?,pageNum=?,wordNum=?,printtime=?," +
				"booksize=?,paper=?,cid=? where bid=?";
		Object[] params = {book.getBname(),book.getAuthor(),
				book.getPrice(),book.getCurrPrice(),book.getDiscount(),
				book.getPress(),book.getPublishtime(),book.getEdition(),
				book.getPageNum(),book.getWordNum(),book.getPrinttime(),
				book.getBooksize(),book.getPaper(), book.getCategory().getCid(),book.getBid()};
		qr.update(sql, params);
	}
	
	/**
	 * 删除图书
	 * @param bid
	 * @throws SQLException 
	 */
	public void delete(String bid) throws SQLException {
		String sql = "delete from t_book where bid=?";
		qr.update(sql,bid);
	}
	
	public static void main(String[] args) throws SQLException {
		BookDao bookDao = new BookDao();
		List<Expression> exprList = new ArrayList<Expression>();
		//exprList.add(new Expression("bid","=","1"));
		exprList.add(new Expression("bname","=","%java%"));
		
		bookDao.findByCriteria(exprList, 10);
	}
	
	
	
}
