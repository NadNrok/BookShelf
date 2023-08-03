<!DOCTYPE html>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<html>
<head>
    <title>Book</title>
    <link rel='stylesheet'
          href='${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/css/bootstrap.min.css'>
</head>
<body>

<ol class="breadcrumb">
    <li class="breadcrumb-item"> <a href="#">Home</a></li>
    <li class="breadcrumb-item"> <a href="<c:url value="/admin/books" />">Books</a></li>
    <li class="breadcrumb-item active">Book</li>
</ol>

<div class="container">
    <div class="row">
        <div class="col-md-1"></div>
        <div class="col-md-10">
        <div class="panel panel-primary">
            <div class="panel-heading">Book</div>
            <div class="panel-body">
            <form:form modelAttribute="book" method="POST"
                       enctype="multipart/form-data" class="form-vertical" id="book">
                <div class="form-group">
                    <c:set var="titleErrors"><form:errors path="title"/></c:set>
                    <label>Title:</label>
                    <form:input path="title" class="form-control" value=""/>

                    <c:if test="${not empty titleErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${titleErrors}
                        </div>
                    </c:if>
                </div>

                <div class="form-group">
                    <c:set var="descriptionErrors"><form:errors path="description"/></c:set>
                    <label>Description:</label>

                    <form:input path="description" value="" class="form-control" />
                    <c:if test="${not empty descriptionErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${descriptionErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="pagesCntErrors"><form:errors path="pagesCnt"/></c:set>
                    <label>Pages count: </label>

                    <form:input path="pagesCnt" value="" class="form-control" />
                    <c:if test="${not empty pagesCntErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${pagesCntErrors}
                        </div>
                    </c:if>
                </div>

                <form:hidden path="coverUrl" />

                <div class="form-group">
                    <c:set var="publishYearErrors"><form:errors path="publishYear"/></c:set>
                    <label>Publish year:</label>

                    <form:input path="publishYear" value="" class="form-control" />
                    <c:if test="${not empty publishYearErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${publishYearErrors}
                        </div>
                    </c:if>
                </div>
                <div class="form-group">
                    <c:set var="coverErrors"><form:errors path="cover"/></c:set>
                    <form:label path="cover">Cover:</form:label>

                    <label class="btn btn-default btn-file">
                        Browse <form:input path="cover" style="display: none;"
                                           type="file" class="form-control"
                                           onchange="$('#upload-file-info').html($(this).val());"/>
                        <span class='label label-info' id="upload-file-info">${book.coverUrl}</span>
                    </label>
                    <c:if test="${not empty coverErrors}">
                        <div class="alert alert-danger">
                            <strong>Error!</strong> ${coverErrors}
                        </div>
                    </c:if>
                </div>

                <div class="form-group">
                    <form:label path="genres">Genres:</form:label>
                    <form:select path="genres" multiple="true" class="form-control" >
                        <form:options items="${genres}" />
                    </form:select>
                </div>

                <c:forEach var="author" items="${book.authors}" varStatus="status">

                    <div class="panel panel-info">
                        <div class="panel-heading">Author #${status.index + 1}</div>
                        <div class="panel-body">
                            <div class="form-group">
                                <label>Name:</label>
                                <form:input path="authors[${status.index}].name" class="form-control" value="" />
                            </div>
                            <div class="form-group">
                                <label>Surname:</label>
                                <form:input path="authors[${status.index}].surname" class="form-control" value="" />
                            </div>
                            <div class="form-group">
                                <label>BirthDate:</label>
                                <div class="controls">
                                    <form:input path="authors[${status.index}].birthDate" class="date form-control" />
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <div class="panel panel-info">
                    <div class="panel-heading">Publisher</div>
                    <div class="panel-body">
                        <div class="form-group">
                            <c:set var="publisherNameErrors"><form:errors path="publisher.name"/></c:set>
                            <label>Name:</label>

                            <form:input path="publisher.name" value="" class="form-control" />
                            <c:if test="${not empty publisherNameErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${publisherNameErrors}
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <c:set var="addressZipErrors"><form:errors path="publisher.address.zip"/></c:set>
                            <label>Zip:</label>

                            <form:input path="publisher.address.zip" value="" class="form-control" />
                            <c:if test="${not empty addressZipErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${addressZipErrors}
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <c:set var="addressCountryErrors"><form:errors path="publisher.address.country"/></c:set>
                            <label>Country:</label>

                            <form:input path="publisher.address.country" value="" class="form-control" required="required" />
                            <c:if test="${not empty addressCountryErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${addressCountryErrors}
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <c:set var="addressCityErrors"><form:errors path="publisher.address.city"/></c:set>
                            <label>City:</label>

                            <form:input path="publisher.address.city" value="" class="form-control" required="required" />
                            <c:if test="${not empty addressCityErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${addressCityErrors}
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <c:set var="addressStreetErrors"><form:errors path="publisher.address.street"/></c:set>
                            <label>Street:</label>

                            <form:input path="publisher.address.street" value="" class="form-control" />
                            <c:if test="${not empty addressStreetErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${addressStreetErrors}
                                </div>
                            </c:if>
                        </div>

                        <div class="form-group">
                            <c:set var="addressBuildingErrors"><form:errors path="publisher.address.building"/></c:set>
                            <label>Building:</label>

                            <form:input path="publisher.address.building" value="" class="form-control" />
                            <c:if test="${not empty addressBuildingErrors}">
                                <div class="alert alert-danger">
                                    <strong>Error!</strong> ${addressBuildingErrors}
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
            </div>

                <div class="form-group">
                    <div class="col-sm-12">
                        <button type="submit" class="btn btn-primary" name="submit_btn">Submit</button>
                        <c:if test="${not empty editType}">
                        <button type="submit" class="btn btn-primary" name="delete_btn">Delete</button>
                        </c:if>
                    </div>
                </div>
            </form:form>
        </div>
        <div class="col-md-1"></div>
    </div>
</div>

<script src="${pageContext.request.contextPath}/webjars/jquery/3.1.1/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/webjars/bootstrap/3.3.7-1/js/bootstrap.min.js"></script>
</body>
</html>
