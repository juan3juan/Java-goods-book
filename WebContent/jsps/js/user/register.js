$(function() {
	/*
	 *  1. obtain the error info, loop
	 */
	$(".errorClass").each(function() {
		showError($(this));
	});
	
	/*
	 *  2. change button img
	 */
	$("#submitBtn").hover(
			/* hover first param, when mouse move in */
			function() {
				$("#submitBtn").attr("src", "/goods/images/regist2.jpg");
			},
			/* hover second param, when mouse move out */
			function() {
				$("#submitBtn").attr("src", "/goods/images/regist1.jpg");
			}
		);
	/*
	 *  3. input focus hide error info
	 */
	$(".inputClass").focus(function() {
		var labelId = $(this).attr("id")+"Error";
		$("#"+labelId).text(""); //clear the label content
		showError($("#"+labelId));
	});
	
	/*
	 *  4. input blur with check again 
	 */
	$(".inputClass").blur(function() {
		var id = $(this).attr("id");
		var funName = "validate" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";
		eval(funName);
	});
	
	/*
	 *  5. submit validate
	 */
	$("#registerForm").submit(function() {
		var bool = true;
		$(".inputClass").each(function() {
			var id = $(this).attr("id");
			var funName = "validate" + id.substring(0,1).toUpperCase() + id.substring(1) + "()";
			if(!eval(funName))
				bool = false;
		});
		return bool;
	});
});

/*
 *  validate User name
 */
function validateLoginname() {
	var id = "loginname";
	var value = $("#"+id).val(); //obtain input content	
	/*
	 *  1. not none
	 */
	if(!value) 
	{
		/*
		 * obtain label
		 * add error info
		 * show label
		 */
		$("#" + id + "Error").text("User Name can't be null!");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  2. length validate
	 */
	if(value.length<3 || value.length>20)
	{
		$("#" + id + "Error").text("user name has to be between 3-20!");		
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  3. register or not
	 */
	$.ajax({
		url:"/goodsPrototype/UserServlet",
		data:{method:"ajaxValidateLoginname", loginname:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(!result){ // validate fail
				$("#" + id + "Error").text("User Name already exist!");
				showError($("#" + id + "Error"));
				return false;
			}
		}		
	});
	return true;
}

/*
 *  validate password
 */
function validateLoginpass() {
	var id = "loginpass";
	var value = $("#"+id).val(); //obtain input content	
	/*
	 *  1. not none
	 */
	if(!value) 
	{
		/*
		 * obtain label
		 * add error info
		 * show label
		 */
		$("#" + id + "Error").text("password can't be null!");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  2. length validate
	 */
	if(value.length<3 || value.length>20)
	{
		$("#" + id + "Error").text("password has to be between 3-20!");		
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  3. register or not
	 */
	return true;
}

/*
 *  validate repass
 */
function validateReloginpass() {
	var id = "reloginpass";
	var value = $("#"+id).val(); //obtain input content	
	/*
	 *  1. not none
	 */
	if(!value) 
	{
		/*
		 * obtain label
		 * add error info
		 * show label
		 */
		$("#" + id + "Error").text("confirm password can't be null!");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  2. length validate
	 */
	if(value != $("#loginpass").val())
	{
		$("#" + id + "Error").text("two passwords have to be the same!");		
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  3. register or not
	 */

	
	return true;
}

/*
 *  validate email
 */
function validateEmail() {
	var id = "email";
	var value = $("#"+id).val(); //obtain input content	
	/*
	 *  1. not none
	 */
	if(!value) 
	{
		/*
		 * obtain label
		 * add error info
		 * show label
		 */
		$("#" + id + "Error").text("email can't be null!");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  2. length validate
	 */
	if(!/^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\.[a-zA-Z0-9_-]{2,3}){1,2})$/.test(value)) {	
		$("#" + id + "Error").text("email format is not correct!");		
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  3. register or not
	 */
	$.ajax({
		url:"/goodsPrototype/UserServlet",
		data:{method:"ajaxValidateEmail", email:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			if(!result){ // validate fail
				$("#" + id + "Error").text("email already used!");
				showError($("#" + id + "Error"));
				return false;
			}
		}		
	});
	return true;
}

/*
 *  validate verify code
 */
function validateVerifyCode() {
	var id = "verifyCode";
	var value = $("#"+id).val(); //obtain input content	
	/*
	 *  1. not none
	 */
	if(!value) 
	{
		/*
		 * obtain label
		 * add error info
		 * show label
		 */
		$("#" + id + "Error").text("verify code can't be null!");
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  2. length validate
	 */
	if(value.length != 4)
	{
		$("#" + id + "Error").text("length is not right!");		
		showError($("#" + id + "Error"));
		return false;
	}
	/*
	 *  3. register or not
	 */
	$.ajax({
		url:"/goodsPrototype/UserServlet",
		data:{method:"ajaxValidateVerifyCode", verifyCode:value},
		type:"POST",
		dataType:"json",
		async:false,
		cache:false,
		success:function(result){
			alert("XXX-1");
			if(!result){ // validate fail
				alert("XXX-2");

				$("#" + id + "Error").text("verify code wrong!");
				showError($("#" + id + "Error"));
				return false;
			}
		}		
	});	
	return true;
}

/*
 * if current element has content,display;
 * if not, don't display
 */
function showError(ele) {
	var text = ele.text();
	if(!text)
		ele.css("display","none");
	else
		ele.css("display","");
}

/*
 * change verify code
 */
function _changeVerifyCode(){
	/*
	 * 1. obtain<img>
	 * 2. reset src
	 * 3. ms to update
	 */
	$("#vCode").attr("src", "/goodsPrototype/VerifyCodeServlet?a=" + new Date().getTime());
}

