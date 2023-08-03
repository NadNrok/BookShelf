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

    <title>Books list</title>

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
            <li><a href="<c:url value="/admin/users" />">Users</a></li>
            <li class="active"><a href="<c:url value="/admin/books" />">Books</a></li>
            <li><a href="<c:url value="/admin/stats" />">Statistic</a></li>
        </ul>
        <form class="navbar-form navbar-left">
            <div class="input-group">
                <input id="q" name="q" type="text" class="form-control" placeholder="Search">
                <div class="input-group-btn">
                    <button class="btn btn-default" type="submit">
                        <i class="glyphicon glyphicon-search"></i>
                    </button>
                </div>
            </div>
        </form>
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
        <strong>Notification!</strong> <c:out value="${error}" />
    </div>
</c:if>

<c:if test="${empty query}">
    <c:url var="firstUrl" value="/admin/books?page=1" />
    <c:url var="lastUrl" value="/admin/books?page=${books.totalPages}" />
    <c:url var="prevUrl" value="/admin/books?page=${current - 1}" />
    <c:url var="nextUrl" value="/admin/books?page=${current + 1}" />
</c:if>
<c:if test="${not empty query}">
    <c:url var="firstUrl" value="/admin/books?page=1&q=${query}" />
    <c:url var="lastUrl" value="/admin/books?page=${books.totalPages}&q=${query}" />
    <c:url var="prevUrl" value="/admin/books?page=${current - 1}&q=${query}" />
    <c:url var="nextUrl" value="/admin/books?page=${current + 1}&q=${query}" />
</c:if>

<c:if test="${empty error}" >
<div class="container">
    <table class="table table-hover table-responsive">
        <thead>
        <tr>
            <th>Title</th>
            <th>Authors</th>
            <th>Pages</th>
            <th>Publisher</th>
            <th>Publish Year</th>
            <th>Reviews</th>
            <th>Cover url</th>
            <th>Genres</th>
            <th>Rating</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${books.content}" var="book" >
            <tr class='clickable-row'
                data-href=<c:url value="/admin/books/${book.id}" />>
                <td><c:out value="${book.title}" /></td>
                <td><c:out value="${book.printAuthors()}" /></td>
                <td><c:out value="${book.pagesCnt}" /></td>
                <td><c:out value="${book.publisher}" /></td>
                <td><c:out value="${book.publishYear}" /></td>
                <td><c:out value="${book.getReviewsCnt()}" /></td>
                <td><c:out value="${book.coverUrl}" /></td>
                <td><c:out value="${book.printGenres()}" /></td>
                <td><fmt:formatNumber value="${book.averageRate}" maxFractionDigits="1"/></td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</c:if>

    <div class="container">
        <div class="span12">
            <a type="button" class="btn btn-primary" name="add_button"
               href="${pageContext.request.contextPath}/admin/books/new">Create
            </a>
        </div>
    </div>

    <c:if test="${empty error and books.totalPages != 1}" >
    <div class="inner">
        <ul class="pagination">
            <c:if test="${current != 1}">
                <li><a href="${firstUrl}">&lt;&lt;</a></li>
                <li><a href="${prevUrl}">&lt;</a></li>
            </c:if>

            <c:forEach var="i" begin="${begin}" end="${end}">
                <c:if test="${empty query}">
                    <c:url var="pageUrl" value="/admin/books?page=${i}" />
                </c:if>
                <c:if test="${not empty query}">
                    <c:url var="pageUrl" value="/admin/books?page=${i}&q=${query}" />
                </c:if>
                <c:choose>
                    <c:when test="${i == current}">
                        <li class="active"><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageUrl}"><c:out value="${i}" /></a></li>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${current != books.totalPages}">
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
