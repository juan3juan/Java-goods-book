package goodsPrototype.admin.category.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.book.service.BookService;
import goodsPrototype.category.domain.Category;
import goodsPrototype.category.service.CategoryService;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AdminCategoryServlet extends BaseServlet {
	
	private CategoryService categoryService = new CategoryService();
	private BookService bookService = new BookService();
	
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "/adminjsps/admin/category/list.jsp";
 	}
	
	public String addParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 封装表单数据到Category中
		 * 2. 调用service的add()方法完成
		 * 3. 调用findAll(), 返回list.jsp显示所有分类
		 */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		parent.setCid(CommonUtils.uuid());
		categoryService.add(parent);
		return findAll(req, resp);	
	}
	
	public String addChildPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		String pid = req.getParameter("pid");
		List<Category> parents = categoryService.findParents();
		req.setAttribute("pid", pid);
		req.setAttribute("parents", parents);	
		
		return "f:/adminjsps/admin/category/add2.jsp";
	}
	
	public String addChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 封装表单数据到Category中
		 * 2. 需要手动把pid映射给parent
		 * 3. 调用service的add()方法完成
		 * 4. 调用findAll(), 返回list.jsp显示所有分类
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		child.setCid(CommonUtils.uuid());
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		categoryService.add(child);
		return findAll(req, resp);	
	}
	
	public String editParentPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 获取cid
		 * 2. 使用service load
		 * 3. 保存category
		 * 4. 转发
		 */
		String cid = req.getParameter("cid");
		Category parent = categoryService.load(cid);
		req.setAttribute("parent", parent);
		return "f:/adminjsps/admin/category/edit.jsp";
	}
	
	public String editParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 封装表单数据到category
		 * 2. 调用service edit完成修改
		 * 3. 调用findAll返回转发到list.jsp
		 */
		Category parent = CommonUtils.toBean(req.getParameterMap(), Category.class);
		categoryService.edit(parent);
		return findAll(req, resp);
	}
	
	/**
	 * 修改二级分类第一步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editChildPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 获取cid，通过cid加载category,保存之
		 * 2. 查询出所有1级分类，保存之
		 * 3. 转发到edit2.jsp
		 */
		String cid = req.getParameter("cid");
		Category child = categoryService.load(cid);
		List<Category> parents = categoryService.findParents();
		req.setAttribute("child", child);
		req.setAttribute("parents", parents);
		return "f:/adminjsps/admin/category/edit2.jsp";
	}
	/**
	 * 修改二级分类第二步
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String editChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 封装表单数据到child
		 * 2. 把表单中的pid封装到child中
		 * 3. 调用service方法完成修改
		 * 4. 转发
		 */
		Category child = CommonUtils.toBean(req.getParameterMap(), Category.class);
		String pid = req.getParameter("pid");
		Category parent = new Category();
		parent.setCid(pid);
		child.setParent(parent);
		categoryService.edit(child);
		return findAll(req, resp);
	}
	
	/**
	 * 删除一级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteParent(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 获取链接参数cid，它是一级分类的cid
		 * 2. 通过cid，查看该父分类下子分类的个数
		 * 3. 如果>0, 说明还有子分类不能删除，保存错误信息转发到msg.jsp
		 * 4. ==0，删除，返回到list.jsp
		 */
		String cid = req.getParameter("cid");
		int cnt = categoryService.findChildrenCountByParent(cid);
		if(cnt > 0 ) {
			req.setAttribute("msg", "still has children, can't be deleted!");
			return "f:/adminjsps/msg.jsp";
		} else {
			categoryService.delete(cid);
			return findAll(req, resp);
		}
		
	}
	
	/**
	 * 删除二级分类
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deleteChild(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException { 
		/*
		 * 1. 获取cid
		 * 2. 判断cid下是否还有book（调用BookService下方法判断）
		 * 3. 如果>0, 说明还有book不能删除，保存错误信息转发到msg.jsp
		 * 4. 如果==0，调用delete删除，返回list.jsp
		 */
		String cid = req.getParameter("cid");
		int cnt = bookService.findBookCountByCategory(cid);
		if(cnt>0) {
			req.setAttribute("msg", "still has book, can't be deleted!");
			return "f:/adminjsps/msg.jsp"; 
		} else {
			categoryService.delete(cid);
			return findAll(req, resp);
		}
	}

}
