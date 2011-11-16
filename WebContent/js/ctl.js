var projList = {};
var fileList = {};
var curProject = null;
var curFile = null;
var update = 0;
var compiled = 0;
var debugging = 0;
var localdebug = 0;
var bks = {};
var curline = 0;
var errorFlag = 0;
var buffer;
var curline;
var platform = 0;
var reg_line = /line="(\d*)"/;
var reg_file = /file="([^"]*)"/;
var reg_addr = /addr="([^"]*)"/;
var reg_func = /func="([^"]*)"/;
var reg_level = /level="([^"]*)"/;
var reg_err = /:(\d+):[\d*:]* error:/;
var reg_cinfo = /([Building File]|Invoking):/;

/*plugin = plugin0;

function plugin0() {
	return document.getElementById('plugin0');
}*/

$(document).ready(function() {
				/*	if(plugin().add(1, 2) == 3){
						//alert("plugin works!");
					}*/					
					$.ajax({
						type : "GET",
						url : "http://127.0.0.1:8080/CloudCompiler/WebSocketServlet",
						dataType : "text",
						async : false,
						success : function(data) {
							if(data == 1){
								//alert("application registered!");
							}
						}
					});
					
					$("#platform").change(function(evt){
						platform = this.selectedIndex;
						//alert(platform);
					});
					
					$("#treeDiv1")
							.jstree(
									{
										"core" : {},
										"plugins" : [ "themes", "html_data",
												"crrm", "ui" ]
									})
							.bind(
									"loaded.jstree",
									function(event, data) {
										$
												.ajax({
													type : "GET",
													url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
													data : "type=listpj&name=all",
													dataType : "text",
													success : function(data) {
														// document.write(msg);
														// alert(data);
														projList = data.split(
																" ").sort();
														// alert(projList);
														for ( var elem in projList) {
															var str = projList[elem];
															// alert(str);
															$("#treeDiv1")
																	.jstree(
																			"create",
																			null,
																			false,
																			{
																				attr : {
																					id : str
																				},
																				data : {
																					title : str
																				}
																			},
																			false,
																			true);

														}
														for ( var elem2 in projList) {
															var str = projList[elem2];
															$
																	.ajax({
																		type : "GET",
																		url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
																		data : "type=listpj&name="
																				+ str,
																		dataType : "text",
																		async : false,
																		success : function(
																				data) {
																			// document.write(msg);
																			// alert(data);
																			var idtag = "#"
																					+ str;
																			fileList = data
																					.split(
																							" ")
																					.sort();

																			if (fileList != {}) {
																				for ( var elem1 in fileList) {
																					var str1 = fileList[elem1];
																					if (str1 != "") {
																						// alert(idtag
																						// + "
																						// " +
																						// str1);
																						$(
																								"#treeDiv1")
																								.jstree(
																										"create",
																										$(idtag),
																										"inside",
																										{
																											attr : {
																												id : str1
																											},
																											data : {
																												title : str1,
																												icon : "../CloudCompiler/image/file.png"
																											}
																										},
																										false,
																										true);
																					}
																				}
																			}
																		}
																	});
														}
													}
												});
									}).bind(
									"select_node.jstree",
									function(event, data) {
										// alert(data.rslt.obj.attr("id"));
										if (data.inst._get_parent() == -1) {
											curProject = data.rslt.obj
													.attr("id");
											// $("#header").html(curProject);
											curFile = null;
										} else {
											curProject = data.inst._get_parent(
													data.rslt.obj).attr("id");
											curFile = data.rslt.obj.attr("id");// data.inst._get_parent(data.rslt.obj).attr("id");
										}
										$("#header").html(curProject);
									});
					$("#treeDiv1")
							.delegate(
									"a",
									"dblclick",
									function() {

										if (curProject != null
												&& curFile != null) {
											$("#header").append("/" + curFile);
											$
													.ajax({
														type : "GET",
														url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
														data : "type=getfile&pjname="
																+ curProject
																+ "&fname="
																+ curFile,
														dataType : "text",
														async : false,
														success : function(data) {
															// document.write(msg);
															// alert(data);
															if (data != -1) {
																// alert(data);
																editor
																		.setValue(data);
																editor.focus();
																$("#header_chg")
																		.html(
																				"(Saved)");
															}
														}
													});
										}
									});
				});

