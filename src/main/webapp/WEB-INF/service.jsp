<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "OAuth Java Example - Available services";
%>
<%@include file="common/header.jsp" %>
<%@include file="common/menu.jsp" %>
<main role="main" class="container vh-90">
    <div class="row align-items-center h-100">
        <div class="col-10 mx-auto">
            <div class="jumbotron">
                <c:choose>
                    <c:when test="${hasRefreshToken}">
                        <h2>Revoke linked Webfleet Solutions service</h2>
                        <p class="lead">To revoke a refresh token use OAuth 2.0 Revoke Token (RFC 7009)</p>
                        <pre>
                            <!-- Keep formatting for highlighting-->
                            <code class="http">POST /uaa/oauth/revoke HTTP/1.1
Host: auth.webfleet.com
Authorization: Basic ...

token=${refresh_token}</code>
                        </pre>
                        <c:url value="<%=KnownUrls.REVOKE%>" var="revoke"/>
                        <a class="btn btn-primary btn-lg" href="${revoke}" role="button">Revoke</a>
                    </c:when>
                    <c:otherwise>
                        <h2>Link your account to a Webfleet Solutions account</h2>
                        <p class="lead">Clicking on the button below will ask to login into Webfleet Solutions</p>
                        <p>After a successful login you will be redirected to this site.</p>
                        <a class="btn btn-primary btn-lg" href="${authorizeUrl}" role="button">Link account</a>
                        <hr/>
                        <p class="lead">The above button simply takes the user-agent to the authorize url as defined in
                            <a href="https://tools.ietf.org/html/rfc6749#section-1.3.1">OAuth 2.0 (RFC 6749)</a>:</p>
                        <pre>
                            <code class="html" style="white-space: pre-wrap !important;">&lt;a href="${authorizeUrl}"&gt;Link account&lt;/a&gt;</code>
                        </pre>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
</main>
<%@include file="common/footer.jsp" %>