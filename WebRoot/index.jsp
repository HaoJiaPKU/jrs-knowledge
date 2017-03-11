<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>jrs-knowledge</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<link rel="stylesheet" href="ztree/css/demo.css">
	<link rel="stylesheet" href="ztree/css/zTreeStyle/zTreeStyle.css">
	<link rel="stylesheet" href="bootstrap/style/overlay.css">
	<link rel="stylesheet" href="bootstrap/css/bootstrap.min.css">
	<link rel="stylesheet" href="bootstrap/css/bootstrap-theme.min.css">
	<script type="text/javascript" src="bootstrap/js/jquery-2.0.3.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.core.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.excheck.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.exedit.js"></script>

	<script type="text/javascript">
		var zNodes1 = [];
		var zNodes2 = [];
		var newCount1 = 1;
		var newCount2 = 1;
		
		function beforeDrag(treeId, treeNodes) {
			for (var i = 0, l = treeNodes.length; i < l; i ++) {
				if (treeNodes[i].drag === false) {
					return false;
				}
			}
			return true;
		}
		
		function beforeDrop(treeId, treeNodes, targetNode, moveType) {
			return targetNode ? targetNode.drop !== false : true;
		}
		
		function removeHoverDom(treeId, treeNode) {
			$("#addBtn_" + treeNode.tId).unbind().remove();
		};
		
		function beforeRename1(treeId, treeNode, newName, isCancel) {
			if (newName.length == 0) {
				setTimeout(function() {
					var zTree = $.fn.zTree.getZTreeObj("window1");
					zTree.cancelEditName();
					alert("节点名称不能为空");
				}, 0);
				return false;
			}
			return true;
		}
		
		function addHoverDom1(treeId, treeNode) {
			var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) {
				return;
			}
			var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
				+ "' title='add node' onfocus='this.blur();'></span>";
			sObj.after(addStr);
			var btn = $("#addBtn_" + treeNode.tId);
			if (btn) btn.bind("click", function(){
				var zTree = $.fn.zTree.getZTreeObj("window1");
				zTree.addNodes(treeNode, {id:(100 + newCount1), pId:treeNode.id, name:"new node" + (newCount1 ++)});
				return false;
			});
		};
		
		var setting1 = {
			view: {
				addHoverDom: addHoverDom1,
				removeHoverDom: removeHoverDom,
				selectedMulti: false
			},
			edit: {
				enable: true,
				showRemoveBtn: true,
				showRenameBtn: true,
				editNameSelectAll: true,
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeDrop: beforeDrop,
				beforeRename: beforeRename1,
			}
		};
		
		function beforeRename2(treeId, treeNode, newName, isCancel) {
			if (newName.length == 0) {
				setTimeout(function() {
					var zTree = $.fn.zTree.getZTreeObj("window2");
					zTree.cancelEditName();
					alert("节点名称不能为空");
				}, 0);
				return false;
			}
			return true;
		}
		
		function addHoverDom2(treeId, treeNode) {
			var sObj = $("#" + treeNode.tId + "_span");
			if (treeNode.editNameFlag || $("#addBtn_" + treeNode.tId).length > 0) {
				return;
			}
			var addStr = "<span class='button add' id='addBtn_" + treeNode.tId
				+ "' title='add node' onfocus='this.blur();'></span>";
			sObj.after(addStr);
			var btn = $("#addBtn_" + treeNode.tId);
			if (btn) btn.bind("click", function(){
				var zTree = $.fn.zTree.getZTreeObj("window2");
				zTree.addNodes(treeNode, {id:(100 + newCount2), pId:treeNode.id, name:"new node" + (newCount2 ++)});
				return false;
			});
		};
		
		var setting2 = {
			view: {
				addHoverDom: addHoverDom2,
				removeHoverDom: removeHoverDom,
				selectedMulti: false
			},
			edit: {
				enable: true,
				showRemoveBtn: true,
				showRenameBtn: true,
				editNameSelectAll: true,
			},
			data: {
				simpleData: {
					enable: true
				}
			},
			callback: {
				beforeDrag: beforeDrag,
				beforeDrop: beforeDrop,
				beforeRename: beforeRename2,
			}
		};
		
		$(document).ready(function(){
			$.fn.zTree.init($("#window1"), setting1, zNodes1);
			$.fn.zTree.init($("#window2"), setting2, zNodes2);
		});		
	</script>
  </head>
  
  
  <body>
  	<div class="container">
  		<jsp:include page="navigation.jsp"/>
  	</div>
  	<div class="container" style="height:60px; width:100%;"></div>
  	
	<div id="light" class="white_content"></div>
	<div id="fade" class="black_overlay"></div>  
	
	<div class="container-fluid">
		<div class="row-fluid">
			<div id="container1" class="col-md-12">
				<div id="buttons1" class="col-md-12 btn-group">
			  		<button class="btn btn-default" id="openKnowware1" onclick="openKnowware(1)">打开</button>
			  		<button class="btn btn-default" id="saveKnowware1" onclick="saveKnowware(1)">保存</button>
			  		<button class="btn btn-default" id="saveasKnowware1" onclick="saveasKnowware(1)">另存为</button>
			  		<button class="btn btn-default" id="renameKnowware1" onclick="renameKnowware(1)">重命名</button>
			  		<button class="btn btn-default" id="newWindow" onclick="newWindow()">新窗口</button>
			  	</div>
			  	<div class="col-md-12">
			  		<ul id="window1" class="ztree"></ul>
				</div>
			</div>
			<div id="container2" class="col-md-6" style="display:none;">
			    <div id="buttons2" class="col-md-12 btn-group">
				 	<button class="btn btn-default" id="openKnowware1" onclick="openKnowware(2)">打开</button>
				  	<button class="btn btn-default" id="saveKnowware1" onclick="saveKnowware(2)">保存</button>
				  	<button class="btn btn-default" id="saveasKnowware1" onclick="saveasKnowware(2)">另存为</button>
				  	<button class="btn btn-default" id="renameKnowware1" onclick="renameKnowware(2)">重命名</button>
				  	<button class="btn btn-default" id="newWindow" onclick="closeWindow()">关闭</button>
				</div>
				<div class="col-md-12">
			  		<ul id="window2" class="ztree"></ul>
				</div>
			</div>
		</div>
	</div>
	
  </body>
  	
	<script type="text/javascript">
		var fileName1 = "";
		var fileName2 = "";
		function openKnowware(windowId) {
			openList();
			$.ajax({
				url: "knowledge/open",
				data: {},
				type : "POST",
				async : false,
				dataType : "json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					makeList(json, windowId);
					document.close();
				},
				error : function(xhr, status){
					return false;
				},
				complete : function(xhr, status){
					console.log("open complete");
				}
			});
		}
		
		function makeList(json, windowId) {
			for (var i = 0; i < json.length; i ++) {
				var a = document.createElement("a");
				a.setAttribute("id", json[i]);
				a.setAttribute("name", json[i]);
				a.setAttribute("style", "cursor:pointer;text-decoration:none;");
				a.setAttribute("onclick", "loadKnowware(\"" + json[i] + "\", " + windowId + ")");
				a.innerHTML = json[i];
				var nobr = document.createElement("nobr");
				nobr.appendChild(a);
				var div = document.createElement("div");
				div.appendChild(nobr);
				$("#light").append(div);
			}
			var a = document.createElement("a");
			a.setAttribute("style", "cursor:pointer;text-decoration:none;floag:bottom;");
			a.setAttribute("onclick", "colseList()");
			a.innerHTML = "取消";
			var nobr = document.createElement("nobr");
			nobr.appendChild(a);
			var div = document.createElement("div");
			div.appendChild(nobr);
			$("#light").append(div);
		}
		
		function loadKnowware(fileName, windowId){  
			console.log(fileName);
			$.ajax({
				url: "knowledge/load",
				data: {
					fileName : fileName,
				},
				type : "POST",
				async : false,
				dataType : "json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					if (windowId == 1) {
						zNodes1 = json;
						fileName1 = fileName;
						$.fn.zTree.init($("#window1"), setting1, zNodes1);
					} else if (windowId == 2) {
						zNodes2 = json;
						fileName2 = fileName;
						$.fn.zTree.init($("#window2"), setting2, zNodes2);
					}
					colseList();
					document.close();
				},
				error : function(xhr, status) {
					return false;
				},
				complete : function(xhr, status){
					console.log("load complete");
				}
			});
		}
		
		function saveKnowware(windowId){
			var fileName = "";
			var zNodes = [];
			if (windowId == 1) {
				fileName = fileName1;
				var treeObj = $.fn.zTree.getZTreeObj("window1");
				zNodes = treeObj.transformToArray(treeObj.getNodes());
			} else if (windowId == 2) {
				fileName = fileName2;
				var treeObj = $.fn.zTree.getZTreeObj("window2");
				zNodes = treeObj.transformToArray(treeObj.getNodes());
			}
			console.log(zNodes);
			$.ajax({
				url: "knowledge/save",
				data: {
					fileName : fileName,
					zNodes : JSON.stringify(zNodes),
				},
				type : "POST",
				async : false,
				dataType : "application/json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					document.close();
				},
				error : function(xhr, status) {
					return false;
				},
				complete : function(xhr, status){
					console.log("save complete");
				}
			});
		}
		
		function saveasKnowware(fileName, windowId){  
			console.log(fileName);
			$.ajax({
				url: "knowledge/saveas",
				data: {
					fileName : fileName,
					
				},
				type : "POST",
				async : false,
				dataType : "json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					if (windowId == 1) {
						zNodes1 = json;
						$.fn.zTree.init($("#window1"), setting1, zNodes1);
					} else if (windowId == 2) {
						zNodes2 = json;
						$.fn.zTree.init($("#window2"), setting2, zNodes2);
					}
					colsediv();
					document.close();
				},
				error : function(xhr, status) {
					return false;
				},
				complete : function(xhr, status){
					console.log("saveas complete");
				}
			});
		}
		
		function renameKnowware(fileName, windowId){  
			console.log(fileName);
			$.ajax({
				url: "knowledge/rename",
				data: {
					fileName : fileName,
				},
				type : "POST",
				async : false,
				dataType : "json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					if (windowId == 1) {
						zNodes1 = json;
					} else if (windowId == 2) {
						zNodes2 = json;
					}
					$.fn.zTree.init($("#window1"), setting1, zNodes1);
					$.fn.zTree.init($("#window2"), setting2, zNodes2);
					colsediv();
					document.close();
				},
				error : function(xhr, status) {
					return false;
				},
				complete : function(xhr, status){
					console.log("rename complete");
				}
			});
		}
		
		function openList() {
			document.getElementById('light').style.display='block';  
			document.getElementById('fade').style.display='block';
		}
			
		function colseList() {
			$("#light").empty();
			document.getElementById('light').style.display='none';  
			document.getElementById('fade').style.display='none';  
		}
		
		function newWindow() {
			$("#container1").attr("class", "col-md-6");
			$("#container2").attr("style", "display:block;");
			var thisNode = document.getElementById("newWindow");
			thisNode.parentNode.removeChild(thisNode);
		}
		
		function closeWindow() {
			$("#container1").attr("class", "col-md-12");
			$("#container2").attr("style", "display:none;");
			var f = document.getElementById("buttons1");
			var newWindow = document.createElement("button");
			newWindow.innerHTML = "新窗口";
			newWindow.setAttribute("class", "btn btn-default");
			newWindow.setAttribute("id", "newWindow");
			newWindow.setAttribute("onclick", "newWindow()");
			f.appendChild(newWindow);
		}
	</script>
</html>
