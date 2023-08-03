<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <title>BookShelf</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap-theme.min.css'>
    <style>
        .navbar {
            margin-bottom: 50px;
            border-radius: 0;
        }

        .jumbotron {
            margin-bottom: 0;
        }

        .inner {
            width: 50%;
            margin: 0 auto;
        }

        img.displayed {
            display: block;
            margin-left: auto;
            margin-right: auto }

        .stars
        {
            font-size: 24px;
            color: #d17581;
        }

        footer {
            background-color: #f2f2f2;
            padding: 25px;
        }
    </style>
</head>
<body>
<c:if test="${not empty query}">
    <c:url var="firstUrl" value="/?page=1&q=${query}" />
    <c:url var="lastUrl" value="/?page=${books.totalPages}&q=${query}" />
    <c:url var="prevUrl" value="/?page=${current - 1}&q=${query}" />
    <c:url var="nextUrl" value="/?page=${current + 1}&q=${query}" />
</c:if>
<c:if test="${empty query}">
    <c:url var="firstUrl" value="/?page=1" />
    <c:url var="lastUrl" value="/?page=${books.totalPages}" />
    <c:url var="prevUrl" value="/?page=${current - 1}" />
    <c:url var="nextUrl" value="/?page=${current + 1}" />
</c:if>

<div class="jumbotron">
    <div class="container text-center">
        <h1>Online Library</h1>
        <p>Read, Learn & Inspire</p>
    </div>
</div>

<nav class="navbar navbar-inverse">
    <div class="container-fluid">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle" data-toggle="collapse" data-target="#myNavbar">
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="<c:url value="/" />">BookShelf</a>
        </div>
        <div class="collapse navbar-collapse" id="myNavbar">
            <ul class="nav navbar-nav">
                <li class="active"><a href="<c:url value="/" />">Home</a></li>
                <li><a href="<c:url value="/top" />">Top Rated</a></li>
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
                <sec:authorize access="hasRole('ADMIN')">
                    <li><a href="<c:url value="/admin/books" />"><span class="glyphicon glyphicon-lock"></span> Admin panel</a></li>
                </sec:authorize>
                <sec:authorize access="isAnonymous()">
                    <li><a href="<c:url value="/login" />"><span class="glyphicon glyphicon-log-in"></span> Login</a></li>
                    <li><a href="<c:url value="/registration" />"><span class="glyphicon glyphicon-share"></span> Sign Up</a></li>
                </sec:authorize>
                <sec:authorize access="isAuthenticated()">
                    <c:set var="login">
                        <sec:authentication property="principal.username" />
                    </c:set>
                    <li><a href="/user/${login}"><span class="glyphicon glyphicon-user"></span> Your profile</a></li>
                    <li><a href="#" onclick="document.getElementById('logout').submit();"><span class="glyphicon glyphicon-log-out"></span> Logout</a></li>
                    <form action="<c:url value="/logout" />" id="logout" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    </form>
                </sec:authorize>
            </ul>
        </div>
    </div>
</nav>

<c:if test="${not empty error}" >
    <div class="alert alert-danger">
        <strong>Notification!</strong> <c:out value="${error}" />
    </div>
</c:if>

<c:if test="${empty error}" >
<div class="container">
    <div class="row">
        <c:forEach items="${books.content}" var="book" varStatus="i">
            <div class="col-md-4">
                <div class="thumbnail">
                    <img src="/getCover/${book.id}" height="222" width="350"  alt="">
                    <div class="caption">
                        <h4><a href="<c:url value="/book/${book.id}" />"><c:out value="${book.title}" /></a></h4>
                        <p><c:out value="${book.description}" /></p>
                    </div>
                    <div class="ratings">
                        <p class="pull-right">${book.getReviewsCnt()} reviews</p>
                        <p>
                            <c:choose>
                            <c:when test="${book.getAverageRate() >= 0.5}">
                                <span class="glyphicon glyphicon-star stars"></span>
                            </c:when>
                            <c:otherwise>
                                <span class="glyphicon glyphicon-star-empty stars"></span>
                            </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${book.getAverageRate() >= 1.5}">
                                    <span class="glyphicon glyphicon-star stars"></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="glyphicon glyphicon-star-empty stars"></span>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${book.getAverageRate() >= 2.5}">
                                    <span class="glyphicon glyphicon-star stars"></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="glyphicon glyphicon-star-empty stars"></span>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${book.getAverageRate() >= 3.5}">
                                    <span class="glyphicon glyphicon-star stars"></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="glyphicon glyphicon-star-empty stars"></span>
                                </c:otherwise>
                            </c:choose>
                            <c:choose>
                                <c:when test="${book.getAverageRate() >= 4.5}">
                                    <span class="glyphicon glyphicon-star stars"></span>
                                </c:when>
                                <c:otherwise>
                                    <span class="glyphicon glyphicon-star-empty stars"></span>
                                </c:otherwise>
                            </c:choose>
                        </p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</div><br>
<br>

    <c:if test="${books.totalPages != 1}">
        <div class="inner">
            <ul class="pagination">
                <c:if test="${current != 1}">
                    <li><a href="${firstUrl}">&lt;&lt;</a></li>
                    <li><a href="${prevUrl}">&lt;</a></li>
                </c:if>

                <c:forEach var="i" begin="${begin}" end="${end}">
                    <c:if test="${not empty query}">
                        <c:url var="pageUrl" value="/?page=${i}&q=${query}" />
                    </c:if>
                    <c:if test="${empty query}">
                        <c:url var="pageUrl" value="/?page=${i}" />
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

<footer class="container-fluid text-center">
    <p>Online Library Copyright</p>
</footer>
</c:if>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>

<script>
    jQuery(document).ready(function($) {
        $(".clickable").click(function() {
            window.document.location = $(this).data("href");
        });
    });
</script>

</body>
</html>
