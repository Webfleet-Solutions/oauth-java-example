<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String title = "3rd Party Home Page - Consume TTTSP API";
    LinkedHashMap<String, String> menu = new LinkedHashMap<>();
    menu.put("/consent", "Review linked services");
    menu.put("/consume", "Consume TTTSP API with current token");
    menu.put("/revoke", "Revoke token");
%>
<%@include file="header.jsp" %>
<%@include file="menu.jsp" %>
<main class="main-container">
    <blockquote>This page requests TTTSP API when loaded, click browser's refresh button to place another request to the
        TTTSP API
    </blockquote>
    <p style="padding: 5px; border: 1px solid #d0d5d4">TTTSP API responded <fmt:formatDate value="${tttspApiDate}"
                                                                                           pattern="yyyy-MM-dd HH:mm:ss"/></p>
</main>
<%@include file="footer.jsp" %>