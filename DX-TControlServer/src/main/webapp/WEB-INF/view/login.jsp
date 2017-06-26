<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="ui" uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link rel="stylesheet" type="text/css" href="../css/common.css">
<script type="text/javascript" src="../js/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="../js/common.js"></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>eXperDB</title>
<script type="text/javascript">

	function fn_login(){
			var strid = document.getElementById('usr_id').value;
			var strpw = document.getElementById('pwd').value; 
			 
			if (strid == "" || strid == "undefind" || strid == null)
			{
				alert("아이디를 넣어주세요");
				document.getElementById('usr_id').focus();
				return false;
			}
			if (strpw == "" || strpw == "undefind" || strpw == null){
					alert("비밀번호를 넣어주세요");
					document.getElementById('usr_id').focus();
					return false;
			}
	    	document.loginForm.action = "<c:url value='/login.do'/>";
	       	document.loginForm.submit();
		}


	$(window.document).ready(
			function() {				
				 document.getElementById("usr_id").focus();		
			});

</script>

</head>

<body class="bg">
	<form name="loginForm" id="loginForm" method="post">
	<div id="login_wrap">
		<div class="inr">
			<div class="logo"><img src="../images/login_logo.png" alt="eXperDB"></div>
			<div class="inp_bx">
				<p class="tit">MEMBER LOGIN</p>
				<div class="inp_wrap t1">
					<label for="member_id">ID</label>
					<input type="text" class="txt" id="usr_id" name="usr_id" title="아이디 입력" maxlength="" placeholder="아이디를 입력하세요."/>
				</div>
				<div class="inp_wrap t2">
					<label for="member_pwd">Password</label>
					<input type="password" class="txt" id="pwd" name="pwd" title="비밀번호 입력" maxlength="" placeholder="비밀번호를 입력하세요." />
				</div>
				<div class="btn_wrap">
					<button onClick="fn_login()">LOGIN</button>
				</div>
			</div>
		</div>
	</div>
</form>

<%-- 	<form name="loginForm" id="loginForm" method="post">
 		<div class="login-page">
			  <div class="form">
			    <img src="<c:url value='/images/egovframework/example/DX-Tcontrol_title.png'/>" alt="DX-TCONTROL" />
			    <br><br>
			      <input type="text" placeholder="id" id="usr_id"  name="usr_id" />
			      <input type="password" placeholder="password" id="pwd" name="pwd"/>
			      <button onClick="fn_login()">login</button>
			      <c:out value="${loginException.message}"/>
			  </div>
				<div class="footer">
				<br>
					<strong>Copyright (c) 2017 K4m Corp.  All rights reserved.</strong>
			  </div>
		</div>
	</form> --%>
	
</body>
</html>