$(function() {
	$("#bt_newpj")
			.click(
					function(evt) {
						evt.preventDefault();
						// alert("new pj clicked");
						var pname = prompt("Input name for the new project:",
								"");
						if (pname) {
							$
									.ajax({
										type : "GET",
										url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
										data : "type=newpj&name=" + pname,
										dataType : "text",
										async : false,
										success : function(data) {
											// document.write(msg);
											// alert(data);
											if (data == 0) {
												$("#treeDiv1").jstree("create",
														-1, false, {
															attr : {
																id : pname
															},
															data : {
																title : pname
															}
														}, false, true);
												alert("Successfully create new project!");
											} else {
												alert("Failed to create new project: existing project name.");
											}
										}
									});
						}

					});
});

$(function() {
	$("#bt_new")
			.click(
					function(evt) {
						evt.preventDefault();
						// alert("new file clicked");
						if (curProject != null) {
							var fname = prompt("Input name for the new file:");
							// alert(fname);
							if (fname) {
								// alert(curProject + " " + fname);
								$
										.ajax({
											type : "GET",
											url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
											data : "type=newfile&pjname="
													+ curProject + "&fname="
													+ fname,
											dataType : "text",
											async : false,
											success : function(data) {
												// document.write(msg);
												// alert(data);
												if (data == 0) {
													var curidtag = "#"
															+ curProject;
													$("#treeDiv1")
															.jstree(
																	"create",
																	$(curidtag),
																	false,
																	{
																		attr : {
																			id : fname
																		},
																		data : {
																			title : fname,
																			icon : "../CloudCompiler/image/file.png"
																		}
																	}, false,
																	true);
													alert("Successfully create new file!");
												} else {
													alert("Failed to create new project: existing file name.");
												}
											}
										});
							}
						} else {
							alert("Please select a project folder!");
						}
					});
});

$(function() {
	$("#code").change(function(evt) {
		alert("code change!");
	});
});

$(function() {
	$("#bt_save").click(function(evt) {
		evt.preventDefault();
		processSave();
	});
});

function processSave() {
	// alert("save");
	if (curProject != null && curFile != null) {
		compiled = 0;
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/ProjectManager",
			data : {
				type : "savefile",
				pjname : curProject,
				fname : curFile,
				content : editor.getValue()
			},
			dataType : "text",
			async : false,
			success : function(data) {
				if (data == 0) {
					update = 1;
					$("#header_chg").html("(Saved)");
					alert("Successfully saved!");
				} else {
					alert("Error saving file.");
				}
			}
		});
	} else {
		alert("Please select file.");
	}
}

$(function() {
	$("#bt_compile")
			.click(
					function(evt) {
						evt.preventDefault();
						errorFlag = 0;
						if (curProject != null && curFile != null) {
							// alert("compile");
							$("#status").html("Compiling...");
							// if(update == 0){
							// var r = confirm("You have modified the file, do
							// you want to save before compile?");
							// if(r == true){
							// processSave();
							// }
							// }
							$
									.ajax({
										type : "GET",
										url : "http://127.0.0.1:8080/CloudCompiler/Compiler",
										data : "type=compile&pjname="
												+ curProject + "&fname="
												+ curFile + "&platform=" + platform,
										dataType : "text",
										async : false,
										success : function(data) {
											// alert(data);
											// $("#console").append(data.replace("\\n",
											// "<br>"));
											data = data.split("\n");
											for ( var elem in data) {
												var str = data[elem];

												if (reg_cinfo.test(str)) {
													$("#console").append(
															str + "<br>");
												}

												if (reg_err.test(str)) {
													errorFlag = 1;
													var elems = reg_err
															.exec(str);
													// alert(elems[1]);
													curline = editor
															.setLineClass(
																	parseInt(elems[1]) - 1,
																	"errorline");
													$("#console").append(
															str + "<br>");
												}
											}
											if (errorFlag == 0) {
												alert("Compile successful!");
												compiled = 1;
											} else {
												alert("Compile failed, see console for errors.");
											}
											$("#status").html("Ready");
										}
									});
						} else {
							alert("Please select a project");
							$("#status").html("Ready");
						}
					});
});




