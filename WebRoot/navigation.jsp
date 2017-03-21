<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<nav class="navbar navbar-default navbar-static-top">
	<div class="container">
		<div class="navbar-header"></div>
		<div class="navbar-collapse collapse">
			<ul class="nav navbar-nav">
				<li class="active">
					<a class="nav-a-size" href="#">jrs-knowledge</a>
				</li>
				<li>
					<a class="nav-a-size" href="http://jobpopo.com">jobpopo</a>
				</li>
			</ul>
			<c:if test="${empty employee }">
				<form action="/user/login" method="post"
					class="navbar-form navbar-right">
					<div class="form-group">
						<input name="username" type="text" placeholder="Email"
							class="form-control">
					</div>
					<div class="form-group">
						<input name="password" type="password" placeholder="Password"
							class="form-control">
					</div>
					<button type="submit" class="btn btn-default ">登录</button>
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