package goodsPrototype.category.web.servlet;

import cn.itcast.servlet.BaseServlet;
import goodsPrototype.category.domain.Category;
import goodsPrototype.category.service.CategoryService;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CategoryServlet
 */
public class CategoryServlet extends BaseServlet {	
	private CategoryService categoryService = new CategoryService();
       
  
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. query use service
		 * 2. save to request, forward to left.jsp
		 */
		List<Category> parents = categoryService.findAll();
		req.setAttribute("parents", parents);
		return "f:/jsps/left.jsp";
	}


}
