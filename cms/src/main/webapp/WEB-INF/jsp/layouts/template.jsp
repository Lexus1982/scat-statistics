<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE HTML>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <c:set var="titleKey">
        <tiles:insertAttribute name="title" ignore="true"/>
    </c:set>
    <title><spring:message text="" code="${titleKey}"/></title>

    <base href="${pageContext.request.contextPath}/">

    <link href="css/styles.css" type="text/css" rel="stylesheet">
    <link href="img/favicon.ico" type="image/x-icon" rel="icon">

    <link rel="stylesheet" href="webjars/bootstrap/4.1.3/css/bootstrap.min.css">

    <script type="text/javascript" src="webjars/jquery/3.3.1/dist/jquery.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap/4.1.3/js/bootstrap.min.js" defer></script>
</head>
<body>
<tiles:insertAttribute name="body"/>
</body>
</html>