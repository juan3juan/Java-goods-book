function _change() {
	$("#vCode").attr("src", "/goodsPrototype/VerifyCodeServlet?a=" + new Date().getTime());
}