<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap-theme.min.css'>

    <title>Statistics</title>
</head>
<body>

<nav class="navbar navbar-default">
    <div class="container-fluid">
        <div class="navbar-header">
            <a class="navbar-brand" href="<c:url value="/" />">BookShelf</a>
        </div>
        <ul class="nav navbar-nav">
            <li><a href="<c:url value="/admin/users" />">Users</a></li>
            <li><a href="<c:url value="/admin/books" />">Books</a></li>
            <li class="active"><a href="<c:url value="/admin/stats" />">Statistic</a></li>
        </ul>
        <ul class="nav navbar-nav navbar-right">
            <li><a href="<c:url value="/admin/stats?clear=on" />"><span class="glyphicon glyphicon-trash"></span> Clear cache</a></li>
            <li><a href="<c:url value="/admin/stats?dump=on" />"><span class="glyphicon glyphicon-export"></span> Dump database</a></li>
            <li><a href="#" onclick="document.getElementById('logout').submit();"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
            <form action="<c:url value="/logout" />" id="logout" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            </form>
        </ul>
    </div>
</nav>

<div class="row">
    <div class="col-md-1"></div>
    <div class="col-md-10">
        <div class="panel panel-default">
            <div class="panel-heading">Cache stats</div>
            <div class="panel-body">
                <h3>Loading user page speed test</h3>
                <table class="table table-condensed">
                    <tbody>
                    <tr>
                        <td>Retrieve from database time</td>
                        <td><c:out value="${dbTime}" /> ms </td>
                    </tr>
                    <tr>
                        <td>Retrieve from cache time</td>
                        <td><c:out value="${cacheTime}" /> ms </td>
                    </tr>
                    </tbody>
                </table>

                <h3>Variables in cache</h3>
                <c:if test="${empty cache}">
                    Cache is empty
                </c:if>
                <table class="table table-condensed">
                    <tbody>
                    <c:forEach items="${cache}" var="entry">
                        <tr>
                            <td><c:out value="${entry.key}" /></td>
                            <c:choose>
                                <c:when test="${entry.value.length() > 100}">
                                    <td><c:out value="${entry.value.substring(0, 100)}" />...]</td>
                                </c:when>
                                <c:otherwise>
                                    <td><c:out value="${entry.value}" /></td>
                                </c:otherwise>
                            </c:choose>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
</body>
</html>
