<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<html>
<head>
    <title>User page</title>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
</head>
<body>

<ol class="breadcrumb">
    <li class="breadcrumb-item"> <a href="#">Home</a></li>
    <li class="breadcrumb-item"> <a href="<c:url value="/admin/users" />">Users</a></li>
    <li class="breadcrumb-item active">User</li>
</ol>

<div class="container">
    <div class="row">
        <div class="col-md-1"></div>
        <div class="col-md-10">
            <div class="panel panel-primary">
                <div class="panel-heading">User</div>
                <div class="panel-body">
            <form:form modelAttribute="user" method="POST" enctype="multipart/form-data" class="form-vertical">
                <div class="form-group">
                    <label>Login:</label>
                    <form:input path="login" class="form-control" value="" disabled="true"/>
                </div>
                <div class="form-group">
                    <c:set var="nameErrors"><form:errors path="name"/></c:set>
                    <label>Name:</label>

                    <form:input path="name" value="" class="form-control" required="required" />
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

                <form:hidden path="password" />
                <form:hidden path="photoUrl" />

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
                    <form:label path="photo">Photo:</form:label>

                    <label class="btn btn-default btn-file">
                        Browse <form:input path="photo" style="display: none;"
                                                type="file" class="form-control"
                                                onchange="$('#upload-file-info').html($(this).val());"/>
                        <span class='label label-info' id="upload-file-info">${user.photoUrl}</span>
                    </label>
                    <c:if test="${not empty photoErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${photoErrors}
                        </div>
                    </c:if>
                </div>

                <div class="form-group">
                    <form:label path="roles">Roles:</form:label>
                    <form:select path="roles" multiple="true" class="form-control">
                        <form:options items="${roles}" itemValue="name" itemLabel="name" />
                    </form:select>
                </div>

                <div class="form-group">
                    <label>Is enabled:</label>
                    <form:checkbox path="enabled" />
                </div>

                <div class="form-group">
                    <div class="col-sm-12">
                        <button type="submit" class="btn btn-primary" name="edit_btn">Save</button>
                        <button type="submit" class="btn btn-primary" name="delete_btn">Delete</button>
                    </div>
                </div>
            </form:form>
                </div>
            </div>
        </div>
        <div class="col-md-1"></div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
</body>
</html>
