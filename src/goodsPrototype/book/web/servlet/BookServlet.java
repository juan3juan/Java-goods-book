package goodsPrototype.book.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import goodsPrototype.book.domain.Book;
import goodsPrototype.book.service.BookService;
import goodsPrototype.pager.PageBean;
import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class BookServlet
 */
public class BookServlet extends BaseServlet {
	private BookService bookService = new BookService();
	
	/**
	 * find by bid
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String load(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String bid = req.getParameter("bid");
		Book book = bookService.load(bid);
		req.setAttribute("book", book);	
		return "f:/jsps/book/desc.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
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
		return "f:/jsps/book/list.jsp";
	}

}
