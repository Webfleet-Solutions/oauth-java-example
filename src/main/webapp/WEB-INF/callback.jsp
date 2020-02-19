<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "3rd Party Home Page - Callback from TTT Authserver";
    LinkedHashMap<String, String> menu = new LinkedHashMap<>();
    menu.put("/consent", "Review linked services");
    menu.put("/consume", "Consume TTTSP API");
%>
<%@include file="header.jsp" %>
<%@include file="menu.jsp" %>
<main class="main-container">
    <sec:authorize access="isAuthenticated()">
        <p>Authserver's response</p>
        <pre>
        <code id="response">${responsePayload}</code>
    </pre>
    </sec:authorize>
</main>
<%@include file="footer.jsp" %>