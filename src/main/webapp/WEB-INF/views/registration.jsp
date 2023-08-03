<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <title>Welcome</title>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
</head>

<body>
<div class="page-header">
    <h1>Create user account</h1>
</div>

<div class="container">
    <div class="row">
        <div class="col-sm-6">
            <form:form modelAttribute="user" method="POST" enctype="multipart/form-data" class="form-vertical">
                <c:set var="loginErrors"><form:errors path="login"/></c:set>
                <div class="form-group">
                    <label>Login:</label>
                    <form:input path="login" class="form-control" value="" required="required"/>

                    <c:if test="${not empty loginErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${loginErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="nameErrors"><form:errors path="name"/></c:set>
                    <label>Name:</label>

                    <form:input path="name" value="" class="form-control" required="required"/>
                    <c:if test="${not empty nameErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${nameErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="surnameErrors"><form:errors path="surname"/></c:set>
                    <label>Surname: </label>

                    <form:input path="surname" value="" class="form-control" required="required" />
                    <c:if test="${not empty surnameErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${surnameErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="cityErrors"><form:errors path="city"/></c:set>
                    <label>City:</label>

                    <form:input path="city" value="" class="form-control" />
                    <c:if test="${not empty cityErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${cityErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="photoErrors"><form:errors path="photo"/></c:set>
                    <label>Photo: </label>

                    <form:input path="photo" value="" type="file" class="form-control" />
                    <c:if test="${not empty photoErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${photoErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="passwordErrors"><form:errors path="password"/></c:set>
                    <label>Password:</label>

                        <form:input path="password" value="" type="password" class="form-control" required="required"/>
                    <c:if test="${not empty passwordErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${passwordErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="Errors"><form:errors/></c:set>
                    <label>Confirm password:</label>

                    <form:input path="matchingPassword" value="" type="password" class="form-control" required="required"/>
                    <c:if test="${not empty Errors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${Errors}
                        </div>
                    </c:if>
                </div>
                <sec:authorize access="hasRole('ADMIN')">
                    <div class="form-group">
                        <form:label path="roles">Roles:</form:label>
                        <form:select path="roles" multiple="true" class="form-control">
                            <form:options items="${roles}" itemValue="name" itemLabel="name" />
                        </form:select>
                    </div>
                </sec:authorize>
                <button type="submit" class="btn btn-default">Submit</button>
            </form:form>
        </div>
    </div>
</div>
<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
</body>
</html>