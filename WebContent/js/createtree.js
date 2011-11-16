	var tree; //will hold our TreeView instance
	var projlist = {};
	var tmpNode;
	var nodeList = {};
	var fileList = {};
	
	function treeInit() {
		
		
		var xmlhttp;
		if (window.XMLHttpRequest){
			// code for IE7+, Firefox, Chrome, Opera, Safari
  			xmlhttp=new XMLHttpRequest();
  		}
		else{
			// code for IE6, IE5
  			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
  		}
	
		xmlhttp.open("GET","http://127.0.0.1:8080/CloudCompiler/ProjectManager?type=listpj&name=all", true);
	
		xmlhttp.onreadystatechange=function(){
//		alert("ready state=" + xmlhttp.readyState + ", http status=" + xmlhttp.status);
  			if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
  				//document.write(xmlhttp.responseText);
  				if(xmlhttp.responseText){
  					//alert(xmlhttp.responseText + "*");
  	    			projlist = xmlhttp.responseText.split(" ");
  	    			alert(projlist);
  	    			buildTextNodeTree();
  	    			//document.getElementById("code").innerHTML=xmlhttp.responseText;
  				}
    		}
  		};
		xmlhttp.send(null);
		
		YAHOO.log("Example's treeInit function firing.", "info", "example");
		
		//Hand off ot a method that randomly generates tree nodes:
		//buildRandomTextNodeTree();
	}
	
		

	function buildTextNodeTree() {	
		//instantiate the tree:
		tree = new YAHOO.widget.TreeView("treeDiv1");
		tree.subscribe("clickEvent", function(node) {
			alert(node.data.label);
	        alert(node.data.myNodeId);
		});
		
		tree.subscribe("labelClick", function(node) {
	        alert(node.data.label);
	        alert(node.data.myNodeId);
	    });
		
		for(var elem in projlist){
			alert(projlist[elem]);
			tmpNode = new YAHOO.widget.TextNode(projlist[elem], tree.getRoot(), false);
			nodeList += tmpNode;
			
			xmlhttp=new XMLHttpRequest();
			xmlhttp.open("GET","http://127.0.0.1:8080/CloudCompiler/ProjectManager?type=listpj&name=" + projlist[elem], true);
			xmlhttp.onreadystatechange=function(){
//			alert("ready state=" + xmlhttp.readyState + ", http status=" + xmlhttp.status);
	  			if (xmlhttp.readyState == 4 && xmlhttp.status == 200){
	  				//document.write(xmlhttp.responseText);
	  				if(xmlhttp.responseText){
	  					//alert(xmlhttp.responseText + "*");
	  					fileList = xmlhttp.responseText.split(" ");
	  	    			alert(fileList);
	  	    			for(var f in fileList){
	  	    				var myobj = {
	  	    				        label: fileList[f], 
	  	    				        myNodeId:fileList[f]
	  	    				};
	  	    				var newNode = new YAHOO.widget.TextNode(myobj, tmpNode, false);
	  	    				//document.getElementById("code").innerHTML=xmlhttp.responseText;
	  	    				//alert(newNode.isLeaf);
	  	    				//newNode.refresh();
	  	    				drawTree();
	  	    			}
	  	    			
	  				}
	    		}
	  		};
			xmlhttp.send(null);
		}
		
		
	}
	
	function drawTree(){
		//once it's all built out, we need to render
		//our TreeView instance:
		
		
		tree.draw();
		
//		//handler for expanding all nodes
//		YAHOO.util.Event.on("expand", "click", function(e) {
//			YAHOO.log("Expanding all TreeView  nodes.", "info", "example");
//			tree.expandAll();
//			YAHOO.util.Event.preventDefault(e);
//		});
//		
//		//handler for collapsing all nodes
//		YAHOO.util.Event.on("collapse", "click", function(e) {
//			YAHOO.log("Collapsing all TreeView  nodes.", "info", "example");
//			tree.collapseAll();
//			YAHOO.util.Event.preventDefault(e);
//		});
	}
	
	//When the DOM is done loading, we can initialize our TreeView
	//instance:
	YAHOO.util.Event.onDOMReady(treeInit);
	
	function createProject(){
		var pname=prompt("Input name for the new project:", "");
		if(pname){
			var xmlhttp;
			if (window.XMLHttpRequest){
				// code for IE7+, Firefox, Chrome, Opera, Safari
	  			xmlhttp=new XMLHttpRequest();
	  		}
			else{
				// code for IE6, IE5
	  			xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
	  		}
		
			xmlhttp.open("GET","http://127.0.0.1:8080/CloudCompiler/ProjectManager?type=newpj&name="+pname, true);
		
			xmlhttp.onreadystatechange=function(){
//			alert("ready state=" + xmlhttp.readyState + ", http status=" + xmlhttp.status);
	  			if (xmlhttp.readyState==4 && xmlhttp.status==200){
	  				//document.write(xmlhttp.responseText);
	  				if(xmlhttp.responseText==0){
	  					alert(xmlhttp.responseText);
	  	    			tmpNode = new YAHOO.widget.TextNode(pname, tree.getRoot(), false);
	  	    			drawTree();
	  	    			//alert(tmpNode.isLeaf);
	  	    			//newNode.refresh();
	  				}
	    		}
	  		};
			xmlhttp.send(null);
		}
	}
	
	function createFile(){
		
	}