<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>register</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link rel="stylesheet" type="text/css" href="<c:url value='/css/css.css'/>">
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/user/register.css'/>">
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/js/common.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/jsps/js/user/register.js'/>"></script>
  </head>
  
  <body>
  <div id="divMain">
    <div id="divTitle">
      <span id="spanTitle">Register</span>
    </div>
  	<div id="divBody">
  	 <form action="<c:url value='/UserServlet'/>" method="post" id="registerForm">
  	  <input type="hidden" name="method" value="register"/>
  		<table id="tableForm">
  		  <tr>
  		    <td class="tdText">User Name:</td>
  		    <td class="tdInput">
  		    	<input class="inputClass" type="text" name="loginname" id="loginname" value="${form.loginname }"/>
  		    </td>
  		    <td class="tdError">
  		    	<label class="errorClass" id="loginnameError">${errors.loginname }</label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td class="tdText">User Password:</td>
  		    <td>
  		    	<input class="inputClass" type="password" name="loginpass" id="loginpass" value="${form.loginpass }"/>
  		    </td>
  		    <td>
  		    	<label class="errorClass" id="loginpassError">${errors.loginpass }</label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td class="tdText">Confirm Password:</td>
  		    <td>
  		    	<input class="inputClass" type="password" name="reloginpass" id="reloginpass" value="${form.reloginpass }"/>
  		    </td>
  		    <td>
  		    	<label class="errorClass" id="reloginpassError">${errors.reloginpass }</label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td class="tdText">Email:</td>
  		    <td>
  		    	<input class="inputClass" type="text" name="email" id="email" value="${form.email }"/>
  		    </td>
  		    <td>
  		    	<label class="errorClass" id="emailError">${errors.email }</label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td class="tdText">Verify Code:</td>
  		    <td>
  		    	<input class="inputClass" type="text" name="verifyCode" id="verifyCode" value="${form.verifyCode }"/>
  		    </td>
  		    <td>
  		    	<label class="errorClass" id="verifyCodeError">${errors.verifyCode }</label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td></td>
  		    <td>
  		    	<div id="divVerifyCode"><img id="vCode" src="<c:url value='/VerifyCodeServlet'/>"/></div>
  		    </td>
  		    <td>
  		    	<label><a href="javascript:_changeVerifyCode()">update</a></label>
  		    </td>
  		  </tr>
  		  <tr>
  		    <td></td>
  		    <td>
  		    	<input type="image" src="<c:url value='/images/regist1.jpg'/>" id="submitBtn"/>
  		    </td>
  		    <td>
  		    	<label></label>
  		    </td>
  		  </tr>
  		</table>
  	 </form>
  	</div>
  </div>

  </body>
</html>
	