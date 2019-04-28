package goodsPrototype.pager;

import java.util.List;
/**
 * pageBean(分页bean),在各层之间传递数据！
 * @author Jihan
 *
 * @param <T>
 */
public class PageBean<T> {
	private int pc; // pageCode, current page
	private int tr; // totalRecord
	private int ps; // pageSize
	private String url; // eg. BookServlet?method=findXXX&cid=1&bname=2
	private List<T> beanList; //current page data, use T, book can be change to other type
	
	// calculate tp
	public int getTp() {
		int tp = tr/ps;
		return tr%ps==0 ? tp:tp+1;
	}
	
	public int getPc() {
		return pc;
	}
	public void setPc(int pc) {
		this.pc = pc;
	}
	public int getTr() {
		return tr;
	}
	public void setTr(int tr) {
		this.tr = tr;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<T> getBeanList() {
		return beanList;
	}
	public void setBeanList(List<T> beanList) {
		this.beanList = beanList;
	}
}
