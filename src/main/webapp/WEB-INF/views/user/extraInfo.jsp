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
	<h1>닉네임을 수정할 수 있다. 이름도 넣어야함</h1>
	<label>닉네임 : </label>
	<input value="${nickName }" id="nickName"></input>
	<label>이름 : </label>
	<input id="userName"></input>
	<button id="updateButton">수정</button>
	<button id="cancelButton">취소</button>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script>
	$(document).ready(function() {
		$('#updateButton')
			.on('click', func.signIn);
		$('#cancelButton')
			.on('click', func.returnMain)
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
                   	 location.href="/main";
                 },
                 error: function(error) {
                     console.error(error);
                 }
             });
		},
		returnMain : function() {
			location.href = "/mainView";
		}
	}
</script>
</body>
</html>