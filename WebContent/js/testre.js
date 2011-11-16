$(document).ready(
	function() {
		var s1 = '*stopped,reason="breakpoint-hit",disp="keep",bkptno="1",frame={addr="0x00029f38",func="heartbeat",args=[],file="src/main.c",fullname="/home/nesl/workspace_ee/CloudCompiler/build_env/src/main.c",line="95"},thread-id="1",stopped-threads="all" ';
		var reg_line = /line="(\d*)"/;
		var reg_reason = /reason="([^"]*)"/;
		alert(reg_line.exec(s1)[1]);
		alert(reg_reason.exec(s1)[1]);
		
		var test = "test\nthisisatest";
		alert(test.replace("\n", ""));
	}
);