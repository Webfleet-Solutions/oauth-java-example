<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "3rd Party Home Page - Available services";
    LinkedHashMap<String, String> menu = new LinkedHashMap<>();
    menu.put("/consent", "Review linked services");
    if ((boolean) request.getAttribute("hasRefreshToken"))
    {
        menu.put("/consume", "Consume TTTSP API");
    } else
    {
        menu.put("/linkAccount", "Link account to TTTSP");
    }
%>
<%@include file="header.jsp" %>
<%@include file="menu.jsp" %>
<main class="main-container">
    <div>
        <c:choose>
            <c:when test="${hasRefreshToken}">
                <p>TTTSP API service linked <a href="/revoke">revoke</a></p>
            </c:when>
            <c:otherwise>
                <p>No services linked to your account</p>
            </c:otherwise>
        </c:choose>
    </div>
</main>
<%@include file="footer.jsp" %>