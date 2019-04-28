package goodsPrototype.cart.domain;

import java.math.BigDecimal;

import goodsPrototype.book.domain.Book;
import goodsPrototype.user.domain.User;

public class CartItem {
	private String cartItemId;
	private int quantity;
	private Book book;
	private User user;
	
	// 添加小计
	public double getSubtotal() {
		//使用BigDecimal无误差，必须使用String类型构造器
		BigDecimal b1 = new BigDecimal(book.getCurrPrice()+"");
		BigDecimal b2 = new BigDecimal(quantity+"");
		BigDecimal b3 = b1.multiply(b2);
		return b3.doubleValue();
	}
	public String getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	
}
