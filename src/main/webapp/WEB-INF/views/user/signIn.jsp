<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<link rel="shortcut icon" href="data:image/x-icon" type="image/x-icon">
<title>Insert title here</title>
</head>
<body>
	<h1>회원가입을 해보자</h1>
	<label>아이디 : </label>
	<input type="text" id="loginId"></input>
	<label>비밀번호 : </label>
	<input type="password" id="password"></input>
	<label>닉네임 : </label>
	<input type="text" id="nickName"></input>
	<label>이름 : </label>
	<input type="text" id="userName"></input>
	
	<button id="signInButton">회원가입</button>
	<button id="cancelButton">취소</button>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script>
	$(document).ready(function() {
		$('#signInButton')
			.on('click', func.signIn);
		$('#cancelButton')
			.on('click', func.returnMain)
	})
	
	const func = {
		signIn : function() {
			const loginId = $('#loginId').val();
			const password = $('#password').val();
			const nickName = $('#nickName').val();
			const userName = $('#userName').val();
			
			const data = {
				loginId: loginId,
				password: password,
				nickName: nickName,
				userName: userName
			};
			
			 $.ajax({
                 type: 'POST',
                 url: '/signIn',
                 contentType: 'application/json;charset=UTF-8',
                 data: JSON.stringify(data),
                 success: function(response) {
                     console.log(response);
                   	 location.href="/loginView";
                 },
                 error: function(error) {
                     console.error(error);
                 }
             });
		},
		returnMain : function() {
			location.href = "/loginView";
		}
	}
</script>	
</body>
</html>