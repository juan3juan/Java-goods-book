package goodsPrototype.admin.book.web.servlet;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.ImageIcon;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.itcast.commons.CommonUtils;
import goodsPrototype.book.domain.Book;
import goodsPrototype.book.service.BookService;
import goodsPrototype.category.domain.Category;
import goodsPrototype.category.service.CategoryService;


public class AdminAddBookServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		
		/*
		 * 1. commons-fileupload的上传三步
		 */
		//创建工具
		FileItemFactory factory = new DiskFileItemFactory();
		/*
		 * 2. 创建解析器对象
		 */
		ServletFileUpload sfu = new ServletFileUpload(factory);
		sfu.setFileSizeMax(80 * 1024); //设置单个上传的文件上限为80kb
		/*
		 * 3. 解析request得到List<FileItem>
		 */
		List<FileItem> fileItemList = null;
		try {
			fileItemList = sfu.parseRequest(request);
		} catch (FileUploadException e) {
			//如果抛出异常，说明单个文件超出80KB
			error("上传的文件超出了80KB",request,response);
			return ;
		}
		/*
		 * 4. 把List<FileItem>封装到Book对象中
		 * 首先把普通表单字段放到一个Map中，再把Map转换成Book和Category对象，再建立两者关系
		 */
		Map<String, Object> map = new HashMap<String, Object>();
		for(FileItem fileItem: fileItemList) {
			if(fileItem.isFormField()) //如果是普通表单字段
				map.put(fileItem.getFieldName(), fileItem.getString("UTF-8"));
		}
		Book book = CommonUtils.toBean(map, Book.class);
		Category category = CommonUtils.toBean(map, Category.class);
		book.setCategory(category);
		
		/*
		 * 4.2 把上传的图片保存起来
		 *  > 获取文件名：截取之
		 *  > 给文件添加前缀，使用uuid前缀，避免出现同名文件
		 *  > 校验图片的尺寸
		 *  > 校验图片的扩展名：只能是jpg
		 *  > 指定图片的保存路径，需要使用ServletContext#getRealPath()
		 *  > 保存
		 *  > 把图片的路径设置给Book对象
		 */
		// 获取文件名
		FileItem fileItem = fileItemList.get(1);
		String filename = fileItem.getName();
		// 截取文件名，英文部分浏览器上传的绝对路径
		int index = filename.lastIndexOf("\\");
		if(index != -1) {
			filename = filename.substring(index+1);
		}
		// 给文件名添加uuid前缀，避免文件同名现象
		filename = CommonUtils.uuid() + "_" + filename;
		// 校验文件名称的扩展名
		if(!filename.toLowerCase().endsWith(".jpg")) {
			error("上传的图片扩展名必须是JPG", request, response);
			return ;
		}
		// 校验图片的尺寸
		// 保存上传的图片，把图片new成图片对象：Image、Icon、ImageIcon、BufferedImage
		/*
		 * 保存图片
		 * 1. 获取真实路径
		 */
		String savepath = this.getServletContext().getRealPath("/book_img");
		/*
		 * 2. 创建目标文件
		 */
		File destFile = new File(savepath, filename);
		/*
		 * 3. 保存文件
		 */
		try {
			fileItem.write(destFile); //它会把临时文件重定向到指定的路径，再删除临时文件
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// 校验尺寸
		/*
		 * 1. 使用文件路径创建ImageIcon
		 * 2. 通过ImageIcon得到Image对象
		 * 3. 获取宽高来进行校验
		 */
		ImageIcon icon = new ImageIcon(destFile.getAbsolutePath());
		Image image = icon.getImage();
		if(image.getWidth(null) > 350 || image.getHeight(null) > 350) {
			error("上传图片尺寸过大！", request, response);
			destFile.delete();
			return ;
		}
		
		// 把图片路径设置给book对象
		book.setImage_w("book_img/" + filename);
		
		// -------------------------修改小图-----------------
		// 获取文件名
		fileItem = fileItemList.get(2);
		filename = fileItem.getName();
		// 截取文件名，英文部分浏览器上传的绝对路径
		index = filename.lastIndexOf("\\");
		if(index != -1) {
			filename = filename.substring(index+1);
		}
		// 给文件名添加uuid前缀，避免文件同名现象
		filename = CommonUtils.uuid() + "_" + filename;
		// 校验文件名称的扩展名
		if(!filename.toLowerCase().endsWith(".jpg")) {
			error("上传的图片扩展名必须是JPG", request, response);
			return ;
		}
		// 校验图片的尺寸
		// 保存上传的图片，把图片new成图片对象：Image、Icon、ImageIcon、BufferedImage
		/*
		 * 保存图片
		 * 1. 获取真实路径
		 */
		savepath = this.getServletContext().getRealPath("/book_img");
		/*
		 * 2. 创建目标文件
		 */
		destFile = new File(savepath, filename);
		/*
		 * 3. 保存文件
		 */
		try {
			fileItem.write(destFile); //它会把临时文件重定向到指定的路径，再删除临时文件
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		// 校验尺寸
		/*
		 * 1. 使用文件路径创建ImageIcon
		 * 2. 通过ImageIcon得到Image对象
		 * 3. 获取宽高来进行校验
		 */
		icon = new ImageIcon(destFile.getAbsolutePath());
		image = icon.getImage();
		if(image.getWidth(null) > 350 || image.getHeight(null) > 350) {
			error("上传图片尺寸过大！", request, response);
			destFile.delete();
			return ;
		}
		
		// 把图片路径设置给book对象
		book.setImage_b("book_img/" + filename);
		
		
		//------------调用service完成保存-----------------
		book.setBid(CommonUtils.uuid());
		BookService bookService = new BookService();
		bookService.add(book);
		
		// 保存成功信息转发到msg.jsp
		request.setAttribute("msg", "添加图书成功");
		request.getRequestDispatcher("/adminjsps/msg.jsp").forward(request, response);
	}
	
	private void error(String msg, HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		request.setAttribute("msg", msg);
		request.setAttribute("parents", new CategoryService().findParents()); //所有一级分类
		request.getRequestDispatcher("/adminjsps/admin/book/add.jsp");
	}

}
