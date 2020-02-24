<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%
    String title = "OAuth Java Example - Consume API";
%>
<%@include file="common/header.jsp" %>
<%@include file="common/menu.jsp" %>

<main role="main" class="container d-flex h-100">
    <div class="row align-self-center w-100">
        <div class="col-10 mx-auto">
            <div class="jumbotron">
                <h2>Webfleet Solutions API success</h2>
                <p class="lead">Webfleet solutions API responded successfully to the request using the obtained OAuth
                    access token.</p>
                <pre>
                    <code class="http">GET /api HTTP/1.1
Host: auth.webfleet.com
Authorization: Bearer ${access_token}</code>
                    <code class="json">${result}</code>
                </pre>
                <p>We can also revoke the refresh token which will invalidate our refresh token to issue new access
                    tokens.</p>
                <p>After this we will need to login in Webfleet Solutions with our customer credentials again to obtain
                    a new refresh token.</p>
                <c:url value="<%=KnownUrls.REVOKE%>" var="revoke"/>
                <a class="btn btn-primary btn-lg" href="${revoke}" role="button">Revoke token</a>
            </div>
        </div>
    </div>
</main>
<%@include file="common/footer.jsp" %>