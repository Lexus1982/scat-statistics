<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<nav class="navbar navbar-dark bg-dark navbar-expand-lg mb-4">
    <a class="navbar-brand" href="./"><spring:message code="project.name"/></a>

    <div class="collapse navbar-collapse">
        <ul class="navbar-nav mr-auto">
            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="manage-dropdown" role="button"
                   data-toggle="dropdown"
                   aria-haspopup="true" aria-expanded="false">
                    <spring:message code="menu.1"/>
                </a>

                <div class="dropdown-menu" aria-labelledby="manage-dropdown">
                    <a class="dropdown-item" href="admin/users"><spring:message code="menu.1.item.1"/></a>
                    <a class="dropdown-item" href="status"><spring:message code="menu.1.item.2"/></a>
                    <div class="dropdown-divider"></div>
                    <a class="dropdown-item" href="admin/domains"><spring:message code="menu.1.item.3"/></a>
                </div>
            </li>

            <li class="nav-item dropdown">
                <a class="nav-link dropdown-toggle" href="#" id="reports-dropdown" role="button"
                   data-toggle="dropdown"
                   aria-haspopup="true" aria-expanded="false">
                    <spring:message code="menu.2"/>
                </a>

                <div class="dropdown-menu" aria-labelledby="reports-dropdown">
                    <a class="dropdown-item" href="reports/click/count"><spring:message code="menu.2.item.1"/></a>
                    <a class="dropdown-item" href="reports/tracked/requests"><spring:message code="menu.2.item.2"/></a>
                </div>
            </li>

        </ul>
    </div>
</nav>