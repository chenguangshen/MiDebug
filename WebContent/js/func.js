var editor;
(function () {
  // Minimal event-handling wrapper.
  function stopEvent() {
    if (this.preventDefault) {this.preventDefault(); this.stopPropagation();}
    else {this.returnValue = false; this.cancelBubble = true;}
  }
  function addStop(event) {
    if (!event.stop) event.stop = stopEvent;
    return event;
  }
  function connect(node, type, handler) {
    function wrapHandler(event) {handler(addStop(event || window.event));}
    if (typeof node.addEventListener == "function")
      node.addEventListener(type, wrapHandler, false);
    else
      node.attachEvent("on" + type, wrapHandler);
  }

  function forEach(arr, f) {
    for (var i = 0, e = arr.length; i < e; ++i) f(arr[i]);
  }   

  editor = CodeMirror.fromTextArea(document.getElementById("code"), {
    lineNumbers: true,
    
	matchBrackets: true,
	
    mode: "text/x-csrc",
    
    enterMode: "keep",
    
    onKeyEvent: function(i, e) {
      // Hook into ctrl-space
      
      if (e.keyCode == 32 && (e.ctrlKey || e.metaKey) && !e.altKey) {
        e.stop();
        return startComplete();
      }
    },
    
    onGutterClick: function(cm, n) {
      var info = cm.lineInfo(n);
      if (info.markerText){
    	  alert("Delete breakpoint: line " + n);
    	  cm.clearMarker(n);
      }
      else{
    	  insertBreak(n, cm);
      }
    },
    
    onCursorActivity: function() {
      editor.setLineClass(hlLine, null);
      $("#ins_sta").html(editor.getCursor().line + " / " + editor.lineCount());
      hlLine = editor.setLineClass(editor.getCursor().line, "activeline");
  	},
  	
  	onChange: function (){
  		//alert("changed");
  		update = 0;
  		$("#header_chg").html(" (Modified)");
  	}
  	
  });
  
  var hlLine = editor.setLineClass(0, "activeline");

  function selectTheme(node) {
		var theme = node.options[node.selectedIndex].innerHTML;
  		editor.setOption("theme", theme);
  }  
  
  function startComplete() {
    // We want a single cursor position.
    if (editor.somethingSelected()) return;
    // Find the token at the cursor
    var cur = editor.getCursor(false), token = editor.getTokenAt(cur), tprop = token;
    // If it's not a 'word-style' token, ignore the token.
    if (!/^[\w$_]*$/.test(token.string)) {
      token = tprop = {start: cur.ch, end: cur.ch, string: "", state: token.state,
                       className: token.string == "." ? "js-property" : null};
    }
    // If it is a property, find out what it is a property of.
    while (tprop.className == "js-property") {
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (tprop.string != ".") return;
      tprop = editor.getTokenAt({line: cur.line, ch: tprop.start});
      if (!context) var context = [];
      context.push(tprop);
    }
    var completions = getCompletions(token, context);
    if (!completions.length) return;
    function insert(str) {
      editor.replaceRange(str, {line: cur.line, ch: token.start}, {line: cur.line, ch: token.end});
    }
    // When there is only one completion, use it directly.
    if (completions.length == 1) {insert(completions[0]); return true;}

    // Build the select widget
    var complete = document.createElement("div");
    complete.className = "completions";
    var sel = complete.appendChild(document.createElement("select"));
    sel.multiple = true;
    for (var i = 0; i < completions.length; ++i) {
      var opt = sel.appendChild(document.createElement("option"));
      opt.appendChild(document.createTextNode(completions[i]));
    }
    sel.firstChild.selected = true;
    sel.size = Math.min(10, completions.length);
    var pos = editor.cursorCoords();
    complete.style.left = pos.x + "px";
    complete.style.top = pos.yBot + "px";
    document.body.appendChild(complete);
    // Hack to hide the scrollbar.
    if (completions.length <= 10)
      complete.style.width = (sel.clientWidth - 1) + "px";

    var done = false;
    function close() {
      if (done) return;
      done = true;
      complete.parentNode.removeChild(complete);
    }
    function pick() {
      insert(sel.options[sel.selectedIndex].value);
      close();
      setTimeout(function(){editor.focus();}, 50);
    }
    connect(sel, "blur", close);
    connect(sel, "keydown", function(event) {
      var code = event.keyCode;
      // Enter and space
      if (code == 13 || code == 32) {event.stop(); pick();}
      // Escape
      else if (code == 27) {event.stop(); close(); editor.focus();}
      else if (code != 38 && code != 40) {close(); editor.focus(); setTimeout(startComplete, 50);}
    });
    connect(sel, "dblclick", pick);

    sel.focus();
    // Opera sometimes ignores focusing a freshly created node
    if (window.opera) setTimeout(function(){if (!done) sel.focus();}, 100);
    return true;
  }

//  var stringProps = ("aaa").split(" ");
//  var arrayProps = ("bbb").split(" ");
//  var funcProps = "ddd".split(" ");
  var keywords = ("auto break case char const continue default do double else enum extern float for goto if int long register return short signed sizeof static struct switch typedef union unsigned void volatile while ").split(" "); 
  var stdlib_funcs = ("remove rename tmpfile tmpnam fclose fflush fopen freopen setbuf setvbuf fprintf fscanf printf scanf sprintf sscanf vfprintf vprintf vsprintf fgetc fgets fputc fputs getc getchar gets putc putchar puts ungetc fread fwrite fgetpos fseek fsetpos ftell rewind clearerr feof ferror perror EOF FILENAME_MAX NULL TMP_MAX IOFBF _IOLBF _IONBF BUFSIZ FOPEN_MAX L_tmpnam SEEK_CUR SEEK_END SEEK_SET FILE fpos_t size_t").split(" ");
    
  function getCompletions(token, context) {
    var found = [], start = token.string;
    function maybeAdd(str) {
      if (str.indexOf(start) == 0) found.push(str);
    }
    function gatherCompletions(obj) {
//      if (typeof obj == "string") forEach(stringProps, maybeAdd);
//      else if (obj instanceof Array) forEach(arrayProps, maybeAdd);
//      else if (obj instanceof Function) forEach(funcProps, maybeAdd);
      for (var name in obj) maybeAdd(name);
    }

    if (context) {
      // If this is a property, see if it belongs to some object we can
      // find in the current environment.
      var obj = context.pop(), base;
      if (obj.className == "js-variable")
        base = window[obj.string];
      else if (obj.className == "js-string")
        base = "";
      else if (obj.className == "js-atom")
        base = 1;
      while (base != null && context.length)
        base = base[context.pop().string];
      if (base != null) gatherCompletions(base);
    }
    else {
      // If not, just look in the window object and any local scope
      // (reading into JS mode internals to get at the local variables)
      //for (var v = token.state.localVars; v; v = v.next) maybeAdd(v.name);
      //gatherCompletions(window);
      forEach(keywords, maybeAdd);
      forEach(stdlib_funcs, maybeAdd);
    }
    return found;
  }
})();
