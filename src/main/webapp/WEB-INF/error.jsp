<%@ page import="com.webfleet.oauth.common.KnownUrls" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "OAuth Java Example";
%>
<%@include file="common/header.jsp" %>
<main role="main" class="container">
    <div>
        <p class="lead">Something went wrong, please try again later.</p>
        <p>${error}</p>
        <c:url value="<%=KnownUrls.HOME%>" var="home"/>
        <a href="${home}" class="button">Main page</a>
    </div>
</main>
<%@include file="common/footer.jsp" %>