function parse_info(data){
	data = data.replace("\n", "");
	var addr = 0, func = 0, file = 0, line = 0, level = 0;
	var res;
	if ((res = reg_line.exec(data)) != null) {
		line = res[1];
	}

	if ((res = reg_addr.exec(data)) != null) {
		addr = res[1];
	}

	if ((res = reg_func.exec(data)) != null) {
		func = res[1];
	}

	if ((res = reg_level.exec(data)) != null) {
		level = res[1];
	}

	if ((res = reg_file.exec(data)) != null) {
		file = res[1];
	}

	alert("Debugging Information:"
			+ "\nFile: " + file
			+ "\nFunction: " + func
			+ "\nLine: " + line
			+ "\nAddress: " + addr
			+ "\nRecursion Level: " + level);
}
/*
var device_location = "127.0.0.1";
var ws;

function create_websocket(){
	plugin().set_sockfd(0);
	
	var url = 'ws://127.0.0.1:8080/CloudCompiler/localdebug';
	alert(url);
	ws = new WebSocket(url);
	ws.onopen = function(event) {
		alert("Websocket opened!");
		$.ajax({
			type : "POST",
			url  : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : "type=startlocal",
			dataType : "text",
			async : false,
		});
	};
	ws.onmessage = function(event) {
		//alert(event.data);
		var rdata = event.data;
		if(rdata == "EOF"){
			//alert("Enter local debug mode.");
			$("#status").html("Local Debugging...");
			localdebug = 1;
		}
		else if(rdata == "HALT_EOF"){
			//alert("Device halted.");
		}
		else if(rdata == "FILE_EOF"){
			alert("Enter debug mode, device halt, and load symble file.");
		}
		else if(rdata == "BK_EOF"){
			alert("Set breakpoint: " + curProject + "/" + curFile + " at line " + curline);
			curcm.setMarker(curline - 1, "<span style=\"color: #900\">B</span> %N%");
		}
		else if(rdata.indexOf("CONT_EOF") >= 0){
			parse_cont(rdata);
		}
		else if(rdata.indexOf("INFO:") >= 0){
			parse_info(rdata);
		}
		else if(rdata.indexOf("FRAME:") >= 0){
			alert(rdata);
		}
		else if (rdata.indexOf("PRINT_RES") >= 0){
			alert(rdata);
		}
		else{
			ws.send(plugin().send_recv(rdata));
		}
	};
	ws.onclose = function(event) {
		
	};
}
*/
var selectdebug;

$(function() {
	$("#debug_select").click(function(evt) {
		evt.preventDefault();
//		if(curProject != null && compiled == 1){
//			
//		}
//		else{
//			alert("Please select a project and compile it.");
//			$("#status").html("Ready");
//		}
		device_location = $('form[id="device_list"] input[type="radio"]:checked').val();
		$.modal.close();
		if(device_location == "local_device"){
			create_websocket();
		}
		else{
			if(selectdebug == 1){
				alert("Target location: " + device_location);
				if(device_location != null && device_location !=""){
					$("#status").html("Debugging");
					debugging = 1;
					//alert("Plug-in device and press OK below.");
					$.ajax({
						type : "GET",
						url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
						data : "type=start&pjname=" + curProject + "&fname=" + curFile  + "&platform=" + platform + "&target=" + device_location,
						dataType : "text",
						async : false,
						success : function(data) {
							data = data.split("\n");
							for ( var elem in data) {
								var str = data[elem];
								$("#console").append(str + "<br>");
							}
							alert("Enter debug mode, device halt, and load symble file.");
						}
					});
				}
			}
			else{
				alert("RUN!Target location: " + device_location);
				$.ajax({
					type : "GET",
					url : "http://127.0.0.1:8080/CloudCompiler/RemoteConnector",
					data : "platform=" + platform + "&target=" + device_location,
					dataType : "text",
					async : false,
					success : function(data) {
						if (data.indexOf("EOF_RUN") >= 0) {
							alert("Flash and run successful!");
							$("#status").html("Ready");
						}
				   }
				});
			}
			
		}
	});
});

$(function() {
	$("#bt_run").click(function(evt) {
		evt.preventDefault();
		if (curProject != null && compiled == 1 && debugging == 0) {
			$("#status").html("Flashing & Running...");
			alert("Plug in device and press OK to run.");	
			$.ajax({
				type : "GET",
				url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
				data : "type=probe_device",
				dataType : "text",
				async : false,
				success : function(data) {
					if(data != null){
						$("#device_list").html("");
						data = data.split(" ");
						for (var elem in data) {
							var str = data[elem];
							if(str != null && str != "")
								$("#device_list").append('<input type="radio" name="device_location" value="' + str + '"/> Device on ' + (str == "127.0.0.1" ? "Server" : str) +'<br />');
						}
						$("#device_list").append('<input type="radio" name="device_location" value="local_device"/> Local Device <br />');
					}
				}
			});
			$('#basic-modal-content').modal();
			selectdebug == 0;
			
		} else {
			alert("Please select a project and compile it.");
			$("#status").html("Ready");
		}
	});
});

