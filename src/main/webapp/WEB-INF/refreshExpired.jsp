<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "3rd Party Home Page - Refresh token";
%>
<%@include file="header.jsp" %>
<%@include file="menu.jsp" %>
<c:url value="/linkAccount" var="linkAccountUrl"/>
<main class="main-container">
    <sec:authorize access="isAuthenticated()">
        <p>Refresh token has expired, you need to grant 3rdparty's access again to continue consuming TTTSP API, <a
                href="${linkAccountUrl}">link account to TTTSP</a></p>
    </sec:authorize>
</main>
<%@include file="footer.jsp" %>