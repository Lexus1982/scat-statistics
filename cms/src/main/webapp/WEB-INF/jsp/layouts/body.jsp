<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<tiles:insertAttribute name="menu"/>
<div class="container-fluid">
    <div class="row"><tiles:insertAttribute name="content"/></div>
</div>
<tiles:insertAttribute name="footer"/>