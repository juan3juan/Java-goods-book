package goodsPrototype.user.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.user.domain.User;
import goodsPrototype.user.service.UserException;
import goodsPrototype.user.service.UserService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * user Web level
 * Servlet implementation class UserServlet 
 */
public class UserServlet extends BaseServlet {
	private UserService userService= new UserService();


	/**
	 * ajax user name
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		/*
		 * 1. obtain data
		 */
		String loginname = req.getParameter("loginname");
		
		/*
		 * 2. validate with service
		 */
		boolean res = userService.ajaxValidateLoginname(loginname);		
		/*
		 * 3. send to client
		 */
		resp.getWriter().print(res);
		return null;
	}
	
	/**
	 *  ajax email
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		/*
		 * 1. obtain data
		 */
		String email = req.getParameter("email");		
		/*
		 * 2. validate with service
		 */
		boolean res = userService.ajaxValidateLoginname(email);		
		/*
		 * 3. send to client
		 */
		resp.getWriter().print(res);
		return null;
	}
	
	/**
	 * ajax verify code
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		
		/*
		 * 1. obtain input verify code
		 */
		String verifyCode = req.getParameter("verifyCode");		
		System.out.print("\nVerify: "+verifyCode);
		/*
		 * 2. obtain img verify code,
		 */
		String vCode = (String) req.getSession().getAttribute("vCode");
		System.out.print("\nvCode: "+vCode);
		/*
		 * 3. compare two data
		 */
		boolean res = verifyCode.equalsIgnoreCase(vCode);
		
		/*
		 * 4. send to client
		 */
		resp.getWriter().print(res);
		return null;
	}
	
	/**
	 * register servlet
	 * @param req
	 * @param resp
	 * @return 
	 */
	public String register(HttpServletRequest req, HttpServletResponse resp) {
		/*
		 * 1. integrate data to User
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		System.out.print("\nformUser: "+formUser);
		/*
		 * 2. validate
		 */
		Map<String, String> errors = validateRegister(formUser, req.getSession());
		if(errors.size()>0) {
		System.out.print("\nre wrong :");
		System.out.print(errors);
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/register.jsp";
		}
		/*
		 * 3. send to service
		 */
		userService.register(formUser);
		/*
		 * 4. save success msg, forward to msg.jsp
		 */
		req.setAttribute("code", "success");
		req.setAttribute("msg", "Register Success,please go to your email!");
		return "f:/jsps/msg.jsp";
	}
	
	private Map<String,String> validateRegister(User formUser, HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
		
		/*
		 * 1. validate user name
		 */
		String loginname = formUser.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "User name can't be null!");
		}
		else if(loginname.length()<3 || loginname.length()>20) {
			errors.put("loginname", "user name has to be between 3-20!");
		}
		else if(!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "User Name already exist!");
		}
		/*
		 * 2. validate password
		 */
		String loginpass = formUser.getLoginname();
		if(loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "loginpass can't be null!");
		}
		else if(loginpass.length()<3 || loginpass.length()>20) {
			errors.put("loginpass", "loginpass has to be between 3-20!");
		}
		/*
		 * 3. validate user name
		 */
		String reloginpass = formUser.getLoginname();
		if(reloginpass == null || reloginpass.trim().isEmpty()) {
			errors.put("reloginpass", "reloginpass can't be null!");
		}
		else if(!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "reloginpass is not same as loginname!");
		}
		/*
		 * 4. validate user name
		 */
		String email = formUser.getEmail();
		if(email == null || email.trim().isEmpty()) {
			errors.put("email", "email can't be null!");
		}
		// regex java and js is a little different
		else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "email format is not correct--servlet!");
		}
		else if(!userService.ajaxValidateLoginname(email)) {
			errors.put("email", "email already exist!");
		}
		/*
		 * 5. validate verify code
		 */
		String verifyCode = formUser.getVerifyCode();
		String vCode = (String) session.getAttribute("vCode");
		System.out.print("\nverifyCode: "+ verifyCode);
		System.out.print("\nvCode: "+ vCode);
		System.out.print("\nSession: "+session.getAttribute("verifyCode"));
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "verifyCode can't be null--servlet!");
		}
		else if(!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "verifyCode is not correct--servlet!");
		}
		return errors;
	}
	
	public String activation(HttpServletRequest req, HttpServletResponse resp) {
		/*
		 *  1. obtain activation code
		 *  2. send activationCode to service
		 *     if not success activated, return false, save to request, forward to msg.jsp
		 *  3. save success to request, forward to msg.jsp
		 */
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "Activation Success, please login!");
		} catch (UserException e) {
			// TODO Auto-generated catch block
			req.setAttribute("code", "error");
			req.setAttribute("msg", e.getMessage());
		}
		
		return "f:/jsps/msg.jsp";
	}
	
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. integrate data to User
		 * 2. validate
		 * 3. send to service, get User
		 * 4. check if user exist, if not:
		 *    *save wrong info, user name or password wrong 
		 *    *save user data to refill
		 *    *forward to login.jsp
		 * 5. if exist, check status, if status false
		 *    *save wrong info, user not activate
		 *    *save user data to refill
		 *    *forward to login.jsp
		 * 6. login success
		 * 	  *save user info to session
		 * 	  *save user to cookie
		 *    *forward to index.jsp
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		//2. validate
		Map<String, String> errors = validateLogin(formUser, req.getSession());
		if(errors.size()>0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		//3. send to service
		User user = userService.login(formUser);
		
		//4. check
		if(user==null) {
			req.setAttribute("msg", "user name or password wrong!");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		} 
		else {
			if(!user.isStatus()) {
				req.setAttribute("msg", "user not activated!");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp"; 
			}
			else {
				req.getSession().setAttribute("sessionUser", user);
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");
				Cookie cookie = new Cookie("loginname", loginname);
				cookie.setMaxAge(60000);
				resp.addCookie(cookie);
				return "f:/index.jsp";
			}
			
		}
	}
	
	private Map<String,String> validateLogin(User formUser, HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
				
		/*
		 * 1. 校验登录名
		 */
		String loginname = formUser.getLoginname();
		if(loginname == null || loginname.trim().isEmpty())
			errors.put("loginname", "用户名不能为空！");		
		else if(loginname.length()<3 || loginname.length()>20)
			errors.put("loginname", "用户名长度必须在3~20之间");		
		
		/*
		 * 2. password validate
		 */
		String psw  = formUser.getLoginpass();
		if(psw == null || psw.trim().isEmpty())
			errors.put("psw", "密码不能为空！");
		else if(psw.length()<3 || psw.length()>20)
			errors.put("psw", "密码长度必须在3 ~ 20之间!");
		/*
		 * 3. 验证码校验
		 */
		String verifyCode = formUser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空!!!!！");
		} else if(!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误!!!！");
		}
		return errors;
	}
	
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp ) {
		/*
		 * 1. complete data to user
		 * 2. obtain uid from session
		 * 3. send uid and user info to service
		 *   *if error, save error info to request, forward to pwd.jsp
		 * 4. save success info to request
		 * 5. forward to msg.jsp
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		if(user==null) {
			req.setAttribute("msg", "Still not login!");
			return "f:/jsps/user/login.jsp";
		}
		
		try {
			userService.updatePassword(user.getUid(), formUser.getLoginpass(), formUser.getNewloginpass());
			req.setAttribute("msg", "Update password successful!");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			// TODO Auto-generated catch block
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("user", formUser);
			return "f:/jsps/user/pwd.jsp";
		}
			
	}
	
	public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		req.getSession().invalidate();
		return "f:/index.jsp";
	}
}










