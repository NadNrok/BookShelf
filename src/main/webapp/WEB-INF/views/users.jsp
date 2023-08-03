<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
<head>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap-theme.min.css'>

    <title>Users list</title>

    <style>
        .inner {
        width: 50%;
        margin: 0 auto;
        }
    </style>
</head>
<body>

<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="<c:url value="/" />">BookShelf</a>
        </div>
        <ul class="nav navbar-nav">
            <li class="active"><a href="<c:url value="/admin/users" />">Users</a></li>
            <li><a href="<c:url value="/admin/books" />">Books</a></li>
            <li><a href="<c:url value="/admin/stats" />">Statistic</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
                <li><a href="#" onclick="document.getElementById('logout').submit();"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
                <form action="<c:url value="/logout" />" id="logout" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                </form>
        </ul>
    </div>
</nav>

<c:if test="${not empty error}" >
    <div class="alert alert-danger">
        <strong>Warning!</strong> <c:out value="${error}" />
    </div>
</c:if>

<c:url var="firstUrl" value="/admin/users?page=1" />
<c:url var="lastUrl" value="/admin/users?page=${users.totalPages}" />
<c:url var="prevUrl" value="/admin/users?page=${current - 1}" />
<c:url var="nextUrl" value="/admin/users?page=${current + 1}" />

<c:if test="${empty error}" >
<div class="container">
    <table class="table table-hover table-responsive">
        <thead>
        <tr>
            <th>Login</th>
            <th>Name</th>
            <th>Surname</th>
            <th>City</th>
            <th>Photo url</th>
            <th>Roles</th>
            <th>Is enabled</th>
        </tr>
        </thead>
        <tbody>
            <c:forEach items="${users.content}" var="user" >
                <tr class='clickable-row'
                    data-href=<c:url value="/admin/users/${user.login}" />>
                    <td><c:out value="${user.login}" /></td>
                    <td><c:out value="${user.name}" /></td>
                    <td><c:out value="${user.surname}" /></td>
                    <td><c:out value="${user.city}" /></td>
                    <td><c:out value="${user.photoUrl}" /></td>
                    <td><c:out value="${user.printRoles()}" /></td>
                    <c:choose>
                        <c:when test="${user.enabled}">
                            <td><span class="glyphicon glyphicon-ok-sign text-success"></span></td>
                        </c:when>
                        <c:otherwise>
                            <td><span class="glyphicon glyphicon-minus-sign text-danger"></span></td>
                        </c:otherwise>
                    </c:choose>
                </tr>
            </c:forEach>
        </tbody>
    </table>
    </c:if>

    <div class="container">
        <div class="span12">
            <a type="button" class="btn btn-primary" name="add_button"
               href="${pageContext.request.contextPath}/registration">Create
            </a>
        </div>
    </div>

    <c:if test="${empty error}" >
    <div class="inner">
        <ul class="pagination">
            <c:if test="${current != 1}">
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:if>

            <c:forEach var="i" begin="${begin}" end="${end}">
                <c:url var="pageUrl" value="/admin/users?page=${i}" />
                <c:choose>
                    <c:when test="${i == current}">
                        <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${current != users.totalPages}">
                <li><a href="${nextUrl}">&gt;</a></li>
                <li><a href="${lastUrl}">&gt;&gt;</a></li>
            </c:if>
        </ul>
    </div>
    </c:if>
</div>

<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
<script>
    jQuery(document).ready(function($) {
        $(".clickable-row").click(function() {
            window.document.location = $(this).data("href");
        });
    });
</script>


</body>
</html>
