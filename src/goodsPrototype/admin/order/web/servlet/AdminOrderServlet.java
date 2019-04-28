package goodsPrototype.admin.order.web.servlet;

import cn.itcast.servlet.BaseServlet;
import goodsPrototype.order.domain.Order;
import goodsPrototype.order.service.OrderService;
import goodsPrototype.pager.PageBean;
import goodsPrototype.user.domain.User;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminOrderServlet extends BaseServlet {
	private OrderService orderService = new OrderService();

	private int getPc(HttpServletRequest req) {
		int pc = 1;
		String param = req.getParameter("pc");
		if(param !=null && !param.trim().isEmpty()) {
			pc = Integer.parseInt(param);
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
	
	public String findAll(HttpServletRequest req, HttpServletResponse resp) 
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
		 * PageBean<T> PageBean
		 */
		PageBean<Order> pb = orderService.findAll(pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 按状态查询
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String findByStatus(HttpServletRequest req, HttpServletResponse resp) 
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
		 * PageBean<T> PageBean
		 */
		/*
		 * 3. 获取参数链接
		 */
		int status = Integer.parseInt(req.getParameter("status"));
		
		PageBean<Order> pb = orderService.findByStatus(status, pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/adminjsps/admin/order/list.jsp";
	}
	
	/**
	 * 加载订单详细信息
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");
		req.setAttribute("btn", btn);
		return "f:/adminjsps/admin/order/desc.jsp";
	}
	
	/**
	 * 取消订单
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String cancel(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findStatus(oid);
		if(status !=1 ) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "Wrong status, can't cancel!");
			return "f:/adminjsps/admin/msg.jsp";
		}
		orderService.updateStatus(oid, 5); //设置订单状态为5
		req.setAttribute("code", "success");
		req.setAttribute("msg", "Order has been canceled!");
		return "f:/adminjsps/admin/msg.jsp";
	}
	
	/**
	 * 发货
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String deliver(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findStatus(oid);
		if(status !=2 ) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "Wrong status, can't send!");
			return "f:/adminjsps/admin/msg.jsp";
		}
		orderService.updateStatus(oid, 3); //设置订单状态为5
		req.setAttribute("code", "success");
		req.setAttribute("msg", "Order has been send!");
		return "f:/adminjsps/admin/msg.jsp";
	}
}
