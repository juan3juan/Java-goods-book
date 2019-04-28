package goodsPrototype.order.web.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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
import goodsPrototype.order.domain.Order;
import goodsPrototype.order.domain.OrderItem;
import goodsPrototype.order.service.OrderService;
import goodsPrototype.pager.PageBean;
import goodsPrototype.user.domain.User;


public class OrderServlet extends BaseServlet {
	OrderService orderService = new OrderService();
	CartItemService cartItemService = new CartItemService();
   
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
	
	public String myOrders(HttpServletRequest req, HttpServletResponse resp) 
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
		 * 3. 从session中获取当前用户-- this method are uid & pc
		 */
		User user = (User) req.getSession().getAttribute("sessionUser");
		/*
		 * PageBean<T> PageBean
		 */
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		/*
		 * 5. set url to PageBean, save, forward
		 */
		pb.setUrl(url);
		req.setAttribute("pb", pb);
		return "f:/jsps/order/list.jsp";
	}
	
	public String createOrder(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 通过cartItemService获取购物车cartItemIds
		 */
		String cartItemIds = req.getParameter("cartItemIds");
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		/*
		 * 2. 创建order
		 */
		Order order = new Order();
		order.setOid(CommonUtils.uuid()); 
		order.setOrdertime(String.format("%tF %<tT", new Date())); 
		order.setStatus(1); //1,未付款
		order.setAddress(req.getParameter("address"));
		User owner = (User) req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		
		//获取准确值，避免二进制误差 -- new BigDecimal("0") 代表初始化为0
		BigDecimal total = new BigDecimal("0");
		for(CartItem cartItem: cartItemList) {
			//相加，但因调用BigDecimal中参数为String的函数，所以要把传入的值变为String
			total = total.add(new BigDecimal(cartItem.getSubtotal() + ""));
		}
		order.setTotal(total.doubleValue());
		/*
		 * 3. 创建List<OrderItem>
		 */
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
 		for(CartItem cartItem: cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setBook(cartItem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
		}
 		order.setOrderItemList(orderItemList);
 		/*
 		 * 4. 调用service完成
 		 */
 		orderService.createOrder(order);
 		//删除购物车条目
 		cartItemService.batchDelete(cartItemIds);
 		/*
 		 * 5. 保存订单，转发
 		 */
 		req.setAttribute("order", order);
 		return "f:/jsps/order/ordersucc.jsp";
	}
	
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		String btn = req.getParameter("btn");
		req.setAttribute("btn", btn);
		return "f:/jsps/order/desc.jsp";
	}
	
	
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
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 5); //设置订单状态为5
		req.setAttribute("code", "success");
		req.setAttribute("msg", "Order has been canceled!");
		return "f:/jsps/msg.jsp";
	}
	
	public String confirm(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		/*
		 * 校验订单状态
		 */
		int status = orderService.findStatus(oid);
		if(status !=3 ) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "Wrong status, can't confirm!");
			return "f:/jsps/msg.jsp";
		}
		orderService.updateStatus(oid, 4); //设置订单状态为5
		req.setAttribute("code", "success");
		req.setAttribute("msg", "Order success!");
		return "f:/jsps/msg.jsp";
	}
	
	public String paymentPre(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		req.setAttribute("order", order);
		return "f:/jsps/order/pay.jsp";
	}
	
	public String payment(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		/*
		 * 1. 准备13个参数
		 */
		String p0_Cmd = "Buy"; //业务类型，固定填Buy
		String p1_MerId = props.getProperty("p1_MerId"); //商号编码，在易宝唯一标识
		String p2_Order = req.getParameter("order"); //订单编码
		String p3_Amt = "0.01"; //支付金额
		String p4_Cur = "CNY"; //交易币种,固定值
		String p5_Pid = ""; //商品名称
		String p6_Pcat = ""; //商品种类
		String p7_Pdesc = ""; //商品描述
		String p8_Url = props.getProperty("p8_Url"); //支付成功后，易宝会访问这个地址
		String p9_SAF = ""; //送货地址
		String pa_MP = "";//扩展信息
		String pd_FrpId = req.getParameter("yh");//支付通道
		String pr_NeedResponse = "1";//应答机制，固定值1
		
		/*
		 * 2.计算hmac
		 * 需要13个参数
		 * 需要keyValue
		 * 需要加密算法
		 */
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
		/*
		 * 3. 重定向到易宝的支付网关
		 */
		StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		sb.append("?").append("p0_Cmd=").append(p0_Cmd);
		sb.append("&").append("p1_MerId=").append(p1_MerId);
		sb.append("&").append("p2_Order=").append(p2_Order);
		sb.append("&").append("p3_Amt=").append(p3_Amt);
		sb.append("&").append("p4_Cur=").append(p4_Cur);
		sb.append("&").append("p5_Pid=").append(p5_Pid);
		sb.append("&").append("p6_Pcat=").append(p6_Pcat);
		sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		sb.append("&").append("p8_Url=").append(p8_Url);
		sb.append("&").append("p9_SAF=").append(p9_SAF);
		sb.append("&").append("pa_MP=").append(pa_MP);
		sb.append("&").append("pd_FrpId=").append(pd_FrpId);
		sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&").append("hmac=").append(hmac);
		
		resp.sendRedirect(sb.toString());
		return null;
	}
	
	/**
	 * 回馈方法
	 * 当支付成功时，易宝会访问这里
	 * 有两种方法访问：
	 * 1. 引导用户的浏览器重定向（如果用户关闭了浏览器就不能访问这里了，不推荐）
	 * 2. 易宝的服务器会使用点对点通讯的方法访问这个method（必须回馈success，不然易宝会一直调用这个方法）
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String back(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. 获取12个参数
		 */
		String p1_MerId = req.getParameter("p1_MerId");
		String r0_Cmd = req.getParameter("r0_Cmd");
		String r1_Code = req.getParameter("r1_Code");
		String r2_TrxId = req.getParameter("r2_TrxId");
		String r3_Amt = req.getParameter("r3_Amt");
		String r4_Cur = req.getParameter("r4_Cur");
		String r5_Pid = req.getParameter("r5_Pid");
		String r6_Order = req.getParameter("r6_Order");
		String r7_Uid = req.getParameter("r7_Uid");
		String r8_MP = req.getParameter("r8_MP");
		String r9_BType = req.getParameter("r9_BType");
		String hmac = req.getParameter("hmac");
		/*
		 * 2. 获取keyValue
		 */
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		String keyValue = props.getProperty("keyValue");
		/*
		 * 3. 调用PaymentUtil得到校验方法来校验调用者的身份
		 *   > 如果校验失败：保存错误信息，转发到msg.jsp
		 *   > 如果校验通过：
		 *    * 判断访问的方法是重定向还是点对点，如果是重定向
		 *    修改订单状态，保存成功信息，转发到msg.jsp
		 *    * 如果是点对点：修改订单状态，返回success
		 */
		boolean bool = PaymentUtil.verifyCallback(hmac, p1_MerId, r0_Cmd, r1_Code, r2_TrxId, r3_Amt, r4_Cur, r5_Pid, r6_Order, r7_Uid, r8_MP, r9_BType, keyValue);
		if(!bool) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "wrong payment");
			return "f:/jsps/msg.jsp";
		}
		if(r1_Code.equals("1")) {
			orderService.updateStatus(r6_Order, 2);
			if(r9_BType.equals("1")) {
				req.setAttribute("code", "success");
				req.setAttribute("msg", "恭喜，支付成功！");
				return "f:/jsps/msg.jsp";				
			} else if(r9_BType.equals("2")) {
				resp.getWriter().print("success");
			}
		}
		return null;	
	}
}