$(function() {
	$("#bt_debug").click(function(evt) {
		evt.preventDefault();
		$.ajax({
			type : "GET",
			url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : "type=probe_device",
			dataType : "text",
			async : false,
			success : function(data) {
				if(data != null){
					$("#device_list").html("");
					data = data.split(" ");
					for (var elem in data) {
						var str = data[elem];
						if(str != null && str != "")
							$("#device_list").append('<input type="radio" name="device_location" value="' + str + '"/> Device on ' + (str == "127.0.0.1" ? "Server" : str) +'<br />');
					}
					$("#device_list").append('<input type="radio" name="device_location" value="local_device"/> Local Device <br />');
				}
			}
		});
		$('#basic-modal-content').modal();
		selectdebug == 1;
	});
});

function parse_cont(data){
	data = data.replace("\n", "");
	$("#console").append(data);
	var res = reg_line.exec(data);
	if (res != null) {
		var line = res[1];
		if (line != null) {
			alert("Breakpoint Hit. Line: " + line);
			editor.setLineClass(curline, null);
			curline = editor.setLineClass(parseInt(line) - 1, "currentline");
		}
	}
}

$(function() {
	$("#debug_cont")
			.click(
					function(evt) {
						evt.preventDefault();
						if (debugging == 1) {
							//alert("cont debug");
							$
									.ajax({
										type : "GET",
										url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
										data : "type=cont&pjname=" + curProject
												+ "&fname=" + curFile,
										dataType : "text",
										async : false,
										success : function(data) {
											parse_cont(data);
										}
									});
						}
						else if(localdebug == 1){
							opt_dispatch(5);
						}
					});
});

function next_parse(data){
	// alert(data);
	data = data.replace("\n", "");
	// for(var elem in data){
	// var str = data[elem];
	var res = reg_line.exec(data);
	if (res != null) {
		var line = res[1];
		if (line != null) {
			editor.setLineClass(
					curline, null);
			curline = editor
					.setLineClass(
							parseInt(line) - 1,
							"currentline");
		}
	}
}

$(function() {
	$("#debug_next")
			.click(
					function(evt) {
						evt.preventDefault();
						if (debugging == 1) {
							//alert("next debug");
							$.ajax({
										type : "GET",
										url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
										data : "type=next&pjname=" + curProject
												+ "&fname=" + curFile,
										dataType : "text",
										async : false,
										success : function(data) {
											next_parse(data);
										}
							});
						}
						else if (localdebug == 1){
							opt_dispatch(6);
						}
					});
});

$(function() {
	$("#debug_info")
			.click(
					function(evt) {
						evt.preventDefault();
						if (debugging == 1) {
							//alert("info debug");
						$
								.ajax({
									type : "GET",
									url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
									data : "type=info&pjname=" + curProject
											+ "&fname=" + curFile,
									dataType : "text",
									async : false,
									success : function(data) {
										parse_info(data);
									}
								});
						}
						else if (localdebug == 1){
							opt_dispatch(7);
						}
					});
});

var curcm;

function insertBreak(lineNum, cm) {
	curline = lineNum + 1;
	curcm = cm;
	if (debugging == 1) {
		// alert("quit debug");
		$.ajax({
			type : "GET",
			url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : "type=bk&line=" + (lineNum + 1),
			dataType : "text",
			async : false,
			success : function(data) {
				alert("Set breakpoint: " + curProject + "/" + curFile
						+ " at line " + (lineNum + 1));
				cm.setMarker(lineNum,
						"<span style=\"color: #900\">B</span> %N%");
				// alert(data);
				$("#console").append(data);
				// data = data.split("\n");
				// for(var elem in data){
				// var str = data[elem];
				// $("#console").append(str + "<br>");
				// }
			}
		});
	}
	else if(localdebug == 1){
		//alert("Insert breakpoint for local device!!!");
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : {
				type: "bklocal",
				line: (lineNum + 1)
			},
			dataType : "text",
			async : false,
		});
	}
}

$(function() {
	$("#debug_quit").click(function(evt) {
		evt.preventDefault();
		if (debugging == 1) {
			alert("quit debug");
			$.ajax({
				type : "GET",
				url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
				data : "type=quit&pjname=" + curProject + "&fname=" + curFile,
				dataType : "text",
				async : false,
				success : function(data) {
					alert("Exiting debug mode...");
					debugging = 0;
					for ( var i = 1; i <= editor.lineCount(); i++) {
						editor.setLineClass(i - 1, null);
					}
					// editor.setLineClass(0, "activeline");
					$("#console").append("Exiting debug mode...");
					$("#status").html("Ready");
				}
			});
		}
		else if (localdebug == 1){
			
		}
	});
});

