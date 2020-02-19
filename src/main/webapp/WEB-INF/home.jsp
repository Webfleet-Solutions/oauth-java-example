<%@ page import="java.util.LinkedHashMap" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "3rd Party Home Page";
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
        <p>Welcome <sec:authentication property="principal.username"/>, this is the main page of this project.
            Above you may find different options to link this account to a TTTSP customer account</p>
    </div>
</main>
<%@include file="footer.jsp" %>