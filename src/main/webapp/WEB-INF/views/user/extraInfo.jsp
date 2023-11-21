<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<h1>회원가입해라</h1>
	<label>닉네임 : </label>
	<input value="${nickName }" id="nickName"></input>
	<label>이름 : </label>
	<input id="userName"></input>
	<button id="signInButton">회원가입</button>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script>
	$(document).ready(function() {
		$('#signInButton')
			.on('click', func.signIn);
	})
	
	const func = {
		signIn : function() {
			const nickName = $('#nickName').val();
			const userName = $('#userName').val();
			
			const data = {
				nickName: nickName,
				userName: userName
			};
			
			 $.ajax({
                 type: 'POST',
                 url: '/kakaoLoginExtraInfo',
                 contentType: 'application/json;charset=UTF-8',
                 data: JSON.stringify(data),
                 success: function(response) {
                     console.log(response);
                     if(response == "accessTokenExpired") {
                    	alert("다시 회원가입을 시도해주세요");
                    	location.href="/loginView";
                     } else {
                    	 location.href="/redirect";
                     }
                 },
                 error: function(error) {
                     console.error(error);
                 }
             });
		}
	}
</script>
</body>
</html>