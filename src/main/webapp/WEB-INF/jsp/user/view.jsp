<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="shiro" uri="http://shiro.apache.org/tags"%>
<html>
<head>
<title>用户管理</title>
<script type="text/javascript">
	function pagination(pageValue) {
		var currentPage = $("input[name='currentPage']").val();
		if (pageValue == 'F') {
			$("input[name='currentPage']").val('1');
		} else if (pageValue == 'P') {
			$("input[name='currentPage']").val(parseInt(currentPage) - 1);
		} else if (pageValue == 'N') {
			$("input[name='currentPage']").val(parseInt(currentPage) + 1);
		} else if (pageValue == 'L') {
			$("input[name='currentPage']").val('${pageBean.totalPage}');
		}
		$("#form").submit();
	}
	function toEdit(userId) {
		window.location.href = "${CONTEXT_PATH}/userMgmt/edit/" + userId;
	}
	function toGroup(userId) {
		window.location.href = "${CONTEXT_PATH}/userMgmt/group/" + userId;
	}
	function toRole(userId) {
		window.location.href = "${CONTEXT_PATH}/userMgmt/role/" + userId;
	}
	function toAdd() {
		window.location.href = "${CONTEXT_PATH}/userMgmt/add";
	}
</script>
</head>
<body>

	<form action="${CONTEXT_PATH}/userMgmt/view" method="post" id="form">
		<input type="hidden" name="currentPage" value="${currentPage}" />

		<div class="row">
			<div class="col-lg-4">
				<div class="input-group">
					<span class="input-group-addon">User Name</span> <input
						name="userName" value="${userName}" type="text"
						class="form-control" placeholder="User Name">
				</div>
			</div>
			<div class="col-lg-4">
				<div class="input-group">
					<span class="input-group-addon">screen Name</span> <input
						name="screenName" value="${screenName}" type="text"
						class="form-control" placeholder="screen Name">
				</div>
			</div>
			<div class="col-lg-4">
				<div class="input-group"></div>
			</div>
		</div>
		<br>

		<div class="collapse navbar-collapse">
			<div class="navbar-right">
				<button type="submit" class="btn btn-primary">Search</button>
				<button type="button" class="btn btn-default" onclick="toAdd()">Add</button>
			</div>
		</div>
		<br>

		<table class="table table-bordered">
			<thead>
				<tr>
					<th>#</th>
					<th>User Name</th>
					<th>Screen Name</th>
					<th>State</th>
					<th>Operation</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${pageBean.pageList}" var="bean"
					varStatus="status">
					<tr>
						<th scope="row">${status.index + 1}</th>
						<td>${bean.user_name}</td>
						<td>${bean.screen_name}</td>
						<td>${bean.state=='0'?'有效':'无效'}</td>
						<td>
							<button type="button" class="btn btn-default btn-xs"
								onclick="toEdit('${bean.user_id}')">Edit</button>
							<button type="button" class="btn btn-default btn-xs"
								onclick="toGroup('${bean.user_id}')">Group</button>
							<button type="button" class="btn btn-default btn-xs"
								onclick="toRole('${bean.user_id}')">Role</button>
						</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>

		<div class="collapse navbar-collapse">
			<div class="navbar-right">
				<ul class="pagination">
					<li><a href="javascript:void(0)"
						${pageBean.isFirst?'':'onclick=pagination("F")'}
						aria-label="Previous"><span aria-hidden="true">««</span></a></li>
					<li><a href="javascript:void(0)"
						${pageBean.isFirst?'':'onclick=pagination("P")'}>«</a></li>
					<li><a href="javascript:void(0)">共${pageBean.totalCount}条
							${currentPage} / ${pageBean.totalPage}</a></li>
					<li><a href="javascript:void(0)"
						${pageBean.isLast?'':'onclick=pagination("N")'}>»</a></li>
					<li><a href="javascript:void(0)"
						${pageBean.isLast?'':'onclick=pagination("L")'} aria-label="Next"><span
							aria-hidden="true">»»</span></a></li>
				</ul>
			</div>
		</div>
	</form>
</body>
</html>