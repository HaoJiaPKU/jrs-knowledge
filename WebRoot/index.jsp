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
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<link rel="stylesheet" href="ztree/css/zTreeStyle/zTreeStyle.css" type="text/css">
	<script type="text/javascript" src="bootstrap/js/jquery-1.11.3.min.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.core.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.excheck.js"></script>
	<script type="text/javascript" src="ztree/js/jquery.ztree.exedit.js"></script>
  </head>
  
  <body>
    <h1>jrs-knowledge</h1>
    <div>
  		<button id="load">load</button>
  	</div>
  	<div class="">
  		<ul id="div1" class="ztree"></ul>
	</div>
	<div class="">
		<ul id="div2" class="ztree"></ul>
	</div>
  </body>
  	
	<script type="text/javascript">
		var setting = {
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
				beforeDrop: beforeDrop
			}
		};

		var zNodes =[
			{ id:1, pId:0, name:"父节点 1", open:true},
			{ id:11, pId:1, name:"叶子节点 1-1"},
			{ id:12, pId:1, name:"叶子节点 1-2"},
			{ id:13, pId:1, name:"叶子节点 1-3"},
			{ id:2, pId:0, name:"父节点 2", open:true},
			{ id:21, pId:2, name:"叶子节点 2-1"},
			{ id:22, pId:2, name:"叶子节点 2-2"},
			{ id:23, pId:2, name:"叶子节点 2-3"},
			{ id:3, pId:0, name:"父节点 3", open:true},
			{ id:31, pId:3, name:"叶子节点 3-1"},
			{ id:32, pId:3, name:"叶子节点 3-2"},
			{ id:33, pId:3, name:"叶子节点 3-3"}
		];

		function beforeDrag(treeId, treeNodes) {
			for (var i=0,l=treeNodes.length; i<l; i++) {
				if (treeNodes[i].drag === false) {
					return false;
				}
			}
			return true;
		}
		function beforeDrop(treeId, treeNodes, targetNode, moveType) {
			return targetNode ? targetNode.drop !== false : true;
		}
		
		$(document).ready(function(){
			$.fn.zTree.init($("#div1"), setting, zNodes);
			$.fn.zTree.init($("#div2"), setting);
		});
		
		$("#load").click(function(event){
			console.log("click load");
			$.ajax({
				url: "knowledge/load",
				data: {},
				type : "POST",
				async : false,
				dataType : "json",
				contentType : "application/x-www-form-urlencoded; charset=utf-8",
				success : function(json){
					console.log(json);
					zNodes = json;
					$.fn.zTree.init($("#div1"), setting, zNodes);
					$.fn.zTree.init($("#div2"), setting);
					document.close();
				},
				error : function(xhr, status){
					return false;
				},
				complete : function(xhr, status){
					console.log("click complete");
				}
			});
			event.stopPropagation();
		});
	</script>
</html>
