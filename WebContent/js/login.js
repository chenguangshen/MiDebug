$(function() {
	$("#commit").click(function(evt) {
		evt.preventDefault();
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/GitHubConnector",
			async: false,
			data : {
				type: "login",
				username: $("#login_field").val(), 
				password: $("#password").val()
			},
			dataType : "text",
			async : false,
			success : function(data) {
				//alert(data);
			}
		});
		window.location = "https://github.com/login/oauth/authorize?client_id=b5bb4b5f001929ffe04c";
	});
});