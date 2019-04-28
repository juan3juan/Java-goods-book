package goodsPrototype.admin.admin.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.admin.admin.domain.Admin;
import goodsPrototype.admin.admin.service.AdminService;


public class AdminServlet extends BaseServlet {
	AdminService adminService = new AdminService();
  
	public String login(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		Admin form = CommonUtils.toBean(req.getParameterMap(), Admin.class);
		Admin admin = adminService.login(form);
		if(admin==null) {
			req.setAttribute("msg", "username or psw wrong!");
			return "f:/adminjsps/login.jsp";
		}
		req.getSession().setAttribute("admin", admin);
		return "r:/adminjsps/admin/index.jsp";
	}

}
