package goodsPrototype.cart.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.book.domain.Book;
import goodsPrototype.cart.domain.CartItem;
import goodsPrototype.cart.service.CartItemService;
import goodsPrototype.user.domain.User;

/**
 * Servlet implementation class CartItemServlet
 */
public class CartItemServlet extends BaseServlet {
	CartItemService cartItemService = new CartItemService();
       


	public String myCart(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		User user = (User) req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		List<CartItem> cartItemList = cartItemService.myCart(uid);
		req.setAttribute("cartItemList", cartItemList);
		return "f:/jsps/cart/list.jsp";
	}

	/**
	 * add cartItem
	 * @param req
	 * @param resp
	 * @return
	 * @throws IOException 
	 * @throws ServletException 
	 */
	public String add(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain data from form
		 */
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		/*
		 * 2. 调用service添加
		 */
		cartItemService.add(cartItem);
		/*
		 * 3. 查询出当前用户的所有条目，转发到list.jsp显示
		 */
		return myCart(req, resp);
	}
	
	/**
	 * 批量删除
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemIds
		 * 2. 调用Service完成删除
		 * 3. 返回到list.jsp
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		cartItemService.batchDelete(cartItemIds);
		return myCart(req,resp);
	}
	
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemId
		 * 2.获取quantity
		 * 3.
		 */
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
		
		//因数量更新需使用ajax，所以此处要返回json对象
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
System.out.print("\n"+sb);
		resp.getWriter().print(sb);
		return null;
	}
	
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取cartItemIds
		 * 2. 传入service得到List<CartItem>
		 * 3. save, forwards to showitem.jsp
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";		
	}

}
