<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String title = "OAuth Java Example - Callback from Authserver";
%>
<%@include file="common/header.jsp" %>
<%@include file="common/menu.jsp" %>

<main role="main" class="container vh-90">
    <div class="row align-items-center h-100">
        <div class="col-10 mx-auto">
            <sec:authorize access="isAuthenticated()">
                <div class="jumbotron">
                    <h2>Account linked successfully</h2>
                    <p class="lead">Received authorization code and used it to exchange it for an access token with Webfleet Solutions Authorization
                        server.</p>
                    <pre>
                        <code class="http" style="white-space: pre-wrap">POST /uaa/oauth/token HTTP/1.1
Content-Type: application/x-www-form-urlencoded
Accept: application/json

grant_type=authorization_code&client_id=&lt;YOUR_CLIENT_ID&gt;&client_secret=&lt;YOUR_CLIENT_SECRET&gt;&code=&lt;AUTHORIZATION_CODE&gt;&redirect_uri=&lt;YOUR_REDIRECT_URI&gt;</code>
                    </pre>
                    <hr/>
                    <p>Webfleet Solutions Authorization server responded issuing a new access and refresh token pair.</p>
                    <pre><code class="json">${response}</code></pre>
                    <p>We can now use the received access token to request a protected resource from a Webfleet Solutions API</p>
                    <c:url value="<%=KnownUrls.CONSUME%>" var="consume"/>
                    <a class="btn btn-primary btn-lg" href="${consume}" role="button">Request API</a>
                </div>
            </sec:authorize>
        </div>
    </div>
</main>
<%@include file="common/footer.jsp" %>