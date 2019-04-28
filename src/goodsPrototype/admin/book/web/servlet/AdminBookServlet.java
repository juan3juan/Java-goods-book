package goodsPrototype.admin.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.book.domain.Book;
import goodsPrototype.book.service.BookService;
import goodsPrototype.category.domain.Category;
import goodsPrototype.category.service.CategoryService;
import goodsPrototype.pager.PageBean;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminBookServlet extends BaseServlet {
	
	private CategoryService categoryService = new CategoryService();
    private BookService bookService = new BookService();
	  
	public String findCategoryAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. query use service
		 * 2. save to request, forward to left.jsp
		 */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/left.jsp";
	}
	
    /**
     * obtain current pc
     * @param req
     * @return
     */
	private int getPc(HttpServletRequest req) {
		int pc = 1;
		String param = req.getParameter("pc");
		if(param != null && !param.trim().isEmpty()) {
			try {
				pc = Integer.parseInt(param);
			} catch(RuntimeException e) {}
		}
		return pc;
	}
	
	private String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?" + req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index != -1) {
			url = url.substring(0, index);
		}
		return url;
	}
	
	public String findByCategory(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain pc, if not, use pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. obtain url
		 */
		String url = getUrl(req);		
		/*
		 * 3. obtain condition-- this method are cid & pc
		 */
		String cid = req.getParameter("cid");
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Book> pb = bookService.findByCategory(cid, pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	public String findByAuthor(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain pc, if not, use pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. obtain url
		 */
		String url = getUrl(req);	
System.out.print("\nUrl: "+url);
		/*
		 * 3. obtain condition-- this method are cid & pc
		 */
		String author = req.getParameter("author");
System.out.print("\nauthor: "+author);
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
System.out.print("\npb: "+pb);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
System.out.print("\npb222: "+pb.getBeanList());
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	public String findByPress(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain pc, if not, use pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. obtain url
		 */
		String url = getUrl(req);		
		/*
		 * 3. obtain condition-- this method are cid & pc
		 */
		String press = req.getParameter("press");
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Book> pb = bookService.findByPress(press, pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	public String findByBname(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain pc, if not, use pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. obtain url
		 */
		String url = getUrl(req);		
System.out.print("\nurl: "+url);
		/*
		 * 3. obtain condition-- this method are cid & pc
		 */
		String bname = req.getParameter("bname");
System.out.print("\nbname: "+bname);
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}

	public String findByCombination(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain pc, if not, use pc=1
		 */
		int pc = getPc(req);
		/*
		 * 2. obtain url
		 */
		String url = getUrl(req);		
		/*
		 * 3. obtain condition-- this method are cid & pc
		 */
		Book criteria = CommonUtils.toBean(req.getParameterMap(), Book.class);
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Book> pb = bookService.findByCombination(criteria, pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/book/list.jsp";
	}
	
	public String addPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取所有一级分类，保存
		 * 2. 转发到add.jsp, 该页面会在下拉列表中显示所有分类
		 */
		List<Category> parents = categoryService.findParents();
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/book/add.jsp";
	}
	
	public String ajaxFindChildren(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取pid
		 * 2. 通过pid查询出所有的二级分类
		 * 3. 把二级分类的list转换成json对象返回
		 */
		String pid = req.getParameter("pid");
		List<Category> children = categoryService.findByParent(pid);
		String json = toJson(children);
System.out.print(json);
		resp.getWriter().print(json);
		return null;
	}
	
	//{"cid":"dadaD", "cname":"dsdsd"}
	private String toJson(Category category) {
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"cid\"").append(":").append("\"").append(category.getCid()).append("\"");
		sb.append(",");
		sb.append("\"cname\"").append(":").append("\"").append(category.getCname()).append("\"");
		sb.append("}");
		return sb.toString();
	}
	//[{"cid":"dadaD", "cname":"dsdsd"}, {"cid":"dadaddD", "cname":"dsdddsd"}]
	private String toJson(List<Category> categoryList) {
		StringBuilder sb = new StringBuilder("[");
		for(int i=0; i<categoryList.size(); i++) {
			sb.append(toJson(categoryList.get(i)));
			if(i<categoryList.size()-1)
				sb.append(",");
		}
		sb.append("]");
		return sb.toString();
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 获取bid，得到book对象，保存之
		 */
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);
		/*
		 * 2. 获取所有一级分类，保存之
		 */
		req.setAttribute("parents", categoryService.findParents());
		/*
		 * 3. 获取当前图书所属的一级分类下所有的二级分类
		 */
		String pid = book.getCategory().getParent().getCid();
		req.setAttribute("children", categoryService.findByParent(pid));
		/*
		 * 4. 转发到desc.jsp显示
		 */
		return "f:/adminjsps/admin/book/desc.jsp";
	}
	
	/**
	 * 修改图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String edit(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		/*
		 * 1. 把表单数据封装到book对象中
		 * 2. 封装cid到category中
		 * 3. 把category赋给book
		 * 4. 调用service完成工作
		 * 5. 保存成功信息转发到msg.jsp
		 */
		Map map = req.getParameterMap();
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		bookService.edit(book);
		
		req.setAttribute("msg", "修改图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
	
	/**
	 * 删除图书
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String delete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		//删除图片
		Book book = bookService.load(bid);
		String savepath = this.getServletContext().getRealPath("/"); //获取真实路径
		new File(savepath, book.getImage_w()).delete();
		new File(savepath, book.getImage_b()).delete();
		
		bookService.delete(bid);
		
		req.setAttribute("msg", "删除图书成功！");
		return "f:/adminjsps/msg.jsp";
	}
}