$(function() {
	$("#debug_print")
			.click(
					function(evt) {
						evt.preventDefault();
						if(debugging == 1){
							var vname = prompt("Input variable/register/memory address to display:","");
							if (vname != null) {
								$.ajax({
									type : "GET",
									url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
									data : "type=print&vname=" + vname,
									dataType : "text",
									async : false,
									success : function(data) {
										alert(data);
									}
								});
							}
						}
						else if (localdebug == 1){
							var vname = prompt("Input variable/register/memory address to display:", "");
							$.ajax({
								type : "POST",
								url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
								data : {
									type: "printlocal",
									vname: vname
									
								},
								dataType : "text",
								async : false,
							});
						}
					});
});

$(function() {
	$("#debug_frame").click(function(evt) {
		evt.preventDefault();
		if(debugging == 1){
			$.ajax({
				type : "GET",
				url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
				data : "type=lframe",
				dataType : "text",
				async : false,
				success : function(data) {
					alert(data);
					data = data.split("\n");
					for ( var elem in data) {
						var str = data[elem];
						$("#console").append(str + "<br>");
					}
				}
			});
		}
		else if(localdebug == 1){
			opt_dispatch(8);
		}
	});
});

var lastPos = null, lastQuery = null, marked = [];

function unmark() {
	for ( var i = 0; i < marked.length; ++i)
		marked[i]();
	marked.length = 0;
}

function search(text) {
	unmark();
	if (!text)
		return;
	for ( var cursor = editor.getSearchCursor(text); cursor.findNext();)
		marked.push(editor.markText(cursor.from(), cursor.to(), "searched"));

	if (lastQuery != text)
		lastPos = null;
	var cursor = editor.getSearchCursor(text, lastPos || editor.getCursor());
	if (!cursor.findNext()) {
		cursor = editor.getSearchCursor(text);
		if (!cursor.findNext())
			return;
	}
	editor.setSelection(cursor.from(), cursor.to());
	lastQuery = text;
	lastPos = cursor.to();
}

function replace(text, replace) {
	unmark();
	if (!text)
		return;
	for ( var cursor = editor.getSearchCursor(text); cursor.findNext();)
		cursor.replace(replace);
}

$(function() {
	$("#bt_search").click(function(evt) {
		evt.preventDefault();
		var str = prompt("Input target text:", "");
		if (str) {
			search(str);
		}
	});
});

$(function() {
	$("#bt_replace").click(function(evt) {
		evt.preventDefault();
		var str = prompt("Input target text:", "");
		if (str) {
			var str1 = prompt("Input text to replace target:", "");
			if (str1) {
				replace(str, str1);
			}
		}
	});
});

function opt_dispatch(type){
	var calltype = "haltlocal";
	if(type == 2){
		calltype = "haltlocal";
	}
	else if(type == 3){
		calltype = "filelocal";
		alert("File clicked");
	}
	else if(type == 4){
		calltype = "bklocal";
	}
	else if (type == 5){
		calltype = "contlocal";
	}
	else if (type == 6){
		calltype = "nextlocal";
	}
	else if (type == 7){
		calltype = "infolocal";
	}
	else if (type == 8){
		calltype = "framelocal";
	}
	if(localdebug == 1){
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : {
				type: calltype
			},
			dataType : "text",
			async : false,
		});
	}
}

//$(function() {
//	$("#debug_local_halt").click(function(evt) {
//		evt.preventDefault();
//		opt_dispatch(2);
//	});
//});
//
//$(function() {
//	$("#debug_local_file").click(function(evt) {
//		evt.preventDefault();
//		opt_dispatch(3);
//	});
//});

//$(function() {
//	$("#debug_local_cont").click(function(evt) {
//		evt.preventDefault();
//		opt_dispatch(5);
//	});
//});

$(function() {
	$("#debug_local_exit").click(function(evt) {
		evt.preventDefault();
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/Debugger",
			data : {
				type: "exitlocal"
			},
			dataType : "text",
			async : false,
			success : function(data) {
				alert("Exiting local debug mode.");
				$("#status").html("Ready");
				localdebug = 0;
			}
		});
	});
});

$(function() {
	$("#bt_commit").click(function(evt) {
		evt.preventDefault();
		$("#status").html("Commiting");
		$.ajax({
			type : "POST",
			url : "http://127.0.0.1:8080/CloudCompiler/GitHubConnector",
			data : {
				type: "commit",
				pjname: curProject,
				filename: curFile
			},
			dataType : "text",
			async : false,
			success : function(data) {
				//alert(data);
				$("#status").html("Ready");
				if (data.indexOf("OK") >= 0) {
					alert("Successful commited " + curProject + "/" + curFile + " to your GitHub repository");
					$("#console").append("Successful commited " + curProject + "/" + curFile + " to your GitHub repository");
				}
			}
		});
	});
});