<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="navbar navbar-inverse navbar-fixed-top">
	<div class="container">
		<div class="navbar-header">
			<a class="navbar-brand" href="index.jsp">Jobpopo</a>
			<a class="navbar-brand" href="employee.jsp"><span style="color:orange">找工作</span></a> 
			<a class="navbar-brand" href="employer.jsp">找简历</a>
		</div>
		<div class="navbar-collapse collapse">
			<c:if test="${empty employee }">
				<form action="/employee/login" method="post"
					class="navbar-form navbar-right">
					<div class="form-group">
						<input name="username" type="text" placeholder="Email"
							class="form-control">
					</div>
					<div class="form-group">
						<input name="password" type="password" placeholder="Password"
							class="form-control">
					</div>
					<button type="submit" class="btn btn-primary">登录</button>
					<button type="button" class="btn btn-primary"
						onclick="javascript:window.location.href='/employee/regist'">求职者注册</button>
				</form>
			</c:if>
			<c:if test="${not empty employee }">
				<ul class="nav navbar-nav navbar-right">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle"
							data-toggle="dropdown" role="button" aria-haspopup="true"
							aria-expanded="true"> ${employee.email } <span class="caret"></span>
						</a>
						<ul class="dropdown-menu" role="menu">
							<c:if test="${employee.hasResume == 0 }">
								<li><a href="resume/addResume">添加简历</a></li>
							</c:if>
							<c:if test="${employee.hasResume == 1 }">
								<li><a href="resume/checkResume?employeeId=${employee.id }">查看简历</a></li>
								<li><a href="search/updateRelevanceForEmployee">更新适合我的职位</a></li>
								<li><a href="search/listMatchPosition?offset=0">查看匹配职位</a></li>
								<li><a href="employee/check?employeeId=${employee.id }">订阅推送</a></li>
							</c:if>
							<li role="separator" class="divider"></li>
							<li><a href="employee/logout">退出</a></li>
						</ul>
					</li>
				</ul>
			</c:if>
		</div>
		<!--/.navbar-collapse -->
	</div>
</nav>